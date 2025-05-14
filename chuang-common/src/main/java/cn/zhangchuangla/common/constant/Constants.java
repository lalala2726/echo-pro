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
     * 验证码相关
     */
    public static final String BASE64_CODE = "data:image/png;base64,";
    /**
     * JSON 序列化白名单
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework", "cn.zhangchuangla"};


    /**
     * 账号状态常量
     */
    public static final Integer ACCOUNT_LOCK_KEY = 1;
    public static final Integer ACCOUNT_UNLOCK_KEY = 0;

    /**
     * 登录状态常量
     */
    public static final Integer LOGIN_SUCCESS = 0;
    public static final Integer LOGIN_FAIL = 1;

    /**
     * 系统相关常量
     */
    public static final String SYSTEM_CREATE = "系统自动创建";
    public static final String RESOURCE_PREFIX = "/profile";
    public static final Integer CACHE_USER_PERMISSIONS_EXPIRE = 15;

    /**
     * 权限相关常量
     */
    public static final String ALL_PERMISSION = "*:*:*";
    /**
     * 系统默认标识常量
     */
    public static final String IS_SYSTEM_DEFAULT = "YES";

    /**
     * 菜单相关常量
     */
    public static final String IS_MENU_EXTERNAL_LINK = "0";
    public static final String IS_NOT_MENU_EXTERNAL_LINK = "1";
    public static final String MENU_TYPE_DIRECTORY = "M";
    public static final String MENU_TYPE_MENU = "C";
    public static final String MENU_TYPE_BUTTON = "F";

    /**
     * 组件标识常量
     */
    public static final String LAYOUT = "Layout";
    public static final String INNER_LINK = "InnerLink";

    /**
     * HTTP 相关常量
     */
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String WWW = "www.";


    /**
     * 其他常量
     */
    public static final long IS_PARENT_NODE = 0L;
    public static final Integer IS_CACHE = 1;
    public static final String CURRENT_DEFAULT_UPLOAD_TYPE = "currentDefaultUploadType";
    public static final int IS_FILE_UPLOAD_MASTER = 1;
    public static final String ORIGINAL = "original";
    public static final String PREVIEW = "preview";

    /**
     * 登录异常类型标识
     */
    public static final String LOGIN_EXCEPTION_ATTR = "LOGIN_EXCEPTION";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String NOT_LOGIN = "NOT_LOGIN";
    public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    public static final Long ROOT_NODE_ID = 0L;


    public interface CommonConstants {
        Integer ENABLE = 0;
        Integer DISABLE = 1;
    }

    /**
     * 菜单常量
     */
    public interface MenuConstants {
        /**
         * 顶级菜单ID
         */
        Long TOP_MENU_ID = 0L;
        /**
         * 是否为外链（0是 1否）
         */
        Integer IS_EXTERNAL_LINK = 0;
        Integer NOT_EXTERNAL_LINK = 1;
        /**
         * 路由组件
         */
        String LAYOUT = "Layout";
        String PARENT_VIEW = "ParentView";
        String INNER_LINK = "InnerLink";
        /**
         * 菜单类型（M目录 C菜单 F按钮）
         */
        String TYPE_DIRECTORY = "M";
        String TYPE_MENU = "C";
        String TYPE_BUTTON = "F";
        /**
         * 菜单状态（0显示 1隐藏）
         */
        String VISIBLE = "0";
        String HIDDEN = "1";
        /**
         * 菜单状态（0正常 1停用）
         */
        String STATUS_NORMAL = "0";
        String STATUS_DISABLED = "1";
        /**
         * 是否缓存（0缓存 1不缓存）
         */
        Integer CACHE = 0;
        Integer NOT_CACHE = 1;

        String STATUS_DISABLE = "1";
        String MENU_TYPE_DIR = "M";
    }
}
