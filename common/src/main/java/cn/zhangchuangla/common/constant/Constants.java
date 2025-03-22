package cn.zhangchuangla.common.constant;

/**
 * 系统常量类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 22:30
 */
public class Constants {

    /**
     * ==========================
     * ✅ JSON 序列化白名单
     * ==========================
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "cn.zhangchuangla"};

    /**
     * ==========================
     * ✅ Token 相关
     * ==========================
     */
    public static final String TOKEN = "token";
    public static final String LOGIN_USER_KEY = "login_user_key";
    public static final String USERNAME = "username";
    public static final String COLON = ":";

    /**
     * ==========================
     * ✅ 响应信息
     * ==========================
     */
    public static final String MESSAGE = "message";
    public static final String DATA = "data";
    public static final String CODE = "code";
    public static final String TIME = "TIME";

    /**
     * ==========================
     * ✅ 账号状态
     * ==========================
     */
    public static final Integer ACCOUNT_LOCK_KEY = 1;    // 账号封禁
    public static final Integer ACCOUNT_UNLOCK_KEY = 0;  // 账号正常

    /**
     * ==========================
     * ✅ 登录状态
     * ==========================
     */
    public static final Integer LOGIN_SUCCESS = 0;  // 登录成功
    public static final Integer LOGIN_FAIL = 1;     // 登录失败

    /**
     * ==========================
     * ✅ 系统相关
     * ==========================
     */
    public static final String SYSTEM_CREATE = "系统自动创建";
    public static final String RESOURCE_PREFIX = "/profile";
    public static final Integer CACHE_USER_PERMISSIONS_EXPIRE = 15;  // 用户权限缓存过期时间（分钟）

    /**
     * ==========================
     * ✅ 权限相关
     * ==========================
     */
    public static final String ALL_PERMISSION = "*:*:*";  // 拥有所有权限

    /**
     * ==========================
     * ✅ 文件上传方式
     * ==========================
     */
    public static final String LOCAL_FILE_UPLOAD = "local";        // 本地文件上传
    public static final String MINIO_FILE_UPLOAD = "minio";        // Minio 上传
    public static final String ALIYUN_OSS_FILE_UPLOAD = "oss";     // 阿里云 OSS 上传

    public static final String FILE_URL = "fileUrl";
    public static final String COMPRESSED_URL = "compressedUrl";
    public static final String RELATIVE_FILE_LOCATION = "relativeFileLocation";

    public static final String FILE_PREVIEW_FOLDER = "preview";


    public static final String FILE_ORIGINAL_FOLDER = "original";
}
