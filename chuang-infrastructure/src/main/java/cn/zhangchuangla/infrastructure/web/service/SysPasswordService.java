package cn.zhangchuangla.infrastructure.web.service;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/25 15:41
 */
public interface SysPasswordService {


    /**
     * 密码错误尝试次数校验,如果超过设置的错误次数则会禁止登录,如果继续登录则会锁定IP
     *
     * @param username 用户名
     */
    void PasswordErrorCount(String username);

}
