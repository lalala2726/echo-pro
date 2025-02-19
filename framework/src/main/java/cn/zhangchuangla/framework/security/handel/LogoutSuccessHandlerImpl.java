package cn.zhangchuangla.framework.security.handel;

import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ServletUtils;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:45
 */
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    /**
     * 退出成功处理
     *
     * @param request        请求
     * @param response       响应
     * @param authentication 认证
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //todo 记录用户退出日志,删除在Redis中缓存的token
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success()));
    }
}
