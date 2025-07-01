package cn.zhangchuangla.storage.model.request.file;

import cn.zhangchuangla.common.core.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件上传记录表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "文件管理列表请求参数")
public class FileRecordQueryRequest extends BasePageRequest {

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
     * 文件大小，格式化后的字符串，如 "1.5MB"
     */
    @Schema(description = "文件大小，格式化后的字符串，如 \"1.5MB\"")
    private String fileSize;

    /**
     * 文件MD5值，用于文件完整性校验
     */
    @Schema(description = "文件MD5值，用于文件完整性校验")
    private String fileMd5;

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
     * 上传者ID
     */
    @Schema(description = "上传者ID")
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称")
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private Date uploadTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;
}
