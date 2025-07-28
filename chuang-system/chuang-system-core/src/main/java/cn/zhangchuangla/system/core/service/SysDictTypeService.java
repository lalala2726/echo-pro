package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.core.model.entity.SysDictType;
import cn.zhangchuangla.system.core.model.request.dict.SysDictTypeAddRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictTypeQueryRequest;
import cn.zhangchuangla.system.core.model.request.dict.SysDictTypeUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典类型 Service 接口
 *
 * @author Chuang
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 分页查询字典类型列表
     *
     * @param request 查询条件
     * @return 字典类型分页列表
     */
    Page<SysDictType> listDictType(SysDictTypeQueryRequest request);

    /**
     * 根据ID获取字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型信息
     */
    SysDictType getDictTypeById(Long id);

    /**
     * 添加字典类型
     *
     * @param request 添加请求
     * @return 是否添加成功
     */
    boolean addDictType(SysDictTypeAddRequest request);

    /**
     * 更新字典类型
     *
     * @param request 更新请求
     * @return 是否更新成功
     */
    boolean updateDictType(SysDictTypeUpdateRequest request);

    /**
     * 删除字典类型
     *
     * @param ids 字典类型ID列表
     * @return 是否删除成功
     */
    boolean deleteDictType(List<Long> ids);

    /**
     * 检查字典类型是否存在
     *
     * @param dictType 字典类型
     * @return 是否存在
     */
    boolean isDictTypeExist(String dictType);

    /**
     * 获取所有字典类型选项
     *
     * @return 字典类型选项列表
     */
    List<Option<String>> getAllDictType();

    /**
     * 刷新字典缓存
     *
     * @return 是否刷新成功
     */
    boolean refreshCache();
}
