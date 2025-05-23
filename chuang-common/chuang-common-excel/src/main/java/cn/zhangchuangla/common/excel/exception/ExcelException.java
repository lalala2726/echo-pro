package cn.zhangchuangla.common.excel.exception;

import cn.zhangchuangla.common.core.enums.ResponseCode;

/**
 * @author chuang
 */

public class ExcelException extends RuntimeException {

    /**
     * 状态码
     */
    private final Integer code;


    public ExcelException(ResponseCode responseCode, String message) {
        super(message);
        this.code = responseCode.getCode();
    }

    public ExcelException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }

    public ExcelException(String message) {
        super(message);
        this.code = ResponseCode.EXCEL_ERROR.getCode();
    }

}
