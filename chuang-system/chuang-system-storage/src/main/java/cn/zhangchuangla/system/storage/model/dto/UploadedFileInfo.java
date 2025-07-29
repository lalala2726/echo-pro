package cn.zhangchuangla.system.storage.model.dto;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/28 21:05
 */
@Data
public class UploadedFileInfo {

    /**
     * 文件新名字
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 文件原始名称
     */
    private String fileOriginalName;

    /**
     * 文件相对路径
     */
    private String fileRelativePath;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;

    /**
     * 预览图片(图片资源特有)
     */
    private String previewImage;

    /**
     * 预览图片相对路径(图片资源特有)
     */
    private String previewImageRelativePath;

    /**
     * 存储桶名称（OSS/MINIO 使用）
     */
    private String bucketName;

}
