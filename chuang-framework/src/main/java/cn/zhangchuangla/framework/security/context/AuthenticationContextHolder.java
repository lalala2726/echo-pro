package cn.zhangchuangla.framework.security.context;

import cn.zhangchuangla.common.core.core.security.model.SysUserDetails;

/**
 * 身份验证信息
 * 用于存储当前用户的身份信息
 */
public class AuthenticationContextHolder {
    private static final ThreadLocal<SysUserDetails> contextHolder = new ThreadLocal<>();

    public static SysUserDetails getContext() {
        return contextHolder.get();
    }

    public static void setContext(SysUserDetails context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
