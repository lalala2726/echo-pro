package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 授权认证异常
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class AccountException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public AccountException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public AccountException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public AccountException(String message) {
        super(message);
        this.code = ResponseCode.AUTHORIZED.getCode();
    }

}
