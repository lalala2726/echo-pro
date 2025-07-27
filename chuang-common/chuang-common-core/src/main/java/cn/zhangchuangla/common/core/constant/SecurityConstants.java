package cn.zhangchuangla.common.core.constant;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/15 16:01
 */
public class SecurityConstants {

    /**
     * 角色前缀，用于区分 authorities 角色和权限， ROLE_* 角色 、没有前缀的是权限
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * 设备类型
     */
    public static final String DEVICE_TYPE = "device_type";

    /**
     * 设备名称
     */
    public static final String DEVICE_NAME = "device_name";

    /**
     * 登录时间
     */
    public static final String LOGIN_TIME = "login_time";

    public static final String USER_NAME = "username";

    public static final String USER_ID = "userId";

    /**
     * 登录IP
     */
    public static final String IP = "ip";

    /**
     * 登录位置
     */
    public static final String LOCATION = "location";

    public static final String CLAIM_KEY_SESSION_ID = "session";
    public static final String CLAIM_KEY_USERNAME = "username";


    /**
     * 接口白名单,设置后不需要认证直接可以访问，注意！一旦设置白名单系统中需要获取用户的信息将失效！
     */
    public static final String[] WHITELIST = {
            "/auth/login",
            "/captcha",
            "/register",
            "/auth/refresh",
    };

    /**
     * 静态资源白名单
     */
    public static final String[] STATIC_RESOURCES_WHITELIST = {
            "/static/**",
            "/profile/**",
            "/**.html",
            "/**.css",
            "/**.js",
            "/favicon.ico"
    };

    /**
     * 接口文档接口白名单
     */
    public static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/**",
            "/webjars/**"
    };
}
