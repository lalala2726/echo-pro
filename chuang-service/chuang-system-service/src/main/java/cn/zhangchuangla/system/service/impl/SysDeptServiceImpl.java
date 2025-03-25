package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysDeptMapper;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.department.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptListRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptRequest;
import cn.zhangchuangla.system.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author zhangchuang
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
        implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    public SysDeptServiceImpl(SysDeptMapper sysDeptMapper) {
        this.sysDeptMapper = sysDeptMapper;
    }

    /**
     * 部门列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    @Override
    public Page<SysDept> listDept(SysDeptListRequest request) {
        Page<SysDept> sysDepartmentPage = new Page<>(request.getPageSize(), request.getPageNum());
        return sysDeptMapper.listDepartment(sysDepartmentPage, request);
    }

    /**
     * 新增部门
     *
     * @param request 请求参数
     */
    @Override
    public boolean addDept(SysDeptAddRequest request) {
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(request, sysDept);
        return save(sysDept);
    }

    /**
     * 修改
     *
     * @param request 请求参数
     */
    @Override
    public boolean updateDept(SysDeptRequest request) {
        return false;
    }

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @Override
    public SysDept getDeptById(Integer id) {
        return getById(id);
    }

    /**
     * 判断部门名称是否存在
     *
     * @param deptName 部门名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isDeptNameExist(String deptName) {
        LambdaQueryWrapper<SysDept> eq = new LambdaQueryWrapper<SysDept>().eq(SysDept::getDeptName, deptName);
        return count(eq) > 0;
    }
}




