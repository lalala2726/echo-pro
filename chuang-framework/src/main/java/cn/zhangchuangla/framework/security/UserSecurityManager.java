package cn.zhangchuangla.framework.security;

import cn.zhangchuangla.common.core.enums.DeviceType;
import cn.zhangchuangla.common.core.exception.AccessDeniedException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.framework.security.component.PermissionAuth;
import cn.zhangchuangla.framework.security.device.DeviceService;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import cn.zhangchuangla.framework.security.token.TokenService;
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


    private final RedisTokenStore redisTokenStore;
    private final TokenService tokenService;
    private final DeviceService deviceService;
    private final PermissionAuth permissionAuth;

    /**
     * 检查权限
     *
     * @param permission 权限
     */
    public void checkPermission(String permission) {
        if (!permissionAuth.hasPermission(permission)) {
            throw new AccessDeniedException("您没有权限访问此资源");
        }
    }

    /**
     * 检查角色
     *
     * @param role 角色
     */
    public void checkRole(String role) {
        if (!permissionAuth.isSpecificRole(role)) {
            throw new AccessDeniedException("您没有权限访问此资源");
        }
    }

    /**
     * 注销特定用户的登录
     *
     * @param username 用户名
     */
    public boolean logout(String username) {
        return deviceService.deleteDeviceByUsername(username);
    }

    /**
     * 注销特定用户的登录
     *
     * @param username   用户名
     * @param deviceType 设备类型
     * @return 删除成功与否
     */
    public boolean logout(String username, DeviceType deviceType) {
        return deviceService.deleteDeviceByUsername(username, deviceType);
    }

    /**
     * 通过特定刷新令牌注销此用户
     *
     * @param accessToken 访问令牌
     * @return 删除成功与否
     */
    public boolean logoutByToken(String accessToken) {
        Assert.hasText(accessToken, "accessToken不能为空");
        String accessTokenId = tokenService.getSessionId(accessToken);
        //删除设备信息
        String refreshTokenId = redisTokenStore.getRefreshTokenIdByAccessTokenId(accessTokenId);
        return deviceService.deleteDevice(refreshTokenId);
    }


}
