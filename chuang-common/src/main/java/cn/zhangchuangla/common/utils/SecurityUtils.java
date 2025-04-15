package cn.zhangchuangla.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.LoginException;
import cn.zhangchuangla.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;
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
     * 获取用户
     *
     * @return LoginUser
     */
    public static SysUserDetails getLoginUser() {
        try {
            // 检查请求属性中是否有登录异常信息
            HttpServletRequest request = getRequest();
            if (request != null) {
                String loginException = (String) request.getAttribute(Constants.LOGIN_EXCEPTION_ATTR);
                if (loginException != null) {
                    handleLoginException(loginException);
                }
            }

            Authentication authentication = getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null) {
                throw new LoginException(ResponseCode.USER_NOT_LOGIN, "用户未登录");
            }

            // 判断Principal是否为LoginUser类型
            if (authentication.getPrincipal() instanceof SysUserDetails) {
                return (SysUserDetails) authentication.getPrincipal();
            } else {
                throw new ServiceException("获取用户信息失败");
            }
        } catch (LoginException e) {
            throw e;
        } catch (Exception e) {
            throw new LoginException(ResponseCode.USER_NOT_LOGIN, "用户未登录");
        }
    }

    /**
     * 处理登录异常信息
     */
    private static void handleLoginException(String loginException) {
        switch (loginException) {
            case Constants.TOKEN_EXPIRED:
                throw new LoginException(ResponseCode.TOKEN_EXPIRED, "会话已过期，请重新登录");
            case Constants.INVALID_TOKEN:
                throw new LoginException(ResponseCode.INVALID_TOKEN, "无效的令牌");
            case Constants.SYSTEM_ERROR:
                throw new ServiceException("系统错误，请联系管理员");
            case Constants.NOT_LOGIN:
            default:
                throw new LoginException(ResponseCode.USER_NOT_LOGIN, "用户未登录");
        }
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
        return getLoginUser().getSysUser().isSuperAdmin();
    }

    /**
     * 获取用户角色集合
     *
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .filter(CollectionUtil::isNotEmpty)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                // 筛选角色,authorities 中的角色都是以 ROLE_ 开头
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> StrUtil.removePrefix(authority, "ROLE_"))
                .collect(Collectors.toSet());
    }
}
