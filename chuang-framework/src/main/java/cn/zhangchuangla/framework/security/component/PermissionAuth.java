package cn.zhangchuangla.framework.security.component;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.system.service.SysPermissionService;
import cn.zhangchuangla.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@RequiredArgsConstructor
public class PermissionAuth {


    private final SysRoleService sysRoleService;
    private final SysPermissionService sysPermissionService;

    /**
     * 判断当前用户是否拥有指定的权限
     *
     * @param permission 权限标识符（如 "system:role:list"）
     * @return true - 拥有该权限，false - 没有该权限
     */
    public boolean hasPermission(String permission) {
        Set<String> roles = SecurityUtils.getRoles();
        // 如果是超级管理员，直接返回 true
        if (roles.contains(SysRolesConstant.SUPER_ADMIN)) {
            return true;
        }
        Set<String> permissionByRole = sysPermissionService.getPermissionByRole(roles);
        return isAllow(permissionByRole, permission);
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
        Long userId = SecurityUtils.getUserId();
        Set<String> roleSetByRoleId = sysRoleService.getRoleSetByRoleId(userId);
        boolean hasRole = roleSetByRoleId.contains(role);
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
        Set<String> roles = getRoles();
        return roles.contains(SysRolesConstant.ADMIN);
    }

    /**
     * 判断当前用户是否是超级管理员
     *
     * @return true - 是超级管理员，false - 不是超级管理员
     */
    private boolean isSuperAdmin() {
        Set<String> roles = getRoles();
        return roles.contains(SysRolesConstant.SUPER_ADMIN);
    }

    /**
     * 获取当前登录用户的角色集合,这边权限对实时性较高所以不使用缓存
     *
     * @return 角色集合
     */
    private Set<String> getRoles() {
        Long userId = SecurityUtils.getUserId();
        sysRoleService.getRoleSetByUserId(userId);
        return sysRoleService.getRoleSetByUserId(userId);
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
