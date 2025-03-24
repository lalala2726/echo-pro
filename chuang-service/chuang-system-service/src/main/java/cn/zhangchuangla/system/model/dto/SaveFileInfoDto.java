package cn.zhangchuangla.system.model.dto;

import cn.zhangchuangla.common.entity.file.FileInfo;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhangchuang
 * Created on 2025/3/22 22:42
 */
@Data
@Builder
public class SaveFileInfoDto {

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 压缩文件访问URL
     */
    private String compressedUrl;

    /**
     * 文件信息
     */
    private FileInfo fileInfo;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 原始文件相对路径
     */
    private String originalRelativeFileLocation;

    /**
     * 预览图片相对路径
     */
    private String previewRelativeFileLocation;
}
