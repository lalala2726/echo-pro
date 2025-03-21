package cn.zhangchuangla.common.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * minio 配置实体类
 *
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:41
 */
@Data
@Schema(description = "minio 配置实体类")
public class MinioConfigEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 254651044373823297L;


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
     * 存储桶域名
     */
    @Schema(description = "存储桶域名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存储桶域名不能为空")
    private String bucketUrl;

    /**
     * 存储桶区域
     */
    @Schema(description = "存储桶区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存储桶区域不能为空")
    private String bucketRegion;


}
