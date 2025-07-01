package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.model.vo.role.SysRolePermVo;

import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/14 08:23
 */
public interface SysPermissionService {


    /**
     * 根据角色标识符查询权限列表
     *
     * @param roleSet 角色标识符集合
     * @return 权限列表
     */
    Set<String> getUserPermissionByRole(Set<String> roleSet);

    /**
     * 根据角色标识符查询权限列表
     *
     * @param role 角色标识符
     * @return 权限列表
     */
    Set<String> getUserPermissionByRole(String role);

    /**
     * 根据角色id获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    SysRolePermVo getRolePermByRoleId(Long roleId);

    /**
     * 更新角色权限信息
     *
     * @param request 更新角色权限请求参数
     * @return 是否更新成功
     */
    boolean updateRolePermission(SysUpdateRolePermissionRequest request);

    /**
     * 根据角色id获取已选择的权限ID
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> getRolePermissionSelectedByRoleId(Long roleId);
}
