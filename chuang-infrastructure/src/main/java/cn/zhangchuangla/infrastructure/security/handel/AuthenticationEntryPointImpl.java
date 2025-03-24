package cn.zhangchuangla.infrastructure.security.handel;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ServletUtils;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

/**
 * 认证失败处理类
 * 该类用于处理认证失败的情况，返回相应的错误信息。
 *
 * @author Chuang
 * created on 2025/2/19 17:18
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    @Serial
    private static final long serialVersionUID = -1052402770074774341L;

    /**
     * 认证失败处理
     *
     * @param request       请求
     * @param response      响应
     * @param authException 认证异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        ServletUtils.renderString(response, JSON.toJSONString(
                AjaxResult.error(ResponseCode.UNAUTHORIZED, "认证失败，无法访问系统资源 ")));
    }
}
