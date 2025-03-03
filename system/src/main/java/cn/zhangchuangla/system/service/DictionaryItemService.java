package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.DictionaryItem;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryItemRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryItemRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryItemRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author chuang
 */
public interface DictionaryItemService extends IService<DictionaryItem> {

    /**
     * 根据字典编码和字典项键查询字典项是否存在
     *
     * @param itemKey 字典项键
     * @return 如果同一字典内字典项名称存在重复, 则返回true，否则返回false
     */
    boolean noDuplicateKeys(String itemKey);

    long getCountByDictionaryId(Long dictionaryId);

    Page<DictionaryItem> dictionaryItemList(DictionaryItemRequest request);

    List<DictionaryItem> getDictionaryItemByIdDictName(String dictionaryName);

    boolean addDictionaryItem(AddDictionaryItemRequest request);

    DictionaryItem getDictionaryById(Long id);

    boolean updateDictionaryItem(UpdateDictionaryItemRequest request);

    void deleteDictionaryItem(List<Long> ids);
}
