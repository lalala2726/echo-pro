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
}
