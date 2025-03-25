package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysDepartmentMapper;
import cn.zhangchuangla.system.model.entity.SysDepartment;
import cn.zhangchuangla.system.model.request.department.SysDepartmentAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDepartmentListRequest;
import cn.zhangchuangla.system.model.request.department.SysDepartmentUpdateRequest;
import cn.zhangchuangla.system.service.SysDepartmentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhangchuang
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment>
        implements SysDepartmentService {

    private final SysDepartmentMapper sysDepartmentMapper;

    public SysDepartmentServiceImpl(SysDepartmentMapper sysDepartmentMapper) {
        this.sysDepartmentMapper = sysDepartmentMapper;
    }

    /**
     * 部门列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    @Override
    public Page<SysDepartment> listDepartment(SysDepartmentListRequest request) {
        Page<SysDepartment> sysDepartmentPage = new Page<>(request.getPageSize(), request.getPageNum());
        return sysDepartmentMapper.listDepartment(sysDepartmentPage, request);
    }

    /**
     * 新增部门
     *
     * @param request 请求参数
     */
    @Override
    public boolean addDepartment(SysDepartmentAddRequest request) {
        SysDepartment sysDepartment = new SysDepartment();
        BeanUtils.copyProperties(request, sysDepartment);
        return save(sysDepartment);
    }

    /**
     * 修改
     *
     * @param request 请求参数
     */
    @Override
    public boolean updateDepartment(SysDepartmentUpdateRequest request) {
        return false;
    }

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @Override
    public SysDepartment getDepartmentById(Integer id) {
        return getById(id);
    }
}




