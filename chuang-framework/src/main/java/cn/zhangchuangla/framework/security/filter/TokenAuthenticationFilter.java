package cn.zhangchuangla.framework.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.SecurityConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.utils.ResponseUtils;
import cn.zhangchuangla.framework.security.token.TokenManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Token认证拦截器
 *
 * @author Chuang
 */
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenManager tokenManager;

    public TokenAuthenticationFilter(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }


    /**
     * 校验Token，包括验签和是否过期
     *
     * @param request     请求参数
     * @param response    响应参数
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            if (StrUtil.isNotBlank(authorizationHeader)) {

                // 执行令牌有效性检查（包含密码学验签和过期时间验证）
                boolean isValidToken = tokenManager.validateAccessToken(authorizationHeader);
                log.info("当前请求令牌有效性：{}", isValidToken);
                if (!isValidToken) {
                    ResponseUtils.writeErrMsg(response, ResponseCode.ACCESS_TOKEN_INVALID);
                    return;
                }

                // 将令牌解析为 Spring Security 上下文认证对象
                Authentication authentication = tokenManager.parseToken(authorizationHeader);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 安全上下文清除保障（防止上下文残留）
            SecurityContextHolder.clearContext();
            ResponseUtils.writeErrMsg(response, ResponseCode.ACCESS_TOKEN_INVALID);
            return;
        }

        // 继续后续过滤器链执行
        filterChain.doFilter(request, response);
    }

    /**
     * 此方法用于排除不需要过滤的请求，防止过滤器对某些请求进行处理。
     *
     * @param request current HTTP request
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        AntPathMatcher pathMatcher = new AntPathMatcher();

        // 检查普通白名单
        if (isPathMatchAny(path, SecurityConstants.WHITELIST, pathMatcher)) {
            return true;
        }

        // 检查静态资源白名单
        if (isPathMatchAny(path, SecurityConstants.STATIC_RESOURCES_WHITELIST, pathMatcher)) {
            return true;
        }

        // 检查Swagger白名单
        return isPathMatchAny(path, SecurityConstants.SWAGGER_WHITELIST, pathMatcher);
    }

    /**
     * 检查路径是否匹配任一白名单规则
     *
     * @param path        请求路径
     * @param patterns    匹配模式数组
     * @param pathMatcher 路径匹配器
     * @return 是否匹配
     */
    private boolean isPathMatchAny(String path, String[] patterns, AntPathMatcher pathMatcher) {
        return Arrays.stream(patterns)
                .anyMatch(pattern -> pathMatcher.match(pattern, path.trim()));
    }

}
