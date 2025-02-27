package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.core.model.entity.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/26 22:16
 */
public class SecurityUtils {


    /**
     * 获取用户
     *
     * @return LoginUser
     */
    public static LoginUser getLoginUser() {
        return (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

}
