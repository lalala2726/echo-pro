package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.framework.model.request.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/20 10:46
 */
public interface SysLoginService {

    /**
     * 登录
     *
     * @param loginRequest 登录参数
     * @return 返回token
     */
    String login(LoginRequest loginRequest, HttpServletRequest request);
}
