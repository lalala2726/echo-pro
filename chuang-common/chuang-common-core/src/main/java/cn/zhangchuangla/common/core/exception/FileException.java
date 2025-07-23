package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
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


    public FileException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public FileException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public FileException(String message) {
        super(message);
        this.code = ResultCode.PROFILE_ERROR.getCode();
    }


}
