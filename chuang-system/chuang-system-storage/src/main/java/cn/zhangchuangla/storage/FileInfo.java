package cn.zhangchuangla.storage;

import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object for file information.
 *
 * @author Chuang
 */
@Data
@Builder
public class FileInfo {
    // 原始文件名
    private String originalFileName;
    // 存储系统中的文件名（通常带有路径）
    private String newFileName;
    // 文件的可访问URL
    private String url;
    // 文件在存储桶或本地文件系统中的相对路径
    private String relativePath;
    // 文件大小 (bytes)
    private Long size;
    // 文件类型 (MIME type)
    private String contentType;
    // 存储类型
    private StorageType storageType;

    // Fields for image uploads, if applicable
    // 缩略图URL (if generated)
    private String thumbnailUrl;
    // 缩略图在存储中的相对路径 (if generated)
    private String thumbnailPath;

    // Fields for trash functionality
    // 原始文件在回收站中的路径
    private String originalTrashPath;
    // 缩略图在回收站中的路径
    private String thumbnailTrashPath;

}
