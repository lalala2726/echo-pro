package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.StrUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.mapper.SysDictTypeMapper;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeUpdateRequest;
import cn.zhangchuangla.system.service.SysDictItemService;
import cn.zhangchuangla.system.service.SysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictItemService sysDictItemService;
    private final RedisCache redisCache;

    /**
     * 获取字典类型列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    @Override
    public Page<SysDictType> listDictType(Page<SysDictType> page, SysDictTypeQueryRequest request) {
        return dictTypeMapper.listDictType(page, request);
    }

    /**
     * 根据id获取字典类型
     *
     * @param id id
     * @return 字典类型
     */
    @Override
    public SysDictType getDictTypeById(Long id) {
        return getById(id);
    }

    /**
     * 添加字典类型
     *
     * @param request 请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictType(SysDictTypeAddRequest request) {
        if (isDictTypeExist(request.getDictType())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典类型已存在: " + request.getDictType());
        }
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictType.setCreateBy(SecurityUtils.getUsername());
        return save(sysDictType);
    }

    /**
     * 更新字典类型
     *
     * @param request 请求
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictTypeUpdateRequest request) {
        LambdaQueryWrapper<SysDictType> ne = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, request.getDictType())
                .ne(SysDictType::getId, request.getId());
        if (count(ne) > 0) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典类型已存在: " + request.getDictType());
        }
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictType.setUpdateBy(SecurityUtils.getUsername());
        return updateById(sysDictType);
    }

    /**
     * 删除字典类型
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        List<SysDictType> sysDictTypes = listByIds(ids);
        if (sysDictTypes.isEmpty()) {
            return false;
        }

        List<String> dictTypes = sysDictTypes.stream()
                .map(SysDictType::getDictType)
                .distinct()
                .toList();

        if (!dictTypes.isEmpty()) {
            sysDictItemService.deleteDictItemByDictType(dictTypes);
        }

        return removeByIds(ids);
    }

    /**
     * 判断字典类型是否存在
     *
     * @param dictType 字典类型
     * @return true存在，false不存在
     */
    @Override
    public boolean isDictTypeExist(String dictType) {
        if (StrUtils.isBlank(dictType)) {
            return false;
        }
        LambdaQueryWrapper<SysDictType> eq = new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dictType);
        return count(eq) > 0;
    }

    /**
     * 刷新字典缓存
     *
     * @return 操作结果
     */
    @Override
    public boolean refreshCache() {
        try {
            log.info("开始刷新字典缓存...");

            // 1. 清除现有的字典缓存
            Collection<String> existingKeys = redisCache.keys(RedisConstants.DICT_CACHE + "*");
            if (!existingKeys.isEmpty()) {
                long deletedCount = redisCache.deleteObject(existingKeys);
                log.info("清除了 {} 个现有字典缓存", deletedCount);
            }

            // 2. 重新加载所有字典数据到缓存
            LambdaQueryWrapper<SysDictType> dictTypeWrapper = new LambdaQueryWrapper<SysDictType>()
                    .eq(SysDictType::getStatus, 0);
            List<SysDictType> dictTypes = list(dictTypeWrapper);

            if (dictTypes.isEmpty()) {
                log.info("没有找到启用的字典类型");
                return true;
            }

            int successCount = 0;
            int failCount = 0;

            // 3. 为每个字典类型加载字典项到缓存
            for (SysDictType dictType : dictTypes) {
                try {
                    // 查询该字典类型下所有启用的字典项
                    LambdaQueryWrapper<SysDictItem> dictItemWrapper = new LambdaQueryWrapper<SysDictItem>()
                            .eq(SysDictItem::getDictType, dictType.getDictType())
                            .eq(SysDictItem::getStatus, 0)
                            .orderByAsc(SysDictItem::getSort);

                    List<SysDictItem> dictItems = sysDictItemService.list(dictItemWrapper);

                    // 转换为Option格式
                    List<Option<String>> options = dictItems.stream()
                            .map(item -> new Option<>(item.getItemValue(), item.getItemLabel(), item.getTag()))
                            .toList();

                    // 将数据放入缓存
                    String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictType.getDictType());
                    redisCache.setCacheObject(cacheKey, options, RedisConstants.DICT_CACHE_EXPIRE_TIME);

                    successCount++;
                    log.debug("字典类型 [{}] 缓存刷新成功，共 {} 个字典项", dictType.getDictType(), dictItems.size());

                } catch (Exception e) {
                    failCount++;
                    log.error("字典类型 [{}] 缓存刷新失败: {}", dictType.getDictType(), e.getMessage(), e);
                }
            }

            log.info("字典缓存刷新完成，成功: {} 个，失败: {} 个", successCount, failCount);
            // 只有当没有失败的时候才返回true
            return failCount == 0;

        } catch (Exception e) {
            log.error("刷新字典缓存失败", e);
            return false;
        }
    }

    /**
     * 获取所有字典类型
     *
     * @return 字典类型列表
     */
    @Override
    public List<Option<String>> getAllDictType() {
        LambdaQueryWrapper<SysDictType> eq = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getStatus, Constants.DictConstants.ENABLE_STATUS);
        List<SysDictType> list = list(eq);
        return list.stream()
                .map(item -> new Option<>(item.getDictType(), item.getDictName()))
                .toList();
    }

}




