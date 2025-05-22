package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResponseCode;
import lombok.Getter;

/**
 * 授权认证异常
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class LoginException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public LoginException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public LoginException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public LoginException(String message) {
        super(message);
        this.code = ResponseCode.LOGIN_ERROR.getCode();
    }

}
