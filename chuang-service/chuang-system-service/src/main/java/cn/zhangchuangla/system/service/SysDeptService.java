package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.department.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptListRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhangchuang
 */
public interface SysDeptService extends IService<SysDept> {


    /**
     * 部门列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    Page<SysDept> listDept(SysDeptListRequest request);

    /**
     * 新增部门
     *
     * @param request 请求参数
     */
    boolean addDept(SysDeptAddRequest request);

    /**
     * 修改部门
     *
     * @param request 请求参数
     */
    boolean updateDept(SysDeptRequest request);

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    SysDept getDeptById(Integer id);


    /**
     * 部门名称是否存在
     *
     * @param deptName 部门名称
     * @return true存在，false不存在
     */
    boolean isDeptNameExist(String deptName);

    /**
     * 部门下是否存在子部门
     *
     * @param id 部门ID
     */
    boolean departmentHasSubordinates(Integer id);
}
