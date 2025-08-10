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
    public static final Integer ACCOUNT_UNLOCK_KEY = 0;


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
     * 逻辑删除
     */
    public interface LogicDelete {
        //逻辑删除
        Integer DELETED = 1;
        //未删除
        Integer NOT_DELETED = 0;
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
