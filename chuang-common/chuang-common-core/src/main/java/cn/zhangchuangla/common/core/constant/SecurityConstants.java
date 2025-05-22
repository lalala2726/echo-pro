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
     * 接口白名单,设置后不需要认证直接可以访问，注意！一旦设置白名单系统中需要获取用户的信息将失效！
     */
    public static final String[] WHITELIST = {
            "/auth/login",
            "/captcha",
            "/register",
            "/auth/refreshToken",
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
