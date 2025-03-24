package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 11:32
 */
public class ParamException extends RuntimeException {

    private final Integer code;

    public ParamException(String message) {
        super(message);
        this.code = ResponseCode.PARAM_ERROR.getCode();
    }

    public ParamException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public ParamException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ParamException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }


}
