package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.DictionaryData;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryDataRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryDataRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author chuang
 */
public interface DictionaryDataService extends IService<DictionaryData> {

    /**
     * 根据字典编码和字典项键查询字典项是否存在
     *
     * @param itemKey 字典项键
     * @return 如果同一字典内字典项名称存在重复, 则返回true，否则返回false
     */
    boolean noDuplicateKeys(String itemKey);

    /**
     * 根据字典编码查询字典项数量
     *
     * @param dictionaryId 字典编码
     * @return 返回字典项数量
     */
    long getCountByDictionaryId(Long dictionaryId);

    /**
     * 字典值列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    Page<DictionaryData> dictionaryDataList(DictionaryDataRequest request);

    /**
     * 根据字典名称获取字典值
     *
     * @param dictionaryName 字典名称
     * @return 字典值列表
     */
    List<DictionaryData> getDictionaryDataByIdDictName(String dictionaryName);

    /**
     * 添加字典项
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addDictionaryData(AddDictionaryDataRequest request);

    /**
     * 根据id获取字典项
     *
     * @param id 字典项id
     * @return 字典项
     */
    DictionaryData getDictionaryById(Long id);

    /**
     * 更新字典项
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updateDictionaryData(UpdateDictionaryDataRequest request);

    /**
     * 删除字典项,支持批量删除
     *
     * @param ids 字典项ID
     */
    void deleteDictionaryData(List<Long> ids);
}
