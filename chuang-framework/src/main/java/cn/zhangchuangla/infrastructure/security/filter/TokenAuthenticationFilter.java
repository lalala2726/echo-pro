package cn.zhangchuangla.infrastructure.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.SecurityConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.utils.ResponseUtils;
import cn.zhangchuangla.infrastructure.security.token.TokenManager;
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
import java.util.stream.Stream;

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
                boolean isValidToken = tokenManager.validateToken(authorizationHeader);
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

        // 获取白名单数组
        String[] staticResourcesWhitelist = SecurityConstants.STATIC_RESOURCES_WHITELIST;
        String[] swaggerWhitelist = SecurityConstants.SWAGGER_WHITELIST;
        String[] whitelist = SecurityConstants.WHITELIST;

        // 合并所有白名单路径
        String[] combinedWhitelist = Stream.of(staticResourcesWhitelist, swaggerWhitelist, whitelist)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);

        // 路径匹配器（支持 Ant 风格匹配）
        AntPathMatcher pathMatcher = new AntPathMatcher();

        // 判断当前请求路径是否匹配白名单规则
        return Arrays.stream(combinedWhitelist)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

}
