package cn.zhangchuangla.common.excel.exception;

/**
 * Excel操作异常
 *
 * @author Chuang
 * @since 2025-01-23
 */
public class ExcelException extends RuntimeException {

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExcelException(Throwable cause) {
        super(cause);
    }
}
