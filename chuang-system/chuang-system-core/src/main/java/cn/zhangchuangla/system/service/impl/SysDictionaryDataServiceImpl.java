package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.converter.SysDictionaryDataConverter;
import cn.zhangchuangla.system.mapper.SysDictionaryDataMapper;
import cn.zhangchuangla.system.model.entity.SysDictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryDataRequest;
import cn.zhangchuangla.system.service.SysDictionaryDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典内值服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysDictionaryDataServiceImpl extends ServiceImpl<SysDictionaryDataMapper, SysDictionaryData>
        implements SysDictionaryDataService {

    private final SysDictionaryDataMapper sysDictionaryDataMapper;
    private final SysDictionaryDataConverter sysDictionaryDataConverter;


    /**
     * 根据字典编码和字典项键查询字典项是否存在
     *
     * @param itemKey 字典项键
     * @return 如果同一字典内字典项名称存在重复, 则返回true，否则返回false
     */
    @Override
    public boolean noDuplicateKeys(String itemKey) {
        ParamsUtils.paramsNotIsNullOrBlank("字典项键不能为空", itemKey);
        LambdaQueryWrapper<SysDictionaryData> queryWrapper = new LambdaQueryWrapper<SysDictionaryData>()
                .eq(SysDictionaryData::getDataKey, itemKey);
        SysDictionaryData sysDictionaryData = getOne(queryWrapper);
        LambdaQueryWrapper<SysDictionaryData> eq = new LambdaQueryWrapper<SysDictionaryData>()
                .eq(SysDictionaryData::getDictionaryId, sysDictionaryData.getDictionaryId())
                .eq(SysDictionaryData::getDataKey, itemKey);
        return count(eq) > 0;
    }

    /**
     * 根据字典编码查询字典项数量
     *
     * @param dictionaryId 字典编码
     * @return 返回字典项数量
     */
    @Override
    public long getCountByDictionaryId(Long dictionaryId) {
        ParamsUtils.minValidParam(dictionaryId, "字典ID不能小于等于零!");
        LambdaQueryWrapper<SysDictionaryData> eq = new LambdaQueryWrapper<SysDictionaryData>().eq(SysDictionaryData::getDictionaryId, dictionaryId);
        return count(eq);
    }


    /**
     * 根据字典名称获取字典值
     *
     * @param dictionaryName 字典名称
     * @return 字典值列表
     */
    @Override
    public List<SysDictionaryData> getDictionaryDataByIdDictName(String dictionaryName) {
        ParamsUtils.paramsNotIsNullOrBlank("字典名称不能为空", dictionaryName);
        return sysDictionaryDataMapper.dictionaryDataService(dictionaryName);
    }

    /**
     * 添加字典项
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean addDictionaryData(AddDictionaryDataRequest request) {
        if (noDuplicateKeys(request.getDataKey())) {
            throw new ServiceException("字典项键已存在!");
        }
        SysDictionaryData sysDictionaryData = sysDictionaryDataConverter.toEntity(request);
        return save(sysDictionaryData);
    }

    /**
     * 根据id获取字典项
     *
     * @param id 字典项id
     * @return 字典项
     */
    @Override
    public SysDictionaryData getDictionaryById(Long id) {
        ParamsUtils.minValidParam(id, "字典ID不能小于等于零!");
        LambdaQueryWrapper<SysDictionaryData> eq = new LambdaQueryWrapper<SysDictionaryData>().eq(SysDictionaryData::getId, id);
        return getOne(eq);
    }

    /**
     * 更新字典项
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateDictionaryData(UpdateDictionaryDataRequest request) {
        if (noDuplicateKeys(request.getDataKey())) {
            throw new ServiceException("字典项键已存在!");
        }
        SysDictionaryData sysDictionaryData = sysDictionaryDataConverter.toEntity(request);
        log.info("更新字典项:{}", sysDictionaryData);
        int result = sysDictionaryDataMapper.updateDictionaryDataByDictName(sysDictionaryData, request.getDictName());
        return result > 0;
    }

    /**
     * 删除字典项,支持批量删除
     *
     * @param ids 字典项ID
     */
    @Override
    public void deleteDictionaryData(List<Long> ids) {
        sysDictionaryDataMapper.deleteDictionaryItem(ids);
    }

    /**
     * 根据字典名称获取字典项
     *
     * @param id 字典ID
     * @return 字典项
     */
    @Override
    public Page<SysDictionaryData> getDictDataByDictionaryName(Long id, DictionaryDataRequest request) {
        ParamsUtils.minValidParam(id, "字典ID不能小于等于零!");
        Page<SysDictionaryData> dictionaryDataPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictionaryDataMapper.getDictDataByDictionaryName(dictionaryDataPage, id, request);
    }
}




