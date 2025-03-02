package cn.zhangchuangla.framework.security.component;

import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.framework.security.context.PermissionContextHolder;
import cn.zhangchuangla.system.service.SysPermissionsService;
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
@Service("auth")
public class PermissionAuth {

    private final SysPermissionsService sysPermissionsService;

    public PermissionAuth(SysPermissionsService sysPermissionsService) {
        this.sysPermissionsService = sysPermissionsService;
    }

    /**
     * 判断当前用户是否拥有指定的权限
     *
     * @param permission 权限标识符（如 "system:role:list"）
     * @return true - 拥有该权限，false - 没有该权限
     */
    public boolean hasPermission(String permission) {
        if (StringUtils.isBlank(permission)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Set<String> permissions = sysPermissionsService.getPermissionsByUserId(loginUser.getUserId());
        PermissionContextHolder.setContext(permission);
        log.info("用户 [{}] 权限信息: {}", loginUser.getUsername(), permissions);
        log.debug("权限信息:{}", permissions);
        return isAllow(permissions, permission);
    }

    /**
     * 判断当前用户是否至少拥有其中一个权限
     *
     * @param permissions 需要校验的权限列表
     * @return true - 至少拥有一个权限，false - 一个都没有
     */
    public boolean hasAnyPermission(String... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        Long userId = loginUser.getUserId();
        Set<String> userPermissions = sysPermissionsService.getPermissionsByUserId(userId);
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                log.debug("用户 [{}] 至少拥有权限 [{}]，返回 true", loginUser.getUsername(), permission);
                return true;
            }
        }
        return false;
    }

    /**
     * 要求当前权限必须是包含指定角色
     *
     * @param role 需要匹配的角色
     * @return true - 是指定用户，false - 不是指定用户
     */
    public boolean isSpecificRole(String role) {
        if (StringUtils.isBlank(role)) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return false;
    }

    /**
     * 判断当前用户是否拥有指定的权限
     *
     * @param permissions 用户权限集合
     * @param permission  需要校验的权限标识符
     * @return true - 拥有该权限，false - 没有该权限
     */
    public boolean isAllow(Set<String> permissions, String permission) {
        return permissions.contains(SystemConstant.ALL_PERMISSION) || permissions.contains(StringUtils.trim(permission));
    }
}
