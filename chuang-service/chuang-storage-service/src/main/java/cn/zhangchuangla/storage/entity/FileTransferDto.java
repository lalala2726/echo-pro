package cn.zhangchuangla.storage.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 文件传输对象
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:32
 */
@Data
@Builder
public class FileTransferDto {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件字节数组
     */
    private byte[] bytes;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件相对路径
     */
    private String relativePath;
}
