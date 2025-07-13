package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.model.entity.SysDictValue;
import cn.zhangchuangla.system.model.request.dict.SysDictValueAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictValueQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictValueUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典项 服务层
 *
 * @author Chuang
 */
public interface SysDictValueService extends IService<SysDictValue> {

    /**
     * 获取字典项列表
     *
     * @param page    分页
     * @param request 请求参数
     * @return 分页结果
     */
    Page<SysDictValue> listDictValue(Page<SysDictValue> page, String dictKey, SysDictValueQueryRequest request);

    /**
     * 根据id获取字典项
     *
     * @param id id
     * @return 字典项
     */
    SysDictValue getDictValueById(Long id);

    /**
     * 添加字典项
     *
     * @param request 请求
     * @return 是否添加成功
     */
    boolean addDictValue(SysDictValueAddRequest request);

    /**
     * 修改字典项
     *
     * @param request 请求
     * @return 是否修改成功
     */
    boolean updateDictValue(SysDictValueUpdateRequest request);

    /**
     * 删除字典项
     *
     * @param ids id列表
     * @return 是否删除成功
     */
    boolean deleteDictValue(List<Long> ids);

    /**
     * 根据字典类型编码删除字典项
     *
     * @param dictKeys 字典类型编码列表
     */
    void deleteDictValueByDictKey(List<String> dictKeys);


    /**
     * 检查同一字典类型下字典项值是否重复
     *
     * @param dictKey   字典类型编码
     * @param itemValue 字典项值
     * @param itemId    字典项ID (更新时排除自身)
     * @return true 重复, false 不重复
     */
    boolean isDictValueExistByValue(String dictKey, String itemValue, Long itemId);

    /**
     * 根据字典类型编码获取字典项列表
     *
     * @param dictKey 字典类型编码
     * @return 字典项列表
     */
    List<Option<String>> getDictValueOption(String dictKey);
}
