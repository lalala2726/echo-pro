package cn.zhangchuangla.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/20 21:05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "阿里云OSS 配置添加请求参数")
public class AliyunOssConfigSaveRequest extends StorageConfigBaseSaveRequest {

    /**
     * 访问端点
     */
    @Schema(description = "访问端点", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://oss-cn-hangzhou.aliyuncs.com")
    @NotNull(message = "阿里云OSS的访问端点不能为空")
    private String endpoint;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "LTAI5tKZyXyXyXyXyXyXyXyX")
    @NotNull(message = "阿里云OSS的访问密钥不能为空")
    private String accessKeyId;

    /**
     * 密钥
     */
    @Schema(description = "密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "LTAI5tKZyXyXyXyXyXyXyXyX")
    @NotNull(message = "阿里云OSS的密钥不能为空")
    private String accessKeySecret;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "bucketName")
    @NotNull(message = "阿里云OSS的存储桶名称不能为空")
    private String bucketName;

    /**
     * 文件域名
     */
    @Schema(description = "文件域名", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com")
    @NotNull(message = "阿里云OSS的文件访问域名不能为空")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除", type = "boolean", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "true")
    private boolean realDelete = true;
}
