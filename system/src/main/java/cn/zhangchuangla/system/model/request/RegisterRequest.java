package cn.zhangchuangla.system.model.request;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 15:00
 */
@Data
public class RegisterRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String phone;
}
