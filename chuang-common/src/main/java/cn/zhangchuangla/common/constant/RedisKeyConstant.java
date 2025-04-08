package cn.zhangchuangla.common.constant;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 22:55
 */
public class RedisKeyConstant {

    /**
     * Redis登录用户前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login_token_key:";

    /**
     * 用户登录密码错误次数前缀
     */
    public static final String PASSWORD_ERROR_COUNT = "password_error_count:";

    /**
     * 用户权限前缀
     */
    public static final String USER_PERMISSIONS = "user_permissions:";

    /**
     * 系统配置
     */
    public static final String SYSTEM_CONFIG = "system_config:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_CODE = "captcha:code:";

    /**
     * 文件上传服务选择
     */
    public static final String SYSTEM_FILE_UPLOAD_SERVICE_SELECT = SYSTEM_CONFIG + "file_upload_service_select";

    /**
     * 本地文件上传服务选择
     */
    public static final String SYSTEM_FILE_UPLOAD_SERVICE_SELECT_LOCAL = SYSTEM_FILE_UPLOAD_SERVICE_SELECT + ":local";

    /**
     * 阿里云文件上传服务选择
     */
    public static final String SYSTEM_FILE_UPLOAD_SERVICE_SELECT_MINIO = SYSTEM_FILE_UPLOAD_SERVICE_SELECT + ":minio";

    /**
     * 阿里云OSS
     */
    public static final String SYSTEM_FILE_UPLOAD_SERVICE_SELECT_ALIYUN = SYSTEM_FILE_UPLOAD_SERVICE_SELECT + ":oss";

    /**
     * 默认文件上传服务选择
     */
    public static final String SYSTEM_FILE_UPLOAD_SERVICE_SELECT_DEFAULT = SYSTEM_FILE_UPLOAD_SERVICE_SELECT
            + ":default";
    public static final String USER_ROLE = "user_role:";

    /**
     * 接口访问限流前缀
     */
    public static final String ACCESS_LIMIT_PREFIX = "access_limit:";

    /**
     * IP限流前缀
     */
    public static final String ACCESS_LIMIT_IP = ACCESS_LIMIT_PREFIX + "ip:";

    /**
     * 用户ID限流前缀
     */
    public static final String ACCESS_LIMIT_USER = ACCESS_LIMIT_PREFIX + "user:";

    /**
     * 自定义限流前缀
     */
    public static final String ACCESS_LIMIT_CUSTOM = ACCESS_LIMIT_PREFIX + "custom:";
}
