package cn.zhangchuangla.infrastructure.security.context;

import cn.zhangchuangla.common.core.model.entity.LoginUser;

/**
 * 身份验证信息
 * 用于存储当前用户的身份信息
 */
public class AuthenticationContextHolder {
    private static final ThreadLocal<LoginUser> contextHolder = new ThreadLocal<>();

    public static LoginUser getContext() {
        return contextHolder.get();
    }

    public static void setContext(LoginUser context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
