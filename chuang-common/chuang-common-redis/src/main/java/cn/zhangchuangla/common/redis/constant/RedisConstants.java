package cn.zhangchuangla.common.redis.constant;

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

    /**
     * 字典模块缓存接口
     */
    interface Dict {
        /**
         * 字典缓存前缀
         */
        String DICT_CACHE_PREFIX = "system:dict:data:";

        /**
         * 字典数据缓存Key格式: system:dict:data:{dictType}
         */
        String DICT_DATA_KEY = DICT_CACHE_PREFIX + "%s";

        /**
         * 字典缓存过期时间（秒）- 24小时
         */
        int DICT_CACHE_EXPIRE_TIME = 24 * 60 * 60;
    }

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


    interface StorageConfig {
        String ACTIVE_TYPE = "storage:active_type";
        String CURRENT_STORAGE_CONFIG = "storage:current_storage_config";
        String CONFIGURATION_FILE_TYPE = "storage:configuration_file_type";

        String CONFIG_TYPE_DATABASE = "database";
        String CONFIG_TYPE_LOCAL = "local";
    }

    /**
     * 代码生成
     */
    interface Generator {
        String CONFIG_INFO = "generator:config:info";
    }

    /**
     * 认证模块
     */
    interface Auth {

        String ACCESS_TOKEN_USER = "auth:token:access:";

        String USER_ACCESS_TOKEN = "auth:user:access:";

        String USER_REFRESH_TOKEN = "auth:user:refresh:";

        String ROLE_KEY = "auth:role:";


        /**
         * 在线会话管理：按时间排序的在线设备列表。
         * Key 格式: auth:sessions:{userId}:{deviceType}
         * 类型: ZSET
         */
        String SESSIONS_KEY = "auth:sessions:";
        String SESSIONS_KEY_INDEX = "auth:sessions:index:";

        /**
         * 设备信息详情。
         * Key 格式: auth:device:{userId}:{deviceId}
         * 类型: STRING / HASH
         */
        String DEVICE_INFO_KEY = "auth:device:";

        /**
         * 当前设备的 RefreshToken。
         * Key 格式: auth:refresh:{userId}:{deviceId}
         * 类型: STRING
         */
        String DEVICE_REFRESH_TOKEN_KEY = "auth:refresh";

        /**
         * 分布式锁前缀
         * Key 格式: auth:lock:{lockName}
         * 类型: STRING
         */
        String DISTRIBUTED_LOCK_PREFIX = "auth:lock:";

        /**
         * AccessToken 黑名单。
         * Key 格式: auth:blacklist:{jti}
         * 类型: STRING
         */
        String ACCESS_TOKEN_BLACKLIST_KEY = "auth:blacklist:";

        /**
         * 限制登录频率。
         * Key 格式: auth:limit:login:{userId}:{hour}
         * 类型: STRING (INCR)
         */
        String LOGIN_LIMIT_KEY = "auth:limit:login:";
    }

}
