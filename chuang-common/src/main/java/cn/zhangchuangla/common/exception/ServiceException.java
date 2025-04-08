package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class ServiceException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public ServiceException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public ServiceException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public ServiceException(String message) {
        super(message);
        this.code = ResponseCode.ERROR.getCode();
    }

}
