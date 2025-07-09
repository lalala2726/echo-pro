package cn.zhangchuangla.common.core.constant;

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
    /**
     * 权限相关常量
     */
    public static final String ALL_PERMISSION = "*:*:*";
    /**
     * HTTP 相关常量
     */
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String WWW = "www.";
    public static final String ORIGINAL = "original";
    public static final String PREVIEW = "preview";

    /**
     * 逻辑删除
     */
    public interface LogicDelete {
        //逻辑删除
        Integer DELETED = 1;
        //未删除
        Integer NOT_DELETED = 0;
    }

    /**
     * 字典相关常量
     */
    public interface DictConstants {
        // 字典启用
        Integer ENABLE_STATUS = 0;
        // 字典禁用
        Integer DISABLE_STATUS = 1;
    }

    public interface MessageConstants {
        //根据用户ID发送
        int SEND_METHOD_USER = 0;
        //根据角色ID发送
        int SEND_METHOD_ROLE = 1;
        //根据部门ID发送
        int SEND_METHOD_DEPT = 2;
        //全部发送
        int SEND_METHOD_ALL = 3;

    }

    /**
     * 代码生成相关常量
     */
    public interface Generator {

        //主键
        String IS_PK = "1";
        String YES = "1";
        String NO = "0";
        String EQ = "eq";
        String NOT_EQUAL = "ne";
        String GREATER_THAN = "gt";
        String GREATER_THAN_OR_EQUAL_TO = "ge";
        String LESS_THAN = "lt";
        String LESS_THAN_OR_EQUAL_TO = "le";
        String BETWEEN = "between";
        String IN = "in";
        String NOT_IN = "not in";
        String LIKE = "like";
        String CRUD = "crud";
        String TREE = "tree";
        String SUB = "sub";
    }

    /**
     * 菜单常量
     */
    public interface MenuConstants {

        /**
         * 目录
         */
        String TYPE_DIR = "catalog";

        /**
         * 菜单
         */
        String TYPE_MENU = "menu";

        /**
         * 按钮
         */
        String TYPE_BUTTON = "button";

        /**
         * 内嵌
         */
        String TYPE_INTERNAL = "embedded";

        /**
         * 外链
         */
        String TYPE_EXTERNAL = "link";
    }
}
