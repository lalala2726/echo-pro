package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.core.mapper.SysDictTypeMapper;
import cn.zhangchuangla.system.core.model.entity.SysDictData;
import cn.zhangchuangla.system.core.model.entity.SysDictType;
import cn.zhangchuangla.system.core.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictTypeQueryRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictTypeUpdateRequest;
import cn.zhangchuangla.system.core.service.SysDictDataService;
import cn.zhangchuangla.system.core.service.SysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典类型 Service 实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;
    private final SysDictDataService sysDictDataService;
    private final RedisCache redisCache;

    /**
     * 分页查询字典类型列表
     *
     * @param request 查询条件
     * @return 字典类型分页列表
     */
    @Override
    public Page<SysDictType> listDictType(SysDictTypeQueryRequest request) {
        Page<SysDictType> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictTypeMapper.listDictType(page, request);
    }

    /**
     * 根据ID获取字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型信息
     */
    @Override
    public SysDictType getDictTypeById(Long id) {
        return getById(id);
    }

    /**
     * 添加字典类型
     *
     * @param request 添加请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictType(SysDictTypeAddRequest request) {
        if (isDictTypeExist(request.getDictType())) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "字典类型已存在: " + request.getDictType());
        }
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictType.setCreateBy(SecurityUtils.getUsername());
        sysDictType.setCreateBy(SecurityUtils.getUsername());
        return save(sysDictType);
    }

    /**
     * 更新字典类型
     *
     * @param request 更新请求
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictTypeUpdateRequest request) {
        // 检查字典类型是否重复（排除自身）
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, request.getDictType())
                .ne(SysDictType::getId, request.getId());
        if (count(queryWrapper) > 0) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "字典类型已存在: " + request.getDictType());
        }

        SysDictType dictType = getById(request.getId());
        if (dictType == null) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "字典类型不存在");
        }
        SysDictType sysDictType = new SysDictType();
        BeanUtils.copyProperties(request, sysDictType);
        sysDictType.setUpdateBy(SecurityUtils.getUsername());
        sysDictType.setUpdateBy(SecurityUtils.getUsername());
        return updateById(sysDictType);
    }

    /**
     * 删除字典类型
     *
     * @param ids 字典类型ID列表
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictType(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 获取要删除的字典类型信息
        List<SysDictType> sysDictTypes = listByIds(ids);
        if (sysDictTypes.isEmpty()) {
            return false;
        }

        // 提取字典类型
        List<String> dictTypes = sysDictTypes.stream()
                .map(SysDictType::getDictType)
                .distinct()
                .toList();

        // 删除相关的字典数据
        if (!dictTypes.isEmpty()) {
            sysDictDataService.deleteDictDataByDictType(dictTypes);
        }

        // 删除字典类型
        boolean result = removeByIds(ids);

        // 清除相关缓存
        if (result) {
            dictTypes.forEach(this::clearDictCache);
        }

        return result;
    }

    /**
     * 检查字典类型是否存在
     *
     * @param dictType 字典类型
     * @return 是否存在
     */
    @Override
    public boolean isDictTypeExist(String dictType) {
        if (StringUtil.isBlank(dictType)) {
            return false;
        }
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getDictType, dictType);
        return count(queryWrapper) > 0;
    }

    /**
     * 获取所有字典类型选项
     *
     * @return 字典类型选项列表
     */
    @Override
    public List<Option<String>> getAllDictType() {
        LambdaQueryWrapper<SysDictType> queryWrapper = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getStatus, 0)
                .orderByDesc(SysDictType::getCreateTime);
        List<SysDictType> list = list(queryWrapper);
        return list.stream()
                .map(item -> new Option<>(item.getDictType(), item.getDictName()))
                .toList();
    }

    /**
     * 刷新字典缓存
     *
     * @return 是否刷新成功
     */
    @Override
    public boolean refreshCache() {
        try {
            log.info("开始刷新字典缓存...");

            // 1. 清除现有的字典缓存（使用 SCAN 替代 KEYS，避免阻塞）
            List<String> existingKeys = redisCache.scanKeys(RedisConstants.Dict.DICT_CACHE_PREFIX + "*");
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

            // 3. 为每个字典类型加载字典数据到缓存
            for (SysDictType dictType : dictTypes) {
                try {
                    // 查询该字典类型下所有启用的字典数据
                    LambdaQueryWrapper<SysDictData> dictDataWrapper = new LambdaQueryWrapper<SysDictData>()
                            .eq(SysDictData::getDictType, dictType.getDictType())
                            .eq(SysDictData::getStatus, 1)
                            .orderByAsc(SysDictData::getSort);

                    List<SysDictData> dictDataList = sysDictDataService.list(dictDataWrapper);

                    // 转换为Option格式
                    List<Option<String>> options = dictDataList.stream()
                            .map(item -> new Option<>(item.getDictValue(), item.getDictLabel()))
                            .toList();

                    // 将数据放入缓存
                    String cacheKey = String.format(RedisConstants.Dict.DICT_DATA_KEY, dictType.getDictType());
                    redisCache.setCacheObject(cacheKey, options, RedisConstants.Dict.DICT_CACHE_EXPIRE_TIME);

                    successCount++;

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
     * 清除指定字典类型的缓存
     */
    private void clearDictCache(String dictType) {
        if (!StringUtils.isBlank(dictType)) {
            String cacheKey = String.format(RedisConstants.Dict.DICT_DATA_KEY, dictType);
            try {
                redisCache.deleteObject(cacheKey);
                log.debug("已清除字典缓存: {}", dictType);
            } catch (Exception e) {
                log.warn("清除字典缓存失败: {}, 错误: {}", dictType, e.getMessage());
            }
        }
    }
}
