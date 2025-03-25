package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysDepartment;
import cn.zhangchuangla.system.model.request.department.SysDepartmentAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDepartmentListRequest;
import cn.zhangchuangla.system.model.request.department.SysDepartmentUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author zhangchuang
 */
public interface SysDepartmentService extends IService<SysDepartment> {


    /**
     * 部门列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    Page<SysDepartment> listDepartment(SysDepartmentListRequest request);

    /**
     * 新增部门
     *
     * @param request 请求参数
     */
    boolean addDepartment(SysDepartmentAddRequest request);

    /**
     * 修改部门
     *
     * @param request 请求参数
     */
    boolean updateDepartment(SysDepartmentUpdateRequest request);

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    SysDepartment getDepartmentById(Integer id);
}
