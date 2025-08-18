package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.system.core.mapper.SysDeptMapper;
import cn.zhangchuangla.system.core.model.entity.SysDept;
import cn.zhangchuangla.system.core.model.request.dept.SysDeptAddRequest;
import cn.zhangchuangla.system.core.model.request.dept.SysDeptQueryRequest;
import cn.zhangchuangla.system.core.model.request.dept.SysDeptUpdateRequest;
import cn.zhangchuangla.system.core.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 部门服务实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
        implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<SysDept> listDept(SysDeptQueryRequest request) {
        SysDept sysDept = BeanCotyUtils.copyProperties(request, SysDept.class);
        return sysDeptMapper.listDepartment(sysDept);
    }

    /**
     * 新增部门
     *
     * @param request 请求参数
     */
    @Override
    public boolean addDept(SysDeptAddRequest request) {
        if (isDeptNameExist(request.getDeptName())) {
            throw new ServiceException(ResultCode.DICT_NAME_EXIST, "部门名称已存在！");
        }

        // 检查父部门设置是否合理
        if (request.getParentId() != null && !request.getParentId().equals(0L)) {
            // 检查父部门是否存在
            SysDept parentDept = getById(request.getParentId());
            if (parentDept == null) {
                throw new ServiceException(ResultCode.OPERATION_ERROR, "指定的父部门不存在！");
            }
        }

        if (request.getParentId() == null || request.getParentId() == 0) {
            request.setParentId(0L);
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
    public boolean updateDept(SysDeptUpdateRequest request) {
        LambdaQueryWrapper<SysDept> ne = new LambdaQueryWrapper<SysDept>()
                .eq(SysDept::getDeptName, request.getDeptName())
                .ne(SysDept::getDeptId, request.getDeptId());
        if (count(ne) > 0) {
            throw new ServiceException(ResultCode.DICT_NAME_EXIST, "部门名称已存在！");
        }

        // 检查父部门设置是否合理，避免形成循环依赖
        if (request.getParentId() != null && !request.getParentId().equals(0L)) {
            // 不能将部门设置为自己的子部门
            if (request.getParentId().equals(request.getDeptId())) {
                throw new ServiceException(ResultCode.OPERATION_ERROR, "不能将部门设置为自己的父部门！");
            }

            // 检查是否试图将部门设置为其子部门的父部门
            if (isChildOf(request.getParentId(), request.getDeptId())) {
                throw new ServiceException(ResultCode.OPERATION_ERROR, "不能将部门设置为其子部门的父部门！");
            }
        }

        SysDept sysDept = BeanCotyUtils.copyProperties(request, SysDept.class);
        return updateById(sysDept);
    }

    /**
     * 根据ID查询部门信息
     *
     * @param id 部门ID
     * @return 操作结果
     */
    @Override
    public SysDept getDeptById(Long id) {
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
    public boolean deptHasSubordinates(Long id) {
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
    public boolean deleteDeptById(List<Long> ids) {
        ids.forEach(id -> {
            if (deptHasSubordinates(id)) {
                throw new ServiceException(ResultCode.DICT_NAME_EXIST, "该部门下有子部门，不能删除！");
            }
        });
        return removeByIds(ids);
    }


    /**
     * 获取部门下拉列表
     *
     * @return 部门下拉列表
     */
    @Override
    public List<Option<Long>> getDeptOptions() {
        List<SysDept> allDept = list();
        return allDept.stream().map(dept -> new Option<>(dept.getDeptId(), dept.getDeptName())).toList();

    }

    /**
     * 获取部门树下拉列表
     *
     * @return 部门树下拉列表
     */
    @Override
    public List<Option<Long>> getDeptTreeOptions() {
        List<SysDept> allDept = list();
        return buildDeptTreeRecursive(0L, allDept);
    }

    /**
     * 递归构建部门树
     *
     * @param parentId 父部门ID
     * @param allDept  所有部门列表
     * @return 部门树列表
     */
    private List<Option<Long>> buildDeptTreeRecursive(Long parentId, List<SysDept> allDept) {
        return allDept.stream()
                .filter(dept -> {
                    // 处理可能的 null 值情况
                    Long deptParentId = dept.getParentId();
                    return deptParentId != null && deptParentId.equals(parentId);
                })
                .map(dept -> {
                    Option<Long> node = new Option<>();
                    node.setValue(dept.getDeptId());
                    node.setLabel(dept.getDeptName());
                    // 递归查找子节点
                    List<Option<Long>> children = buildDeptTreeRecursive(dept.getDeptId(), allDept);
                    if (!children.isEmpty()) {
                        node.setChildren(children);
                    }
                    return node;
                })
                .toList();
    }

    /**
     * 检查某个部门是否是另一个部门的子部门（递归检查）
     *
     * @param childId  可能的子部门ID
     * @param parentId 父部门ID
     * @return true 如果childId是parentId的子部门，否则false
     */
    private boolean isChildOf(Long childId, Long parentId) {
        if (childId == null || parentId == null) {
            return false;
        }

        SysDept childDept = getById(childId);
        if (childDept == null || childDept.getParentId() == null) {
            return false;
        }

        // 如果直接父部门就是目标部门
        if (childDept.getParentId().equals(parentId)) {
            return true;
        }

        // 递归检查父部门的父部门
        return isChildOf(childDept.getParentId(), parentId);
    }
}
