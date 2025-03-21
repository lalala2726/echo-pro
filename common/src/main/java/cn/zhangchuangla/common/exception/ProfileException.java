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
public final class ProfileException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public ProfileException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public ProfileException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public ProfileException(String message) {
        super(message);
        this.code = ResponseCode.PROFILE_ERROR.getCode();
    }

    public ProfileException() {
        super(ResponseCode.PROFILE_ERROR.getMessage());
        this.code = ResponseCode.PROFILE_ERROR.getCode();
    }

}
