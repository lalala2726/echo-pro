package cn.zhangchuangla.framework.security.filter;

import cn.zhangchuangla.common.config.TokenConfig;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AuthenticationException;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.web.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * token认证拦截器
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:47
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;

    private final TokenService tokenService;

    private final RedisCache redisCach;

    public JwtAuthenticationTokenFilter(TokenConfig tokenConfig, TokenService tokenService, RedisCache redisCach) {
        this.tokenConfig = tokenConfig;
        this.tokenService = tokenService;
        this.redisCach = redisCach;
    }

    /**
     * 拦截请求，判断请求头中是否包含token，并且验证token是否正确
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     * @throws ServletException servlet异常
     * @throws IOException      io异常
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = tokenService.getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                // 获取用户身份信息
                LoginUser loginUser = tokenService.getLoginUser(request);
                if (loginUser == null) {
                    log.warn("Token验证失败：无法获取用户信息");
                    throw new AuthenticationException(ResponseCode.TOKEN_EXPIRE, "Token已过期或无效");
                }

                // 从Redis获取用户信息
                LoginUser redisLoginUser = redisCach.getCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + loginUser.getUserId());
                if (redisLoginUser == null) {
                    log.warn("Token验证失败：Redis中未找到用户信息，userId={}", loginUser.getUserId());
                    throw new AuthenticationException(ResponseCode.TOKEN_EXPIRE, "登录状态已过期，请重新登录");
                }

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 将用户信息存入SecurityContext
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(redisLoginUser, null, redisLoginUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.debug("用户认证成功，已将用户信息存入SecurityContext，userId={}", redisLoginUser.getUserId());
                }
            } catch (AuthenticationException ae) {
                throw ae;
            } catch (Exception e) {
                log.error("Token验证过程中发生异常", e);
                throw new AuthenticationException(ResponseCode.TOKEN_EXPIRE, "Token验证失败");
            }
        }
        filterChain.doFilter(request, response);
    }
}
