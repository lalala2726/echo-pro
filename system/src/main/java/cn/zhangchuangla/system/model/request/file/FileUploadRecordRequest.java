package cn.zhangchuangla.system.model.request.file;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 文件上传记录表
 */
@Data
@Schema(description = "文件管理请求类")
public class FileUploadRecordRequest extends BasePageRequest {


    /**
     * 文件名称
     */
    @Schema(description = "文件名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileName;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String originalFileName;

    /**
     * 文件存储路径
     */
    @Schema(description = "文件存储路径", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String filePath;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileUrl;

    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long fileSize;

    /**
     * 文件类型/MIME类型
     */
    @Schema(description = "文件类型/MIME类型", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileType;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileExtension;

    /**
     * 存储类型(LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型(LOCAL/MINIO/ALIYUN_OSS)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storageType;

    /**
     * 存储桶名称(OSS/MINIO使用)
     */
    @Schema(description = "存储桶名称(OSS/MINIO使用)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String bucketName;

    /**
     * 文件MD5值
     */
    @Schema(description = "文件MD5值", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String md5;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date uploadTime;

    /**
     * 是否删除(0-未删除,1-已删除)
     */
    @Schema(description = "是否删除(0-未删除,1-已删除)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Date updateTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String createBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String updateBy;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}