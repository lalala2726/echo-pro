package cn.zhangchuangla.framework.model.request;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:56
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

}
