package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.mapper.DictionaryMapper;
import cn.zhangchuangla.system.model.entity.Dictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryRequest;
import cn.zhangchuangla.system.service.DictionaryDataService;
import cn.zhangchuangla.system.service.DictionaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final DictionaryDataService dictionaryDataService;
    private final DictionaryMapper dictionaryMapper;

    @Autowired
    public DictionaryServiceImpl(DictionaryDataService dictionaryDataService, DictionaryMapper dictionaryMapper) {
        this.dictionaryDataService = dictionaryDataService;
        this.dictionaryMapper = dictionaryMapper;
    }


    /**
     * 字典列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    @Override
    public Page<Dictionary> getDictionaryList(DictionaryRequest request) {
        Page<Dictionary> dictionaryPage = new Page<>(request.getPageNum(), request.getPageSize());
        return dictionaryMapper.getDictionaryList(dictionaryPage, request);
    }

    /**
     * 根据字典名称查询字典是否存在, 如果当前字典名称存在, 则返回true，否则返回false
     *
     * @param id 当前字典id
     * @return 如果当前字典名称存在, 则返回true，否则返回false
     */
    @Override
    public boolean isNameExistExceptCurrent(Long id, String name) {
        Long count = dictionaryMapper.getDictionaryCountExcludeCurrentId(id, name);
        log.info("count:{}", count);
        return count > 0;
    }


    /**
     * 根据字典名称查询字典是否存在
     *
     * @param name 字典名称
     * @return 如果同一字典内字典名称存在重复, 则返回true，否则返回false
     */
    @Override
    public boolean isNameExist(String name) {
        LambdaQueryWrapper<Dictionary> eq = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getName, name);
        return this.count(eq) > 0;
    }

    /**
     * 添加字典
     *
     * @param request 请求参数
     */
    @Override
    public void addDictionary(AddDictionaryRequest request) {
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(request, dictionary);
        save(dictionary);
    }

    /**
     * 根据id获取字典
     *
     * @param id 字典id
     * @return 字典
     */
    @Override
    public Dictionary getDictionaryById(Long id) {
        LambdaQueryWrapper<Dictionary> eq = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getId, id);
        return getOne(eq);
    }

    /**
     * 更新字典
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateDictionaryById(UpdateDictionaryRequest request) {
        Dictionary dictionary = new Dictionary();
        BeanUtils.copyProperties(request, dictionary);
        LambdaQueryWrapper<Dictionary> eq = new LambdaQueryWrapper<Dictionary>().eq(Dictionary::getId, request.getId());
        return update(dictionary, eq);
    }

    /**
     * 删除字典,支持批量删除
     *
     * @param ids 字典id
     */
    @Override
    @Transactional
    public void deleteDictionary(List<Long> ids) {
        ids.forEach(id -> {
            long countByDictionaryId = dictionaryDataService.getCountByDictionaryId(id);
            if (countByDictionaryId > 0) {
                throw new ServiceException("该字典下有字典项，不能删除");
            }
        });
        removeByIds(ids);
    }

}




