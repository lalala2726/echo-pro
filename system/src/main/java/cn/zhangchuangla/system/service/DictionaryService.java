package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface DictionaryService extends IService<Dictionary> {


    boolean isNameExist(String name);

    void addDictionary(AddDictionaryRequest request);

    Dictionary getDictionaryById(Long id);

    boolean updateDictionaryById(UpdateDictionaryRequest request);

    void deleteDictionary(List<Long> ids);

    Page<Dictionary> getDictionaryList(DictionaryRequest request);
}
