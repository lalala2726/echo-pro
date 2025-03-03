package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.mapper.DictionaryMapper;
import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryRequest;
import cn.zhangchuangla.system.service.DictionaryItemService;
import cn.zhangchuangla.system.service.DictionaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
@Slf4j
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary>
        implements DictionaryService {

    private final DictionaryItemService dictionaryItemService;
    private final DictionaryMapper dictionaryMapper;

    public DictionaryServiceImpl(DictionaryItemService dictionaryItemService, DictionaryMapper dictionaryMapper) {
        this.dictionaryItemService = dictionaryItemService;
        this.dictionaryMapper = dictionaryMapper;
    }


    @Override
    public Page<Dictionary> getDictionaryList(DictionaryRequest request) {
        Page<Dictionary> dictionaryPage = new Page<>(request.getPageNum(), request.getPageSize());
        return dictionaryMapper.getDictionaryList(dictionaryPage, request);
    }


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

    @Override
    public Dictionary getDictionaryById(Long id) {
        LambdaQueryWrapper<Dictionary> eq = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getId, id);
        return getOne(eq);
    }

    @Override
    public boolean updateDictionaryById(UpdateDictionaryRequest request) {
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(request, dictionary);
        LambdaQueryWrapper<Dictionary> eq = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getId, request.getId());
        return update(dictionary, eq);
    }

    @Override
    @Transactional
    public void deleteDictionary(List<Long> ids) {
        ids.forEach(id -> {
            long countByDictionaryId = dictionaryItemService.getCountByDictionaryId(id);
            if (countByDictionaryId > 0) {
                throw new ServiceException("该字典下有字典项，不能删除");
            }
        });
        removeByIds(ids);
    }


}




