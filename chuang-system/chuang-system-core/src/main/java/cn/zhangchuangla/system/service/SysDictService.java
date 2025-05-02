package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysDict;
import cn.zhangchuangla.system.model.request.dict.SysDictAddRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictListRequest;
import cn.zhangchuangla.system.model.request.dict.SysDictUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 获取字典列表
     *
     * @param request 查询参数
     * @return 分页列表
     */
    Page<SysDict> listDict(SysDictListRequest request);

    /**
     * 添加字典
     *
     * @param request 请求参数
     * @return 添加结果
     */
    boolean addDict(SysDictAddRequest request);


    /**
     * 判断字典编码是否存在
     *
     * @param dictCode 字典编码
     * @return true 存在，false 不存在
     */
    boolean isDictCodeExist(String dictCode);

    /**
     * 判断字典名称是否存在
     *
     * @param name 字典名称
     * @return true 存在，false 不存在
     */
    boolean isDictNameExist(String name);

    /**
     * 根据字典ID获取字典信息
     *
     * @param id 字典ID
     * @return 字典信息
     */
    SysDict getDictById(Long id);

    /**
     * 删除字典，支持批量上次
     *
     * @param ids 字典ID集合
     * @return 更新结果
     */
    boolean deleteDict(List<Long> ids);

    /**
     * 更新字典
     *
     * @param request 请求参数
     * @return 更新结果
     */
    boolean updateDict(SysDictUpdateRequest request);
}
