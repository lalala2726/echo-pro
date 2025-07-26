package cn.zhangchuangla.common.redis.constant;

/**
 * Redis 常量
 *
 * @author Theo
 * @since 2024-7-29 11:46:08
 */
public interface RedisConstants {
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


    interface StorageConfig {
        String ACTIVE_TYPE = "storage:active_type";
        String CURRENT_STORAGE_CONFIG = "storage:current_storage_config";
        String CONFIGURATION_FILE_TYPE = "storage:configuration_file_type";
        String CONFIG_TYPE_DATABASE = "database";
    }


    /**
     * 认证模块
     */
    interface Auth {


        String USER_ACCESS_TOKEN = "auth:token:access:";

        String USER_REFRESH_TOKEN = "auth:token:refresh:";

        String ROLE_KEY = "auth:role:";

        String SESSIONS_INDEX_KEY = "auth:session:index:";

        String SESSIONS_DEVICE_KEY = "auth:session:device:";


    }

}
