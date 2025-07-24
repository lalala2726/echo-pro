package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 授权认证异常
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class AuthorizationException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;

    public AuthorizationException() {
        super(ResultCode.AUTHORIZED.getMessage());
        this.code = ResultCode.AUTHORIZED.getCode();
    }

    public AuthorizationException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public AuthorizationException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public AuthorizationException(String message) {
        super(message);
        this.code = ResultCode.FORBIDDEN.getCode();
    }

}
