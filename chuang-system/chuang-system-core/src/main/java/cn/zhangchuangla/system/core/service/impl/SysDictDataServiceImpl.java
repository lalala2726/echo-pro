package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.core.mapper.SysDictDataMapper;
import cn.zhangchuangla.system.core.model.entity.SysDictData;
import cn.zhangchuangla.system.core.model.request.dict.SysDictDataAddRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictDataQueryRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictDataUpdateRequest;
import cn.zhangchuangla.system.core.service.SysDictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据 Service 实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "dictDataOptions")
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
        implements SysDictDataService {

    private final SysDictDataMapper sysDictDataMapper;
    private final CacheManager cacheManager;
    private final RedisCache redisCache;

    /**
     * 分页查询字典数据列表
     *
     * @param dictType 字典类型
     * @param request  查询条件
     * @return 字典数据分页列表
     */
    @Override
    public Page<SysDictData> listDictData(String dictType, SysDictDataQueryRequest request) {
        Page<SysDictData> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictDataMapper.listDictData(page, dictType, request);
    }

    /**
     * 根据ID获取字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据信息
     */
    @Override
    public SysDictData getDictDataById(Long id) {
        return getById(id);
    }

    /**
     * 根据字典类型获取字典数据选项
     *
     * @param dictType 字典类型
     * @return 字典数据选项列表
     */
    @Override
    public List<Option<String>> getDictDataOption(String dictType) {
        final int normal = 0;
        if (StringUtils.isBlank(dictType)) {
            return List.of();
        }

        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, normal)
                .orderByAsc(SysDictData::getSort);

        List<SysDictData> dictDataList = list(queryWrapper);
        return dictDataList.stream()
                .map(item -> new Option<>(item.getDictValue(), item.getDictLabel()))
                .toList();
    }

    /**
     * 添加字典数据
     *
     * @param request 添加请求
     * @return 是否添加成功
     */
    @Override
    public boolean addDictData(SysDictDataAddRequest request) {
        // 检查同一字典类型下字典值是否重复
        if (isDictDataExistByValue(request.getDictType(), request.getDictValue(), null)) {
            throw new ServiceException(ResultCode.OPERATION_ERROR,
                    "同一字典类型下字典值不能重复: " + request.getDictValue());
        }

        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictData.setCreateBy(SecurityUtils.getUsername());

        sysDictData.setCreateBy(SecurityUtils.getUsername());
        return save(sysDictData);
    }

    /**
     * 更新字典数据
     *
     * @param request 更新请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateDictData(SysDictDataUpdateRequest request) {
        // 检查字典数据是否存在
        SysDictData existDictData = getById(request.getId());
        if (existDictData == null) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "字典数据不存在");
        }

        // 检查同一字典类型下字典值是否重复 (排除自身)
        if (isDictDataExistByValue(request.getDictType(), request.getDictValue(), request.getId())) {
            throw new ServiceException(ResultCode.OPERATION_ERROR,
                    "同一字典类型下字典值不能重复: " + request.getDictValue());
        }

        SysDictData sysDictData = new SysDictData();
        BeanUtils.copyProperties(request, sysDictData);
        sysDictData.setUpdateBy(SecurityUtils.getUsername());
        sysDictData.setUpdateBy(SecurityUtils.getUsername());
        boolean result = updateById(sysDictData);
        if (result) {
            evictDictOptions(request.getDictType());
            if (!request.getDictType().equals(existDictData.getDictType())) {
                evictDictOptions(existDictData.getDictType());
            }
        }
        return result;
    }

    /**
     * 删除字典数据
     *
     * @param ids 字典数据ID列表
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDictData(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 先查询要删除的字典数据，以便清除相关缓存
        List<SysDictData> itemsToDelete = listByIds(ids);

        boolean result = removeByIds(ids);
        if (result) {
            itemsToDelete.stream()
                    .map(SysDictData::getDictType)
                    .distinct()
                    .forEach(this::evictDictOptions);
        }
        return result;
    }

    /**
     * 根据字典类型删除字典数据
     *
     * @param dictTypes 字典类型列表
     */
    @Override
    public void deleteDictDataByDictType(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<SysDictData> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysDictData::getDictType, dictTypes);
        remove(queryWrapper);

        dictTypes.forEach(this::evictDictOptions);
    }

    /**
     * 检查字典数据值是否存在
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param dataId    字典数据ID（更新时排除自身）
     * @return 是否存在
     */
    @Override
    public boolean isDictDataExistByValue(String dictType, String dictValue, Long dataId) {
        if (StringUtils.isAllBlank(dictType, dictValue)) {
            return false;
        }
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getDictValue, dictValue);
        // 如果是更新操作，排除当前数据自身
        if (dataId != null) {
            queryWrapper.ne(SysDictData::getId, dataId);
        }
        return count(queryWrapper) > 0;
    }

    /**
     * 清除指定字典类型的 Spring Cache 缓存
     */
    private void evictDictOptions(String dictType) {
        if (!StringUtils.isBlank(dictType)) {
            try {
                var cache = cacheManager.getCache("dictDataOptions");
                if (cache != null) {
                    cache.evict(dictType);
                }
                // 同步删除旧的手工Redis键，兼容 Excel 等读取逻辑
                String manualKey = String.format(RedisConstants.Dict.DICT_DATA_KEY, dictType);
                redisCache.deleteObject(manualKey);
            } catch (Exception e) {
                log.warn("清除字典缓存失败: {}, 错误: {}", dictType, e.getMessage());
            }
        }
    }
}
