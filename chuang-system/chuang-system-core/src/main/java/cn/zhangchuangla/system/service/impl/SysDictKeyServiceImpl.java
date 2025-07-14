package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.StrUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.mapper.SysDictKeyMapper;
import cn.zhangchuangla.system.model.entity.SysDictKey;
import cn.zhangchuangla.system.model.entity.SysDictValue;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictKeyUpdateRequest;
import cn.zhangchuangla.system.service.SysDictKeyService;
import cn.zhangchuangla.system.service.SysDictValueService;
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
public class SysDictKeyServiceImpl extends ServiceImpl<SysDictKeyMapper, SysDictKey>
        implements SysDictKeyService {

    private final SysDictKeyMapper dictKeyMapper;
    private final SysDictValueService sysDictValueService;
    private final RedisCache redisCache;

    /**
     * 获取字典键列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    @Override
    public Page<SysDictKey> listDictKey(Page<SysDictKey> page, SysDictKeyQueryRequest request) {
        return dictKeyMapper.listDictKey(page, request);
    }

    /**
     * 根据id获取字典键
     *
     * @param id id
     * @return 字典键
     */
    @Override
    public SysDictKey getDictKeyById(Long id) {
        return getById(id);
    }

    /**
     * 添加字典键
     *
     * @param request 请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictKey(SysDictKeyAddRequest request) {
        if (isDictKeyExist(request.getDictKey())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典键已存在: " + request.getDictKey());
        }
        SysDictKey sysDictKey = new SysDictKey();
        BeanUtils.copyProperties(request, sysDictKey);
        sysDictKey.setCreateBy(SecurityUtils.getUsername());
        return save(sysDictKey);
    }

    /**
     * 更新字典键
     *
     * @param request 请求
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictKey(SysDictKeyUpdateRequest request) {
        LambdaQueryWrapper<SysDictKey> ne = new LambdaQueryWrapper<SysDictKey>()
                .eq(SysDictKey::getDictKey, request.getDictKey())
                .ne(SysDictKey::getId, request.getId());
        if (count(ne) > 0) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典键已存在: " + request.getDictKey());
        }
        SysDictKey sysDictKey = new SysDictKey();
        BeanUtils.copyProperties(request, sysDictKey);
        sysDictKey.setUpdateBy(SecurityUtils.getUsername());
        return updateById(sysDictKey);
    }

    /**
     * 删除字典键
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictKey(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        List<SysDictKey> sysDictKeys = listByIds(ids);
        if (sysDictKeys.isEmpty()) {
            return false;
        }

        List<String> dictKeys = sysDictKeys.stream()
                .map(SysDictKey::getDictKey)
                .distinct()
                .toList();

        if (!dictKeys.isEmpty()) {
            sysDictValueService.deleteDictValueByDictKey(dictKeys);
        }

        return removeByIds(ids);
    }

    /**
     * 判断字典键是否存在
     *
     * @param dictKey 字典键
     * @return true存在，false不存在
     */
    @Override
    public boolean isDictKeyExist(String dictKey) {
        if (StrUtils.isBlank(dictKey)) {
            return false;
        }
        LambdaQueryWrapper<SysDictKey> eq = new LambdaQueryWrapper<SysDictKey>().eq(SysDictKey::getDictKey, dictKey);
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
            LambdaQueryWrapper<SysDictKey> dictKeyWrapper = new LambdaQueryWrapper<SysDictKey>()
                    .eq(SysDictKey::getStatus, Constants.SystemStatus.NORMAL);
            List<SysDictKey> dictKeys = list(dictKeyWrapper);

            if (dictKeys.isEmpty()) {
                log.info("没有找到启用的字典键");
                return true;
            }

            int successCount = 0;
            int failCount = 0;

            // 3. 为每个字典键加载字典项到缓存
            for (SysDictKey dictKey : dictKeys) {
                try {
                    // 查询该字典键下所有启用的字典项
                    LambdaQueryWrapper<SysDictValue> dictValueWrapper = new LambdaQueryWrapper<SysDictValue>()
                            .eq(SysDictValue::getDictKey, dictKey.getDictKey())
                            .eq(SysDictValue::getStatus, Constants.SystemStatus.NORMAL)
                            .orderByAsc(SysDictValue::getSort);

                    List<SysDictValue> dictValues = sysDictValueService.list(dictValueWrapper);

                    // 转换为Option格式
                    List<Option<String>> options = dictValues.stream()
                            .map(item -> new Option<>(item.getValue(), item.getLabel(), item.getTag()))
                            .toList();

                    // 将数据放入缓存
                    String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictKey.getDictKey());
                    redisCache.setCacheObject(cacheKey, options, RedisConstants.DICT_CACHE_EXPIRE_TIME);

                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    log.error("字典键 [{}] 缓存刷新失败: {}", dictKey.getDictKey(), e.getMessage(), e);
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
     * 获取所有字典键
     *
     * @return 字典键列表
     */
    @Override
    public List<Option<String>> getAllDictKey() {
        LambdaQueryWrapper<SysDictKey> eq = new LambdaQueryWrapper<SysDictKey>()
                .eq(SysDictKey::getStatus, Constants.DictConstants.ENABLE_STATUS);
        List<SysDictKey> list = list(eq);
        return list.stream()
                .map(item -> new Option<>(item.getDictKey(), item.getDictName()))
                .toList();
    }

}




