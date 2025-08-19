package cn.zhangchuangla.common.core.utils;

import cn.zhangchuangla.common.core.constant.RolesConstant;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 安全工具类
 *
 * @author Chuang
 */
@Slf4j
public class SecurityUtils {

    /**
     * 默认令牌头部名称
     */
    private static final String DEFAULT_HEADER = "Authorization";

    /**
     * 默认令牌前缀
     */
    private static final String DEFAULT_TOKEN_PREFIX = "Bearer";

    /**
     * 获取用户
     *
     * @return LoginUser
     */
    public static SysUserDetails getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SysUserDetails)) {
            throw new ServiceException(ResultCode.UNAUTHORIZED, "用户未登录");
        }
        return (SysUserDetails) authentication.getPrincipal();
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
     * 判断密码是否相同,这边原始的密码是明文的不需要额外加密
     *
     * @param rawPassword     真实密码(未加密的密码)
     * @param encodedPassword 加密后字符
     * @return true代表相同，false代表不同
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    /**
     * 是否拥有某个角色
     *
     * @return true代表有，false代表无
     */
    public static boolean hasRole(String role) {
        return getRoles().contains(role);
    }


    /**
     * 获取当前请求对象
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return getLoginUser().getUserId();
    }

    /**
     * 判断是否为超级管理员
     */
    public static boolean isSuperAdmin() {
        Set<String> roles = getRoles();
        return roles.contains(RolesConstant.SUPER_ADMIN);
    }

    /**
     * 获取用户角色集合
     *
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .filter(CollectionUtils::isNotEmpty)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                // 筛选角色,authorities 中的角色都是以 ROLE_ 开头
                .filter(authority -> authority.startsWith(SecurityConstants.ROLE_PREFIX))
                .map(authority -> Strings.CS.removeStart(authority, SecurityConstants.ROLE_PREFIX))
                .collect(Collectors.toSet());
    }

    /**
     * 获取当前请求的 Token
     */
    public static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String header = request.getHeader(DEFAULT_HEADER);
        if (header != null && header.startsWith(DEFAULT_TOKEN_PREFIX.trim() + " ")) {
            header = header.substring(DEFAULT_TOKEN_PREFIX.length()).trim();
        }
        return header;
    }

    /**
     * 获取当前请求的Request
     *
     * @return request
     */
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}
