package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.mapper.SysDeptMapper;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.department.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptListRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptRequest;
import cn.zhangchuangla.system.model.vo.dept.DeptTree;
import cn.zhangchuangla.system.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务实现类
 *
 * @author zhangchuang
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
        implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Autowired
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
        if (isDeptNameExist(request.getName())) {
            throw new ServiceException(ResponseCode.DICT_NAME_EXIST, "部门名称已存在！");
        }
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
        if (isDeptNameExist(request.getName())) {
            throw new ServiceException(ResponseCode.DICT_NAME_EXIST, "部门名称已存在！");
        }
        SysDept sysDept = new SysDept();
        BeanUtils.copyProperties(request, sysDept);
        return updateById(sysDept);
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

    /**
     * 判断部门是否存在子部门
     *
     * @param id 部门ID
     * @return true存在，false不存在
     */
    @Override
    public boolean departmentHasSubordinates(Integer id) {
        if (id != null) {
            LambdaQueryWrapper<SysDept> eq = new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id);
            return count(eq) > 0;
        }
        return false;
    }

    /**
     * 删除部门，支持批量删除，如果批量删除，则判断是否有子部门，如果有子部门，则不允许删除，并且回滚
     *
     * @param ids 部门ID集合
     * @return 操作结果
     */
    @Override
    public boolean removeDeptById(List<Integer> ids) {
        ids.forEach(id -> {
            if (departmentHasSubordinates(id)) {
                throw new ServiceException(ResponseCode.DICT_NAME_EXIST, "该部门下有子部门，不能删除！");
            }
        });
        return removeByIds(ids);
    }

    /**
     * 构建部门树
     *
     * @return 部门树
     */
    @Override
    public List<DeptTree> buildTree() {
        List<SysDept> deptList = list();
        return buildDeptTreeRecursive(0L, deptList);
    }

    /**
     * 递归构建部门树
     *
     * @param parentId 父部门ID
     * @param allDepts 所有部门列表
     * @return 部门树列表
     */
    private List<DeptTree> buildDeptTreeRecursive(Long parentId, List<SysDept> allDepts) {
        return allDepts.stream()
                .filter(dept -> {
                    // 处理可能的 null 值情况
                    Long deptParentId = dept.getParentId();
                    return deptParentId != null && deptParentId.equals(parentId);
                })
                .map(dept -> {
                    DeptTree node = new DeptTree();
                    node.setId(dept.getDeptId());
                    node.setLabel(dept.getDeptName());
                    // 递归查找子节点
                    List<DeptTree> children = buildDeptTreeRecursive(dept.getDeptId(), allDepts);
                    if (!children.isEmpty()) {
                        node.setChildren(children);
                    }
                    return node;
                })
                .toList();
    }

}
