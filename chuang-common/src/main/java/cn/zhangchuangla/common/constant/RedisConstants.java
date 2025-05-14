package cn.zhangchuangla.common.constant;

/**
 * Redis 常量
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisConstants {

    /**
     * 用户登录密码错误次数前缀
     */
    String PASSWORD_ERROR_COUNT = "password_error_count:";


    /**
     * 系统配置
     */
    String SYSTEM_CONFIG = "system_config:";

    /**
     * 验证码前缀
     */
    String CAPTCHA_CODE = "captcha:code:";

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

    String DICT_CACHE = "dict:cache:";

    /**
     * access-token-expire-time: 7200
     * refresh-token-expire-time: 2592000
     * single-login: false
     */
    //todo 将配置项放入Redis中可以动态修改
    interface SystemConfig {
        interface SessionConfig {
            // 是否单点登录
            boolean SINGLE_LOGIN = false;
            // 访问令牌过期时间(单位秒)
            int ACCESS_TOKEN_EXPIRE_TIME = 7200;
            // 刷新令牌过期时间(单位秒)
            int REFRESH_TOKEN_EXPIRE_TIME = 2592000;
        }
    }

    /**
     * 认证模块
     */
    interface Auth {

        // 存储访问令牌对应的用户信息（accessToken -> OnlineUser）
        String ACCESS_TOKEN_USER = "auth:token:access:";

        // 存储刷新令牌对应的用户信息（refreshToken -> OnlineUser）
        String REFRESH_TOKEN_USER = "auth:token:refresh:";

        // 用户与访问令牌的映射（userId -> accessToken）
        String USER_ACCESS_TOKEN = "auth:user:access:";

        // 用户与刷新令牌的映射（userId -> refreshToken
        String USER_REFRESH_TOKEN = "auth:user:refresh:";

        // 刷新令牌ID与访问令牌ID的映射 (refreshTokenId -> accessTokenId)
        String REFRESH_TOKEN_MAPPING = "auth:token:refresh_mapping:{}";

        // 黑名单 Token（用于退出登录或注销）
        String BLACKLIST_TOKEN = "auth:token:blacklist:";

        // 权限前缀
        String PERMISSIONS_PREFIX = "auth:permissions:";

        // 角色权限缓存前缀
        String ROLE_PERMISSIONS_PREFIX = "auth:role_permissions:{}";
        String ROLE_KEY = "auth:role:";
    }

}
