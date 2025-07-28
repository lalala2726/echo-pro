package cn.zhangchuangla.system.storage.exception;

/**
 * Custom exception for storage-related errors.
 *
 * @author Chuang
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
