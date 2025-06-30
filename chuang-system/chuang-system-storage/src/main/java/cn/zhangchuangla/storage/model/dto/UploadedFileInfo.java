package cn.zhangchuangla.storage.model.dto;

import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/28 21:05
 */
@Data
public class UploadedFileInfo {

    /**
     * 文件名
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
     * 文件MD5
     */
    private String md5;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件扩展名
     */
    private String fileExtension;


}
