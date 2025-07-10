package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.model.entity.SysDictType;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeQueryRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictTypeUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysDictTypeService extends IService<SysDictType> {


    /**
     * 获取字典类型列表
     *
     * @param page    分页
     * @param request 请求参数
     * @return 分页结果
     */
    Page<SysDictType> listDictType(Page<SysDictType> page, SysDictTypeQueryRequest request);

    /**
     * 根据id获取字典类型
     *
     * @param id id
     * @return 字典类型
     */
    SysDictType getDictTypeById(Long id);

    /**
     * 添加字典类型
     *
     * @param request 请求参数
     * @return 是否添加成功
     */
    boolean addDictType(SysDictTypeAddRequest request);

    /**
     * 修改字典类型
     *
     * @param request 请求参数
     * @return 是否修改成功
     */
    boolean updateDictType(SysDictTypeUpdateRequest request);

    /**
     * 删除字典类型
     *
     * @param id 字典ID
     * @return 是否删除成功
     */
    boolean deleteDictType(List<Long> id);


    /**
     * 检查字典类型编码是否存在
     *
     * @param dictType 字典类型编码
     * @return true 存在，false 不存在
     */
    boolean isDictTypeExist(String dictType);


    /**
     * 刷新字典缓存
     *
     * @return 操作结果
     */
    boolean refreshCache();

    /**
     * 获取所有字典类型
     *
     * @return 字典类型列表
     */
    List<Option<String>> getAllDictType();
}
