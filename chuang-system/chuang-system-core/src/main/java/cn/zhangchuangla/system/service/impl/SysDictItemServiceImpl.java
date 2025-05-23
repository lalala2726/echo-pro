package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.model.entity.Option;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.StringUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.mapper.SysDictItemMapper;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import cn.zhangchuangla.system.service.SysDictItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem>
        implements SysDictItemService {

    private final SysDictItemMapper sysDictItemMapper;
    private final RedisCache redisCache;

    /**
     * 获取字典项列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    @Override
    public Page<SysDictItem> listDictItem(Page<SysDictItem> page, String dictType, SysDictItemQueryRequest request) {
        return sysDictItemMapper.listDictItem(page, dictType, request);
    }

    /**
     * 根据id获取字典项
     *
     * @param id id
     * @return 字典项
     */
    @Override
    public SysDictItem getDictItemById(Long id) {
        return sysDictItemMapper.selectById(id);
    }

    /**
     * 根据字典类型编码获取字典项列表
     *
     * @param dictType 字典类型编码
     * @return 字典项列表
     */
    @Override
    public List<Option<String>> getDictItemOption(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            return List.of();
        }

        // 1. 优先从缓存中获取
        String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictType);
        List<Option<String>> cachedOptions = redisCache.getCacheObject(cacheKey);

        if (cachedOptions != null) {
            log.debug("从缓存中获取字典数据: {}", dictType);
            return cachedOptions;
        }

        // 2. 缓存中没有数据，从数据库查询
        log.debug("缓存中没有字典数据，从数据库查询: {}", dictType);
        LambdaQueryWrapper<SysDictItem> queryWrapper = new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictType, dictType)
                .eq(SysDictItem::getStatus, 0) // 只查询启用的字典项
                .orderByAsc(SysDictItem::getSort); // 按排序字段升序

        List<SysDictItem> dictItems = list(queryWrapper);
        List<Option<String>> options = dictItems.stream()
                .map(item -> new Option<>(item.getItemValue(), item.getItemLabel(), item.getTag()))
                .toList();

        // 3. 将查询结果缓存起来
        try {
            redisCache.setCacheObject(cacheKey, options, RedisConstants.DICT_CACHE_EXPIRE_TIME);
            log.debug("字典数据已缓存: {}", dictType);
        } catch (Exception e) {
            log.warn("缓存字典数据失败: {}, 错误: {}", dictType, e.getMessage());
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
    public boolean addDictItem(SysDictItemAddRequest request) {
        // 检查同一字典类型下字典项值是否重复
        LambdaQueryWrapper<SysDictItem> eq = new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictType, request.getDictType())
                .eq(SysDictItem::getItemValue, request.getItemValue());
        if (sysDictItemMapper.selectCount(eq) > 0) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "同一字典类型下字典项值不能重复: " + request.getItemValue());
        }
        SysDictItem sysDictItem = new SysDictItem();
        BeanUtils.copyProperties(request, sysDictItem);
        sysDictItem.setCreateBy(SecurityUtils.getUsername());

        boolean result = save(sysDictItem);
        if (result) {
            // 清除相关缓存
            clearDictCache(request.getDictType());
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
    public boolean updateDictItem(SysDictItemUpdateRequest request) {
        // 检查字典项是否存在
        SysDictItem existDictItem = sysDictItemMapper.selectById(request.getId());
        if (existDictItem == null) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "字典项不存在");
        }

        // 检查同一字典类型下字典项值是否重复 (排除自身)
        if (isDictItemValueExist(request.getDictType(), request.getItemValue(), request.getId())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "同一字典类型下字典项值不能重复: " + request.getItemValue());
        }

        SysDictItem sysDictItem = new SysDictItem();
        BeanUtils.copyProperties(request, sysDictItem);
        sysDictItem.setUpdateBy(SecurityUtils.getUsername());

        boolean result = sysDictItemMapper.updateById(sysDictItem) > 0;
        if (result) {
            // 清除相关缓存
            clearDictCache(request.getDictType());
            // 如果字典类型发生了变化，也要清除旧的缓存
            if (!request.getDictType().equals(existDictItem.getDictType())) {
                clearDictCache(existDictItem.getDictType());
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
    public boolean deleteDictItem(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 先查询要删除的字典项，以便清除相关缓存
        List<SysDictItem> itemsToDelete = sysDictItemMapper.selectByIds(ids);

        boolean result = sysDictItemMapper.deleteByIds(ids) > 0;
        if (result) {
            // 清除相关缓存
            itemsToDelete.stream()
                    .map(SysDictItem::getDictType)
                    .distinct()
                    .forEach(this::clearDictCache);
        }
        return result;
    }

    /**
     * 根据字典类型编码删除字典项
     *
     * @param dictTypes 字典类型编码列表
     */
    @Override
    public void deleteDictItemByDictType(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SysDictItem> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysDictItem::getDictType, dictTypes);
        // 返回删除的记录数是否大于0
        sysDictItemMapper.delete(queryWrapper);
    }

    /**
     * 根据旧的字典类型编码更新为新的字典类型编码
     *
     * @param oldDictType 旧字典类型编码
     * @param newDictType 新字典类型编码
     */
    @Override
    public void updateDictItemDictType(String oldDictType, String newDictType) {
        if (StringUtils.isBlank(oldDictType, newDictType) || oldDictType.equals(newDictType)) {
            return;
        }
        LambdaUpdateWrapper<SysDictItem> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(SysDictItem::getDictType, oldDictType)
                .set(SysDictItem::getDictType, newDictType);

        // 返回影响的行数是否大于0
        update(updateWrapper);
    }

    /**
     * 检查同一字典类型下字典项值是否重复
     *
     * @param dictType  字典类型编码
     * @param itemValue 字典项值
     * @param itemId    字典项ID (更新时排除自身)
     * @return true 重复, false 不重复
     */
    @Override
    public boolean isDictItemValueExist(String dictType, String itemValue, Long itemId) {
        if (StringUtils.isBlank(dictType, itemValue)) {
            // 关键参数为空，无法判断，或者认为不重复
            return false;
        }
        LambdaQueryWrapper<SysDictItem> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictItem::getDictType, dictType)
                .eq(SysDictItem::getItemValue, itemValue);
        // 如果是更新操作，排除当前项自身
        if (itemId != null) {
            queryWrapper.ne(SysDictItem::getId, itemId);
        }
        return sysDictItemMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 清除指定字典类型的缓存
     */
    private void clearDictCache(String dictType) {
        if (!StringUtils.isBlank(dictType)) {
            String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictType);
            try {
                redisCache.deleteObject(cacheKey);
                log.debug("清除字典缓存: {}", dictType);
            } catch (Exception e) {
                log.warn("清除字典缓存失败: {}, 错误: {}", dictType, e.getMessage());
            }
        }
    }
}




