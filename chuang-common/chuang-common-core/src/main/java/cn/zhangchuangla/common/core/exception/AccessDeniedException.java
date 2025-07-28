package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * 授权失败异常
 *
 * @author Chuang
 * <p>
 * created on 2025/7/27 23:03
 */
@Getter
public final class AccessDeniedException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;

    public AccessDeniedException() {
        super(ResultCode.FORBIDDEN.getMessage());
        this.code = ResultCode.FORBIDDEN.getCode();
    }

    public AccessDeniedException(String message) {
        super(message);
        this.code = ResultCode.FORBIDDEN.getCode();
    }

    public AccessDeniedException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = ResultCode.FORBIDDEN.getCode();
    }


}
