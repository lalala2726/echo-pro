package cn.zhangchuangla.common.constant;

/**
 * Redis 常量
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisConstants {

    /**
     * 接口访问限流前缀
     */
    String ACCESS_LIMIT_PREFIX = "access_limit:";

    /**
     * IP限流前缀
     */
    String ACCESS_LIMIT_IP = ACCESS_LIMIT_PREFIX + "ip:";

    /**
     * 用户ID限流前缀
     */
    String ACCESS_LIMIT_USER = ACCESS_LIMIT_PREFIX + "user:";

    /**
     * 自定义限流前缀
     */
    String ACCESS_LIMIT_CUSTOM = ACCESS_LIMIT_PREFIX + "custom:";

    /**
     * 认证模块
     */
    interface Auth {
        // 存储访问令牌对应的用户信息（accessToken -> OnlineUser）
        String ACCESS_TOKEN_USER = "auth:token:access:{}";
        // 存储刷新令牌对应的用户信息（refreshToken -> OnlineUser）
        String REFRESH_TOKEN_USER = "auth:token:refresh:{}";
        // 用户与访问令牌的映射（userId -> accessToken）
        String USER_ACCESS_TOKEN = "auth:user:access:{}";
        // 用户与刷新令牌的映射（userId -> refreshToken
        String USER_REFRESH_TOKEN = "auth:user:refresh:{}";
        // 黑名单 Token（用于退出登录或注销）
        String BLACKLIST_TOKEN = "auth:token:blacklist:{}";
        // 权限前缀
        String PERMISSIONS_PREFIX = "auth:permissions:{}"; // 权限缓存前缀
    }

}
