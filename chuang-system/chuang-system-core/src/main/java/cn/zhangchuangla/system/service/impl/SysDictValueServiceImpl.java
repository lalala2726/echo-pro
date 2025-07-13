package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.StrUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.mapper.SysDictValueMapper;
import cn.zhangchuangla.system.model.entity.SysDictValue;
import cn.zhangchuangla.system.model.request.dict.SysDictValueAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictValueQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictValueUpdateRequest;
import cn.zhangchuangla.system.service.SysDictValueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典项 服务实现层
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysDictValueServiceImpl extends ServiceImpl<SysDictValueMapper, SysDictValue>
        implements SysDictValueService {

    private final SysDictValueMapper sysDictValueMapper;
    private final RedisCache redisCache;

    /**
     * 获取字典项列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    @Override
    public Page<SysDictValue> listDictValue(Page<SysDictValue> page, String dictKey, SysDictValueQueryRequest request) {
        return sysDictValueMapper.listDictValue(page, dictKey, request);
    }

    /**
     * 根据id获取字典项
     *
     * @param id id
     * @return 字典项
     */
    @Override
    public SysDictValue getDictValueById(Long id) {
        return sysDictValueMapper.selectById(id);
    }

    /**
     * 根据字典类型编码获取字典项列表
     *
     * @param dictKey 字典类型编码
     * @return 字典项列表
     */
    @Override
    public List<Option<String>> getDictValueOption(String dictKey) {
        if (StrUtils.isBlank(dictKey)) {
            return List.of();
        }

        // 1. 优先从缓存中获取
        String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictKey);
        List<Option<String>> cachedOptions = redisCache.getCacheObject(cacheKey);

        if (cachedOptions != null) {
            log.debug("从缓存中获取字典数据: {}", dictKey);
            return cachedOptions;
        }

        // 2. 缓存中没有数据，从数据库查询
        log.debug("缓存中没有字典数据，从数据库查询: {}", dictKey);
        LambdaQueryWrapper<SysDictValue> queryWrapper = new LambdaQueryWrapper<SysDictValue>()
                .eq(SysDictValue::getDictKey, dictKey)
                // 只查询启用的字典项
                .eq(SysDictValue::getStatus, 0)
                // 按排序字段升序
                .orderByAsc(SysDictValue::getSort);

        List<SysDictValue> dictValues = list(queryWrapper);
        List<Option<String>> options = dictValues.stream()
                .map(item -> new Option<>(item.getValue(), item.getLabel(), item.getTag()))
                .toList();

        // 3. 将查询结果缓存起来
        try {
            redisCache.setCacheObject(cacheKey, options, RedisConstants.DICT_CACHE_EXPIRE_TIME);
            log.debug("字典数据已缓存: {}", dictKey);
        } catch (Exception e) {
            log.warn("缓存字典数据失败: {}, 错误: {}", dictKey, e.getMessage());
        }

        return options;
    }

    /**
     * 添加字典项
     *
     * @param request 请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictValue(SysDictValueAddRequest request) {
        // 检查同一字典类型下字典项值是否重复
        LambdaQueryWrapper<SysDictValue> eq = new LambdaQueryWrapper<SysDictValue>()
                .eq(SysDictValue::getDictKey, request.getDictKey())
                .eq(SysDictValue::getValue, request.getValue());
        if (sysDictValueMapper.selectCount(eq) > 0) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "同一字典类型下字典项值不能重复: " + request.getValue());
        }
        SysDictValue sysDictValue = new SysDictValue();
        BeanUtils.copyProperties(request, sysDictValue);
        sysDictValue.setCreateBy(SecurityUtils.getUsername());

        boolean result = save(sysDictValue);
        if (result) {
            // 清除相关缓存
            clearDictCache(request.getDictKey());
        }
        return result;
    }

    /**
     * 更新字典项
     *
     * @param request 请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateDictValue(SysDictValueUpdateRequest request) {
        // 检查字典项是否存在
        SysDictValue existDictValue = sysDictValueMapper.selectById(request.getId());
        if (existDictValue == null) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典项不存在");
        }

        // 检查同一字典类型下字典项值是否重复 (排除自身)
        if (isDictValueExistByValue(request.getDictKey(), request.getValue(), request.getId())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "同一字典类型下字典项值不能重复: " + request.getValue());
        }

        SysDictValue sysDictValue = new SysDictValue();
        BeanUtils.copyProperties(request, sysDictValue);
        sysDictValue.setUpdateBy(SecurityUtils.getUsername());

        boolean result = sysDictValueMapper.updateById(sysDictValue) > 0;
        if (result) {
            // 清除相关缓存
            clearDictCache(request.getDictKey());
            // 如果字典类型发生了变化，也要清除旧的缓存
            if (!request.getDictKey().equals(existDictValue.getDictKey())) {
                clearDictCache(existDictValue.getDictKey());
            }
        }
        return result;
    }

    /**
     * 删除字典项
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDictValue(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 先查询要删除的字典项，以便清除相关缓存
        List<SysDictValue> itemsToDelete = sysDictValueMapper.selectByIds(ids);

        boolean result = sysDictValueMapper.deleteByIds(ids) > 0;
        if (result) {
            // 清除相关缓存
            itemsToDelete.stream()
                    .map(SysDictValue::getDictKey)
                    .distinct()
                    .forEach(this::clearDictCache);
        }
        return result;
    }

    /**
     * 根据字典类型编码删除字典项
     *
     * @param dictKeys 字典类型编码列表
     */
    @Override
    public void deleteDictValueByDictKey(List<String> dictKeys) {
        if (dictKeys == null || dictKeys.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SysDictValue> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysDictValue::getDictKey, dictKeys);
        // 返回删除的记录数是否大于0
        sysDictValueMapper.delete(queryWrapper);
    }

    /**
     * 检查同一字典类型下字典项值是否重复
     *
     * @param dictKey   字典类型编码
     * @param itemValue 字典项值
     * @param itemId    字典项ID (更新时排除自身)
     * @return true 重复, false 不重复
     */
    @Override
    public boolean isDictValueExistByValue(String dictKey, String itemValue, Long itemId) {
        if (StrUtils.isBlank(dictKey, itemValue)) {
            // 关键参数为空，无法判断，或者认为不重复
            return false;
        }
        LambdaQueryWrapper<SysDictValue> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictValue::getDictKey, dictKey)
                .eq(SysDictValue::getValue, itemValue);
        // 如果是更新操作，排除当前项自身
        if (itemId != null) {
            queryWrapper.ne(SysDictValue::getId, itemId);
        }
        return sysDictValueMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 清除指定字典类型的缓存
     */
    private void clearDictCache(String dictKey) {
        if (!StrUtils.isBlank(dictKey)) {
            String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictKey);
            try {
                redisCache.deleteObject(cacheKey);
            } catch (Exception e) {
                log.warn("清除字典缓存失败: {}, 错误: {}", dictKey, e.getMessage());
            }
        }
    }
}




