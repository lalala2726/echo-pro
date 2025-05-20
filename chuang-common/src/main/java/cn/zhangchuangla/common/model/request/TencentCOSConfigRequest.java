package cn.zhangchuangla.common.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 腾讯云COS配置实体类
 *
 * @author Chuang
 */
@Data
@Schema(name = "腾讯云COS配置请求类", description = "腾讯云COS配置请求类")
public class TencentCOSConfigRequest {

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
     * 服务区域
     */
    @Schema(description = "服务区域", requiredMode = Schema.RequiredMode.REQUIRED)
    private String region;

    /**
     * 密钥ID
     */
    @Schema(description = "密钥ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String secretId;

    /**
     * 密钥Key
     */
    @Schema(description = "密钥Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bucketName;

    /**
     * 文件域名
     */
    @Schema(description = "文件域名,如果为空将返回相对路径")
    private String fileDomain;
}
