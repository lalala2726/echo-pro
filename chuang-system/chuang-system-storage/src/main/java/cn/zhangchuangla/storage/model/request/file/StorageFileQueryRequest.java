package cn.zhangchuangla.storage.model.request.file;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
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
public class StorageFileQueryRequest extends BasePageRequest {

    /**
     * 文件名
     */
    @Schema(description = "文件名", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2022-01-01.png")
    private String originalName;

    /**
     * 文件类型，如 image/jpeg, application/pdf 等
     */
    @Schema(description = "文件类型", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "image/jpeg")
    private String contentType;

    /**
     * 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     */
    @Schema(description = "存储类型", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "LOCAL")
    private String storageType;

    /**
     * 存储桶名称（OSS/MINIO 使用）
     */
    @Schema(description = "存储桶名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "bucketName")
    private String bucketName;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "张三")
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2023-01-01 00:00:00")
    private Date uploadTime;
}
