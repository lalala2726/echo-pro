package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/26 22:16
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取用户
     *
     * @return LoginUser
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            throw new ServiceException("获取用户信息失败");
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
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
