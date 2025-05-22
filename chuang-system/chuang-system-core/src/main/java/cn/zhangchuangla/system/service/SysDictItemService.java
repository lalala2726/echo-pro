package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.model.entity.Option;
import cn.zhangchuangla.system.model.entity.SysDictItem;
import cn.zhangchuangla.system.model.request.dict.SysDictItemAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictItemUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典项 服务层
 *
 * @author Chuang
 */
public interface SysDictItemService extends IService<SysDictItem> {

    /**
     * 获取字典项列表
     *
     * @param page    分页
     * @param request 请求
     * @return 分页结果
     */
    Page<SysDictItem> listDictItem(Page<SysDictItem> page, String dictType, SysDictItemQueryRequest request);

    /**
     * 根据id获取字典项
     *
     * @param id id
     * @return 字典项
     */
    SysDictItem getDictItemById(Long id);

    /**
     * 添加字典项
     *
     * @param request 请求
     * @return 是否添加成功
     */
    boolean addDictItem(SysDictItemAddRequest request);

    /**
     * 修改字典项
     *
     * @param request 请求
     * @return 是否修改成功
     */
    boolean updateDictItem(SysDictItemUpdateRequest request);

    /**
     * 删除字典项
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    boolean deleteDictItem(List<Long> ids);

    /**
     * 根据字典类型编码删除字典项
     *
     * @param dictTypes 字典类型编码列表
     */
    void deleteDictItemByDictType(List<String> dictTypes);

    /**
     * 根据旧的字典类型编码更新为新的字典类型编码
     *
     * @param oldDictType 旧字典类型编码
     * @param newDictType 新字典类型编码
     */
    void updateDictItemDictType(String oldDictType, String newDictType);

    /**
     * 检查同一字典类型下字典项值是否重复
     *
     * @param dictType  字典类型编码
     * @param itemValue 字典项值
     * @param itemId    字典项ID (更新时排除自身)
     * @return true 重复, false 不重复
     */
    boolean isDictItemValueExist(String dictType, String itemValue, Long itemId);

    /**
     * 根据字典类型编码获取字典项列表
     *
     * @param dictType 字典类型编码
     * @return 字典项列表
     */
    List<Option<String>> getDictItemOption(String dictType);
}
