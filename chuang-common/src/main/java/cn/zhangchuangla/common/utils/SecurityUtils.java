package cn.zhangchuangla.common.utils;

import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 安全工具类
 *
 * @author Chuang
 */
@Slf4j
public class SecurityUtils {

    /**
     * 获取当前登录用户
     *
     * @return SysUserDetails 用户详情
     */
    public static SysUserDetails getLoginUser() {
        try {
            Authentication authentication = getAuthentication();
            if (authentication == null) {
                log.warn("获取用户信息失败：认证对象为空");
                return null;
            }

            Object principal = authentication.getPrincipal();
            if (principal instanceof SysUserDetails) {
                return (SysUserDetails) principal;
            }

            log.warn("获取用户信息失败：Principal类型不匹配，实际类型: {}",
                    principal != null ? principal.getClass().getName() : "null");
            return null;
        } catch (Exception e) {
            log.error("获取登录用户异常", e);
            return null;
        }
    }


    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return true代表相同，false代表不同
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.debug("当前线程安全上下文中Authentication为空");
        }
        return authentication;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        SysUserDetails userDetails = getLoginUser();
        return userDetails != null ? userDetails.getUsername() : "";
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        SysUserDetails userDetails = getLoginUser();
        return userDetails != null ? userDetails.getUserId() : null;
    }


    /**
     * 获取当前请求的 Token
     */
    public static String getTokenFromRequest() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    /**
     * 获取当前请求的Request
     *
     * @return request
     */
    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
