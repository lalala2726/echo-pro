package cn.zhangchuangla.common.exception;

import cn.zhangchuangla.common.enums.ResponseCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:04
 */
public final class ServiceException extends RuntimeException{

    /**
     * 状态码
     */
    private final Integer code;


    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(ResponseCode responseCode, String message){
        super(message);
        this.code = responseCode.getCode();
    }

    public ServiceException(ResponseCode responseCode){
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public Integer getCode() {
        return code;
    }

}
