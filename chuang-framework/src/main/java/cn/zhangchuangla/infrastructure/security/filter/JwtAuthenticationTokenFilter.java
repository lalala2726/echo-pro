package cn.zhangchuangla.infrastructure.security.filter;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.LoginException;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.infrastructure.web.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Token认证拦截器
 *
 * @author Chuang
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtAuthenticationTokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 获取token
        String token = tokenService.getToken(request);

        if (StringUtils.isNotBlank(token)) {
            try {
                // 验证token并获取用户信息
                LoginUser loginUser = tokenService.getLoginUser(request);

                if (loginUser != null) {
                    // 验证token有效期
                    tokenService.validateToken(loginUser);

                    // 设置认证信息
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            loginUser, null, loginUser.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (LoginException e) {
                // 根据异常类型设置请求属性
                if (Objects.equals(e.getCode(), ResponseCode.TOKEN_EXPIRED.getCode())) {
                    request.setAttribute(Constants.LOGIN_EXCEPTION_ATTR, Constants.TOKEN_EXPIRED);
                } else if (Objects.equals(e.getCode(), ResponseCode.INVALID_TOKEN.getCode())) {
                    request.setAttribute(Constants.LOGIN_EXCEPTION_ATTR, Constants.INVALID_TOKEN);
                } else {
                    request.setAttribute(Constants.LOGIN_EXCEPTION_ATTR, Constants.NOT_LOGIN);
                }
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                request.setAttribute(Constants.LOGIN_EXCEPTION_ATTR, Constants.SYSTEM_ERROR);
                SecurityContextHolder.clearContext();
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
