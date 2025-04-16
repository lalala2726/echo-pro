package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.converter.SysDictionaryConverter;
import cn.zhangchuangla.system.mapper.SysDictionaryMapper;
import cn.zhangchuangla.system.model.entity.SysDictionary;
import cn.zhangchuangla.system.model.request.dictionary.AddDictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.DictionaryRequest;
import cn.zhangchuangla.system.model.request.dictionary.UpdateDictionaryRequest;
import cn.zhangchuangla.system.service.SysDictionaryDataService;
import cn.zhangchuangla.system.service.SysDictionaryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 字典服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysDictionaryServiceImpl extends ServiceImpl<SysDictionaryMapper, SysDictionary>
        implements SysDictionaryService {

    private final SysDictionaryDataService sysDictionaryDataService;
    private final SysDictionaryMapper sysDictionaryMapper;
    private final SysDictionaryConverter sysDictionaryConverter;


    /**
     * 字典列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    @Override
    public Page<SysDictionary> getDictionaryList(DictionaryRequest request) {
        Page<SysDictionary> dictionaryPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysDictionaryMapper.getDictionaryList(dictionaryPage, request);
    }

    /**
     * 根据字典名称查询字典是否存在, 如果当前字典名称存在, 则返回true，否则返回false
     *
     * @param id 当前字典id
     * @return 如果当前字典名称存在, 则返回true，否则返回false
     */
    @Override
    public boolean isNameExistExceptCurrent(Long id, String name) {
        Long count = sysDictionaryMapper.getDictionaryCountExcludeCurrentId(id, name);
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
        LambdaQueryWrapper<SysDictionary> eq = new LambdaQueryWrapper<SysDictionary>().eq(SysDictionary::getName, name);
        return this.count(eq) > 0;
    }

    /**
     * 添加字典
     *
     * @param request 请求参数
     */
    @Override
    public void addDictionary(AddDictionaryRequest request) {
        if (isNameExist(request.getName())) {
            throw new ServiceException("字典名称已存在");
        }
        SysDictionary sysDictionary = sysDictionaryConverter.toEntity(request);
        save(sysDictionary);
    }

    /**
     * 根据id获取字典
     *
     * @param id 字典id
     * @return 字典
     */
    @Override
    public SysDictionary getDictionaryById(Long id) {
        LambdaQueryWrapper<SysDictionary> eq = new LambdaQueryWrapper<SysDictionary>().eq(SysDictionary::getId, id);
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
        if (isNameExistExceptCurrent(request.getId(), request.getName())) {
            throw new ServiceException("字典名称已存在");
        }
        SysDictionary sysDictionary = sysDictionaryConverter.toEntity(request);
        LambdaQueryWrapper<SysDictionary> eq = new LambdaQueryWrapper<SysDictionary>().eq(SysDictionary::getId, request.getId());
        return update(sysDictionary, eq);
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
            long countByDictionaryId = sysDictionaryDataService.getCountByDictionaryId(id);
            if (countByDictionaryId > 0) {
                throw new ServiceException("该字典下有字典项，不能删除");
            }
        });
        removeByIds(ids);
    }

}




