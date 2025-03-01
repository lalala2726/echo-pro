package cn.zhangchuangla.common.constant;

import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/26 19:22
 */
@Component("systemRolesConstant")
public class SystemRolesConstant {

    /**
     * 超级管理员
     */
    public static final String SUPER_ADMIN = "super_admin";

    /**
     * 管理员
     */
    public static final String ADMIN = "admin";

    /**
     * 用户
     */
    public static final String USER = "user";

    /**
     * 访客
     */
    public static final String GUEST = "guest";
}
