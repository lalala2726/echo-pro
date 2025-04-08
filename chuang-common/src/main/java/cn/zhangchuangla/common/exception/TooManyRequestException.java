package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class TooManyRequestException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public TooManyRequestException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public TooManyRequestException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public TooManyRequestException(String message) {
        super(message);
        this.code = ResponseCode.PROFILE_ERROR.getCode();
    }

}
