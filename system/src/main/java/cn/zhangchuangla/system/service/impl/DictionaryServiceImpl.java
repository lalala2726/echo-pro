package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.DictionaryMapper;
import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.service.DictionaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhangchuang
 */
@Service
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary>
        implements DictionaryService {


    @Override
    public boolean isNameExist(String name) {
        LambdaQueryWrapper<Dictionary> eq = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getName, name);
        return this.count(eq) > 0;
    }

    @Override
    public void addDictionary(AddDictionaryRequest request) {
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(request, dictionary);
        save(dictionary);
    }
}




