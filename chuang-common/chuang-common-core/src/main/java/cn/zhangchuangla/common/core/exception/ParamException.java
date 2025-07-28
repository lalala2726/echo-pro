package cn.zhangchuangla.common.core.exception;

import cn.zhangchuangla.common.core.enums.ResultCode;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 11:32
 */
@Getter
public class ParamException extends RuntimeException {

    private final Integer code;

    public ParamException(String message) {
        super(message);
        this.code = ResultCode.PARAM_ERROR.getCode();
    }

    public ParamException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public ParamException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ParamException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }


}
