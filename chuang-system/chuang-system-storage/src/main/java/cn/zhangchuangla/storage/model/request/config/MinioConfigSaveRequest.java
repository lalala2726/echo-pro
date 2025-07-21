package cn.zhangchuangla.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/20 21:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "MinIO 配置添加请求参数")
public class MinioConfigSaveRequest extends StorageConfigBaseSaveRequest {

    /**
     * MinIO 服务器的端点。
     */
    @Schema(description = "MinIO 服务器的端点", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "http://192.168.0.1:9000")
    @NotNull(message = "MinIO 服务器的端点不能为空")
    private String endpoint;

    /**
     * MinIO 的访问密钥。
     */
    @Schema(description = "MinIO 的访问密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "minioAccessKey")
    @NotNull(message = "MinIO 的访问密钥不能为空")
    private String accessKey;

    /**
     * MinIO 的密钥。
     */
    @Schema(description = "MinIO 的密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "minioSecretKey")
    @NotNull(message = "MinIO 的密钥不能为空")
    private String secretKey;

    /**
     * MinIO 的存储桶名称。
     */
    @Schema(description = "MinIO 的存储桶名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "minioBucketName")
    @NotNull(message = "MinIO 的存储桶名称不能为空")
    private String bucketName;

    /**
     * MinIO 的文件访问域名。
     */
    @Schema(description = "MinIO 的文件访问域名", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "minioFileDomain")
    @NotNull(message = "MinIO 的文件访问域名不能为空")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除", type = "boolean", requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "true")
    private boolean realDelete = true;

}
