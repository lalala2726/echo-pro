package cn.zhangchuangla.framework.security;

import cn.zhangchuangla.common.core.enums.DeviceType;
import cn.zhangchuangla.common.core.exception.AccessDeniedException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.framework.security.component.PermissionAuth;
import cn.zhangchuangla.framework.security.device.DeviceService;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import cn.zhangchuangla.framework.security.token.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 进一步封装会话管理,设备管理,令牌管理,权限管理,让使用更加方便
 *
 * @author Chuang
 * <p>
 * created on 2025/7/28 16:01
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserSecurityManager {


    private static UserSecurityManager instance;

    private final RedisTokenStore redisTokenStore;
    private final TokenService tokenService;
    private final DeviceService deviceService;
    private final PermissionAuth permissionAuth;

    @PostConstruct
    private void init() {
        instance = this;
    }

    private static UserSecurityManager getInstance() {
        Assert.notNull(instance, "UserSecurityManager 未初始化");
        return instance;
    }

    /**
     * 检查权限
     *
     * @param permission 权限
     */
    public static void checkPermission(String permission) {
        if (!getInstance().permissionAuth.hasPermission(permission)) {
            throw new AccessDeniedException("您没有权限访问此资源");
        }
    }

    /**
     * 检查角色
     *
     * @param role 角色
     */
    public static void checkRole(String role) {
        if (!getInstance().permissionAuth.isSpecificRole(role)) {
            throw new AccessDeniedException("您没有权限访问此资源");
        }
    }

    /**
     * 注销特定用户的登录
     *
     * @param username 用户名
     */
    public static boolean logout(String username) {
        return getInstance().deviceService.deleteDeviceByUsername(username);
    }

    /**
     * 注销特定用户的登录
     *
     * @param username   用户名
     * @param deviceType 设备类型
     * @return 删除成功与否
     */
    public static boolean logout(String username, DeviceType deviceType) {
        return getInstance().deviceService.deleteDeviceByUsername(username, deviceType);
    }

    /**
     * 通过特定刷新令牌注销此用户
     *
     * @param accessToken 访问令牌
     * @return 删除成功与否
     */
    public static boolean logoutByToken(String accessToken) {
        Assert.hasText(accessToken, "accessToken不能为空");
        UserSecurityManager securityManager = getInstance();
        String accessTokenId = securityManager.tokenService.getSessionId(accessToken);
        //删除设备信息
        String refreshTokenId = securityManager.redisTokenStore.getRefreshTokenIdByAccessTokenId(accessTokenId);
        return securityManager.deviceService.deleteDevice(refreshTokenId);
    }


}
