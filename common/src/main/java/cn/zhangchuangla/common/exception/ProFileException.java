package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 配置文件异常
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
@Getter
public final class ProFileException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public ProFileException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public ProFileException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public ProFileException(String message) {
        super(message);
        this.code = ResponseCode.PROFILE_ERROR.getCode();
    }

    public ProFileException() {
        super(ResponseCode.PROFILE_ERROR.getMessage());
        this.code = ResponseCode.PROFILE_ERROR.getCode();
    }

}
