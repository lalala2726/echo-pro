package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class FileException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public FileException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public FileException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

}
