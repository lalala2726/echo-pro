package cn.zhangchuangla.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/20 21:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "AmazonS3配置添加请求参数")
public class AmazonS3ConfigSaveRequest extends StorageConfigBaseSaveRequest {

    /**
     * 存储服务地址
     */
    @Schema(description = "存储服务地址", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://s3.amazonaws.com")
    @NotNull(message = "AmazonS3的存储服务地址不能为空")
    private String endpoint;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "AKIAIOSFODNN7EXAMPLE")
    @NotNull(message = "AmazonS3的访问密钥不能为空")
    private String accessKey;

    /**
     * 密钥
     */
    @Schema(description = "密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "AKIAIOSFODNN7EXAMPLE")
    @NotNull(message = "AmazonS3的密钥不能为空")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "bucketName")
    @NotNull(message = "AmazonS3的存储桶名称不能为空")
    private String bucketName;

    /**
     * 存储桶区域
     */
    @Schema(description = "存储桶区域", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "us-east-1")
    @NotNull(message = "AmazonS3的存储桶区域不能为空")
    private String region;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com")
    @NotNull(message = "AmazonS3的文件访问域名不能为空")
    private String fileDomain;

    /**
     * 是否真实删除文件
     */
    @Schema(description = "是否真实删除文件", type = "boolean", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "true")
    private boolean realDelete = true;
}
