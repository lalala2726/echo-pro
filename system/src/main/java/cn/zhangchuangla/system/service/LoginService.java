package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.request.LoginRequest;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 14:09
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param loginRequest 请求参数
     * @return 令牌
     */
    String login(LoginRequest loginRequest);

}

