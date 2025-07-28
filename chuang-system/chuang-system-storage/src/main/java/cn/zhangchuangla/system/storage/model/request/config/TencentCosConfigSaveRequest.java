package cn.zhangchuangla.system.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/20 21:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "腾讯云COS 配置添加请求参数")
public class TencentCosConfigSaveRequest extends StorageConfigBaseSaveRequest {

    /**
     * 访问区域
     */
    @Schema(description = "访问区域", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "ap-guangzhou")
    @NotNull(message = "访问区域不能为空")
    private String region;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "AKIDSDKSDKDDDWEWEWE")
    @NotNull(message = "访问密钥不能为空")
    private String secretId;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "SDKSDKDDDWEWEWE")
    @NotNull(message = "访问密钥不能为空")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "chuang-system-storage")
    @NotNull(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://chuang-system-storage.oss-cn-beijing.aliyuncs.com")
    @NotNull(message = "文件访问域名不能为空")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除", type = "boolean", requiredMode = Schema.RequiredMode.AUTO, example = "true")
    private boolean realDelete = true;

}
