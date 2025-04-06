package cn.zhangchuangla.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^(https?://)?((([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(:(\\d+))?(/[^/]*)?$",
            message = "访问端点域名格式不正确")
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
    @Pattern(regexp = "^[a-z0-9][a-z0-9.-]{1,61}[a-z0-9]$",
            message = "存储桶名称只能包含小写字母、数字、点号和短横线，且必须以小写字母或数字开头和结尾，长度在 3 到 63 个字符之间")
    @NotBlank(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件访问域名不能为空")
    @Pattern(regexp = "^(https?://)?((([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})|((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(:(\\d+))?(/[^/]*)?$",
            message = "文件访问域名格式不正确")
    private String fileDomain;


}
