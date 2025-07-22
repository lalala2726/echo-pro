package cn.zhangchuangla.storage.model.vo.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 文件管理列表视图对象
 *
 * @author Chuang
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorageFileListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String originalName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    @Schema(description = "文件类型，如 image/jpeg, application/pdf 等")
    private String contentType;

    /**
     * 压缩文件URL，用于图片预览等场景
     */
    private String previewImageUrl;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private String fileSize;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名")
    private String fileExtension;

    /**
     * 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型 (LOCAL/MINIO/ALIYUN_OSS)")
    private String storageType;

    /**
     * 存储桶名称（OSS/MINIO 使用）
     */
    @Schema(description = "存储桶名称（OSS/MINIO 使用）")
    private String bucketName;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称")
    private String uploaderName;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "上传时间")
    private Date uploadTime;


}
