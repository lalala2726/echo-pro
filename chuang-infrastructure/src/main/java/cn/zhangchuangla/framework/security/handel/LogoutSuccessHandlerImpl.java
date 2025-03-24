package cn.zhangchuangla.framework.security.handel;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ServletUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.framework.web.service.TokenService;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:45
 */
@Slf4j
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final TokenService tokenService;
    private final RedisCache redisCache;

    public LogoutSuccessHandlerImpl(TokenService tokenService, RedisCache redisCache) {
        this.tokenService = tokenService;
        this.redisCache = redisCache;
    }

    /**
     * 退出成功处理
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 认证
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String sessionId = loginUser.getSessionId();
            Long userId = loginUser.getUserId();
            //删除用户缓存记录
            redisCache.deleteObject(RedisKeyConstant.LOGIN_TOKEN_KEY + sessionId);
            //删除用户权限缓存
            redisCache.deleteObject(RedisKeyConstant.PASSWORD_ERROR_COUNT + userId);
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success()));
    }
}
