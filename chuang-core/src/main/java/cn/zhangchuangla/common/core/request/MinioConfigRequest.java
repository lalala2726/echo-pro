package cn.zhangchuangla.common.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * minio 配置实体类
 *
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:41
 */
@Data
@Schema(description = "minio 配置实体类")
public class MinioConfigRequest {


    /**
     * 文件配置标志
     */
    @Schema(description = "文件配置标志", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件配置标志不能为空")
    private String storageName;


    /**
     * 参数键名
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键名不能为空")
    private String storageKey;


    /**
     * 访问端点
     */
    @Schema(description = "访问端点", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "访问端点不能为空")
    private String endpoint;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "访问密钥不能为空")
    private String accessKey;

    /**
     * 密钥
     */
    @Schema(description = "密钥", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密钥不能为空")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件访问域名不能为空")
    private String fileDomain;

    /**
     * 存储桶区域
     */
    @Schema(description = "存储桶区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存储桶区域不能为空")
    private String bucketRegion;


}
