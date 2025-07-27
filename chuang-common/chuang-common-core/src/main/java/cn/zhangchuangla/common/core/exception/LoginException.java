package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
import lombok.Getter;

import static cn.zhangchuangla.common.core.enums.ResultCode.LOGIN_ERROR;


/**
 * 登录失败
 *
 * @author Chuang
 * <p>
 * created on 2025/7/27 23:23
 */
@Getter
public final class LoginException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;

    public LoginException() {
        super(LOGIN_ERROR.getMessage());
        this.code = LOGIN_ERROR.getCode();
    }

    public LoginException(ResultCode resultCode, String message) {
        super(message);
        this.code = LOGIN_ERROR.getCode();
    }

    public LoginException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public LoginException(String message) {
        super(message);
        this.code = LOGIN_ERROR.getCode();
    }

}
