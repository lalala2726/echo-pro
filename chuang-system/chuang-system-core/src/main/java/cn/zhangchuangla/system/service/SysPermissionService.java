package cn.zhangchuangla.system.service;

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
     * 根据用户id获取权限列表
     *
     * @param userId 用户id
     * @return 权限列表
     */
    Set<String> getUserPermissionByUserId(Long userId);

    /**
     * 获取当前用户的权限列表,不传递用户ID默认获取当前登录用户的权限列表
     *
     * @return 权限列表
     */
    Set<String> getUserPermissionByUserId();
}
