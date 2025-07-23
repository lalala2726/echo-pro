package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
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


    public ProfileException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public ProfileException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public ProfileException(String message) {
        super(message);
        this.code = ResultCode.PROFILE_ERROR.getCode();
    }

    public ProfileException() {
        super(ResultCode.PROFILE_ERROR.getMessage());
        this.code = ResultCode.PROFILE_ERROR.getCode();
    }

}
