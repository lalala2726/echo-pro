package cn.zhangchuangla.infrastructure.security.component;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.infrastructure.security.context.PermissionContextHolder;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 权限认证组件
 * <p>
 * 提供权限校验的方法，如单个权限校验、多权限校验、用户身份校验等。
 * </p>
 *
 * @author Chuang
 * @since 2025/2/26
 */
@Slf4j
@Service("ss")
public class PermissionAuth {

    private final SysPermissionsService sysPermissionsService;
    private final SysRoleService sysRoleService;

    public PermissionAuth(SysPermissionsService sysPermissionsService, SysRoleService sysRoleService) {
        this.sysPermissionsService = sysPermissionsService;
        this.sysRoleService = sysRoleService;
    }

    /**
     * 判断当前用户是否拥有指定的权限
     *
     * @param permission 权限标识符（如 "system:role:list"）
     * @return true - 拥有该权限，false - 没有该权限
     */
    public boolean hasPermission(String permission) {
        if (isSuperAdmin() || isAdmin() || StringUtils.isBlank(permission)) {
            return true;
        }
        SysUserDetails sysUserDetails = getLoginUser();
        if (sysUserDetails == null) {
            log.warn("未找到登录用户信息，无法进行权限校验");
            return false;
        }
        Set<String> permissions = sysPermissionsService.getPermissionsByUserId(sysUserDetails.getUserId());
        PermissionContextHolder.setContext(permission);
        log.info("用户 [{}] 权限校验: 权限标识 [{}]，用户权限：{}", sysUserDetails.getUsername(), permission, permissions);
        return isAllow(permissions, permission);
    }

    /**
     * 判断当前用户是否至少拥有其中一个权限
     *
     * @param permissions 需要校验的权限列表
     * @return true - 至少拥有一个权限，false - 一个都没有
     */
    public boolean hasAnyPermission(String... permissions) {
        if (isSuperAdmin() || isSuperAdmin() || permissions == null || permissions.length == 0) {
            return true;
        }
        SysUserDetails sysUserDetails = getLoginUser();
        if (sysUserDetails == null) {
            log.warn("未找到登录用户信息，无法进行多权限校验");
            return false;
        }
        Set<String> userPermissions = sysPermissionsService.getPermissionsByUserId(sysUserDetails.getUserId());
        for (String permission : permissions) {
            if (userPermissions.contains(StringUtils.trim(permission))) {
                log.info("用户 [{}] 拥有权限 [{}]，返回 true", sysUserDetails.getUsername(), permission);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前用户是否拥有指定角色
     *
     * @param role 需要匹配的角色标识
     * @return true - 角色匹配，false - 角色不匹配
     */
    public boolean isSpecificRole(String role) {
        if (isSuperAdmin() || isSuperAdmin() || StringUtils.isBlank(role)) {
            return true;
        }
        SysUserDetails sysUserDetails = getLoginUser();
        if (sysUserDetails == null) {
            log.warn("未找到登录用户信息，无法进行角色校验");
            return false;
        }
        Set<String> roleSet = sysRoleService.getUserRoleSetByUserId(sysUserDetails.getUserId());
        boolean hasRole = roleSet.contains(role);
        log.debug("用户 [{}] 角色校验 [{}]，匹配结果：{}", sysUserDetails.getUsername(), role, hasRole);
        return hasRole;
    }

    /**
     * 判断当前用户是否拥有指定的权限
     *
     * @param permissions 用户权限集合
     * @param permission  需要校验的权限标识符
     * @return true - 拥有该权限，false - 没有该权限
     */
    private boolean isAllow(Set<String> permissions, String permission) {
        return permissions.contains(Constants.ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission));
    }

    /**
     * 判断当前用户是否是管理员
     *
     * @return true - 是管理员，false - 不是管理员
     */
    private boolean isAdmin() {
        Set<String> roles = SecurityUtils.getRoles();
        return roles.contains(SysRolesConstant.ADMIN);
    }

    /**
     * 判断当前用户是否是超级管理员
     *
     * @return true - 是超级管理员，false - 不是超级管理员
     */
    private boolean isSuperAdmin() {
        Set<String> roles = SecurityUtils.getRoles();
        return roles.contains(SysRolesConstant.ADMIN);
    }

    /**
     * 获取当前登录用户
     *
     * @return 登录用户对象，如果不存在返回 null
     */
    private SysUserDetails getLoginUser() {
        return SecurityUtils.getLoginUser();
    }
}
