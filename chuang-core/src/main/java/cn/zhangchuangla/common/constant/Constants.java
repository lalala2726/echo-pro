package cn.zhangchuangla.common.constant;

/**
 * 系统常量类
 * <p>
 * 定义系统中使用的各种全局常量，方便管理和维护。
 * </p>
 *
 * @author Chuang
 */
public class Constants {

    /**
     * JSON 序列化白名单
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "cn.zhangchuangla"};

    /**
     * Token 相关常量
     */
    public static final String TOKEN = "token";
    public static final String LOGIN_USER_KEY = "login_user_key";
    public static final String USERNAME = "username";
    public static final String COLON = ":";

    /**
     * 响应信息常量
     */
    public static final String MESSAGE = "message";
    public static final String DATA = "data";
    public static final String CODE = "code";
    public static final String TIME = "TIME";

    /**
     * 账号状态常量
     */
    public static final Integer ACCOUNT_LOCK_KEY = 1;    // 账号封禁
    public static final Integer ACCOUNT_UNLOCK_KEY = 0;  // 账号正常

    /**
     * 登录状态常量
     */
    public static final Integer LOGIN_SUCCESS = 0;  // 登录成功
    public static final Integer LOGIN_FAIL = 1;     // 登录失败

    /**
     * 系统相关常量
     */
    public static final String SYSTEM_CREATE = "系统自动创建";
    public static final String RESOURCE_PREFIX = "/profile";
    public static final Integer CACHE_USER_PERMISSIONS_EXPIRE = 15;  // 用户权限缓存过期时间（分钟）

    /**
     * 权限相关常量
     */
    public static final String ALL_PERMISSION = "*:*:*";  // 拥有所有权限

    /**
     * 文件上传方式常量
     */
    public static final String LOCAL_FILE_UPLOAD = "local";        // 本地文件上传
    public static final String MINIO_FILE_UPLOAD = "minio";        // Minio 上传
    public static final String ALIYUN_OSS_FILE_UPLOAD = "oss";     // 阿里云 OSS 上传
    public static final String FILE_URL = "fileUrl";
    public static final String COMPRESSED_URL = "compressedUrl";
    public static final String RELATIVE_FILE_LOCATION = "relativeFileLocation";
    public static final String FILE_PREVIEW_FOLDER = "preview";
    public static final String FILE_ORIGINAL_FOLDER = "original";

    /**
     * 系统默认标识常量
     */
    public static final String IS_SYSTEM_DEFAULT = "YES";

    /**
     * 菜单相关常量
     */
    public static final String IS_MENU_EXTERNAL_LINK = "0"; // 是否为外链（0是）
    public static final String IS_NOT_MENU_EXTERNAL_LINK = "1"; // 是否不是外链（1否）
    public static final String MENU_TYPE_DIRECTORY = "M"; // 菜单类型（目录）
    public static final String MENU_TYPE_MENU = "C"; // 菜单类型（菜单）
    public static final String MENU_TYPE_BUTTON = "F"; // 菜单类型（按钮）

    /**
     * 组件标识常量
     */
    public static final String LAYOUT = "Layout"; // Layout 组件标识
    public static final String INNER_LINK = "InnerLink"; // InnerLink 组件标识

    /**
     * HTTP 相关常量
     */
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String PARENT_VIEW = "ParentView";
    public static final String IS_HIDDEN = "1";

    /**
     * 其他常量
     */
    public static final long IS_PARENT_NODE = 0L;
    public static final String IS_CACHE = "1";
}
