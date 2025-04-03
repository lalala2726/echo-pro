package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.DictionaryDataMapper;
import cn.zhangchuangla.system.model.entity.DictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryDataRequest;
import cn.zhangchuangla.system.service.DictionaryDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典内值服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
public class DictionaryDataServiceImpl extends ServiceImpl<DictionaryDataMapper, DictionaryData>
        implements DictionaryDataService {

    private final DictionaryDataMapper dictionaryDataMapper;

    @Autowired
    public DictionaryDataServiceImpl(DictionaryDataMapper dictionaryDataMapper) {
        this.dictionaryDataMapper = dictionaryDataMapper;
    }

    /**
     * 根据字典编码和字典项键查询字典项是否存在
     *
     * @param itemKey 字典项键
     * @return 如果同一字典内字典项名称存在重复, 则返回true，否则返回false
     */
    @Override
    public boolean noDuplicateKeys(String itemKey) {
        ParamsUtils.paramsNotIsNullOrBlank("字典项键不能为空", itemKey);
        LambdaQueryWrapper<DictionaryData> queryWrapper = new LambdaQueryWrapper<DictionaryData>()
                .eq(DictionaryData::getDataKey, itemKey);
        DictionaryData dictionaryData = getOne(queryWrapper);
        LambdaQueryWrapper<DictionaryData> eq = new LambdaQueryWrapper<DictionaryData>()
                .eq(DictionaryData::getDictionaryId, dictionaryData.getDictionaryId())
                .eq(DictionaryData::getDataKey, itemKey);
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
        LambdaQueryWrapper<DictionaryData> eq = new LambdaQueryWrapper<DictionaryData>().eq(DictionaryData::getDictionaryId, dictionaryId);
        return count(eq);
    }


    /**
     * 根据字典名称获取字典值
     *
     * @param dictionaryName 字典名称
     * @return 字典值列表
     */
    @Override
    public List<DictionaryData> getDictionaryDataByIdDictName(String dictionaryName) {
        ParamsUtils.paramsNotIsNullOrBlank("字典名称不能为空", dictionaryName);
        return dictionaryDataMapper.dictionaryDataService(dictionaryName);
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
        DictionaryData dictionaryData = new DictionaryData();
        BeanUtils.copyProperties(request, dictionaryData);
        return save(dictionaryData);
    }

    /**
     * 根据id获取字典项
     *
     * @param id 字典项id
     * @return 字典项
     */
    @Override
    public DictionaryData getDictionaryById(Long id) {
        ParamsUtils.minValidParam(id, "字典ID不能小于等于零!");
        LambdaQueryWrapper<DictionaryData> eq = new LambdaQueryWrapper<DictionaryData>().eq(DictionaryData::getId, id);
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
        DictionaryData dictionaryData = new DictionaryData();
        BeanUtils.copyProperties(request, dictionaryData);
        log.info("更新字典项:{}", dictionaryData);
        int result = dictionaryDataMapper.updateDictionaryDataByDictName(dictionaryData, request.getDictName());
        return result > 0;
    }

    /**
     * 删除字典项,支持批量删除
     *
     * @param ids 字典项ID
     */
    @Override
    public void deleteDictionaryData(List<Long> ids) {
        dictionaryDataMapper.deleteDictionaryItem(ids);
    }

    /**
     * 根据字典名称获取字典项
     *
     * @param id 字典ID
     * @return 字典项
     */
    @Override
    public Page<DictionaryData> getDictDataByDictionaryName(Long id, DictionaryDataRequest request) {
        ParamsUtils.minValidParam(id, "字典ID不能小于等于零!");
        Page<DictionaryData> dictionaryDataPage = new Page<>(request.getPageNum(), request.getPageSize());
        return dictionaryDataMapper.getDictDataByDictionaryName(dictionaryDataPage, id, request);
    }
}




