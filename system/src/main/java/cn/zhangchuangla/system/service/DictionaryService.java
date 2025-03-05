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


    /**
     * 根据字典名称查询字典是否存在
     *
     * @param name 字典名称
     * @return 如果同一字典内字典名称存在重复, 则返回true，否则返回false
     */
    boolean isNameExist(String name);

    /**
     * 新增字典
     *
     * @param request 请求参数
     */
    void addDictionary(AddDictionaryRequest request);

    /**
     * 根据id获取字典
     *
     * @param id 字典id
     * @return 字典
     */
    Dictionary getDictionaryById(Long id);

    /**
     * 更新字典
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updateDictionaryById(UpdateDictionaryRequest request);

    /**
     * 删除字典,支持批量删除
     *
     * @param ids 字典id
     */
    void deleteDictionary(List<Long> ids);

    /**
     * 字典分页列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    Page<Dictionary> getDictionaryList(DictionaryRequest request);
}
