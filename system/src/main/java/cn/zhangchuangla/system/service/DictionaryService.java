package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhangchuang
 */
public interface DictionaryService extends IService<Dictionary> {


    boolean isNameExist(String name);

    void addDictionary(AddDictionaryRequest request);
}
