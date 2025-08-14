package cn.zhangchuangla.system.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/20 22:50
 */
@Data
@Schema(description = "文件配置更新请求参数")
public class StorageConfigUpdateRequest {

    @Schema(description = "文件配置ID", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "文件配置ID不能为空")
    private Long id;

    /**
     * 存储配置名称
     */
    @Schema(description = "文件配置标志", type = "string", example = "北京阿里云对象存储")
    @NotBlank(message = "文件配置标志不能为空")
    private String storageName;

    /**
     * 阿里云存储配置
     */
    @Schema(description = "阿里云OSS配置")
    private AliyunOssConfigSaveRequest aliyunOss;

    /**
     * AmazonS3 存储配置
     */
    @Schema(description = "MinIO 配置")
    private AmazonS3ConfigSaveRequest amazonS3;

    /**
     * 腾讯云COS 存储配置
     */
    @Schema(description = "腾讯云COS配置")
    private TencentCosConfigSaveRequest tencentCos;

    /**
     * 阿里云OSS 存储配置
     */
    @Schema(description = "MinIO 配置")
    private MinioConfigSaveRequest minio;


}
