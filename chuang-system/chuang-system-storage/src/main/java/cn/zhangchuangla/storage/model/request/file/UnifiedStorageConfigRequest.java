package cn.zhangchuangla.storage.model.request.file;

import cn.zhangchuangla.common.core.constant.RegularConstants;
import cn.zhangchuangla.storage.enums.StorageType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 统一存储配置请求类
 * 支持所有存储类型的配置：本地存储、MinIO、阿里云OSS、腾讯云COS、亚马逊S3
 *
 * @author Chuang
 * @since 2025/1/7
 */
@Data
@Schema(name = "统一存储配置请求对象", description = "支持所有存储类型的统一配置请求对象")
public class UnifiedStorageConfigRequest {

    /**
     * 存储类型
     */
    @Schema(description = "存储类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "存储类型不能为空")
    private StorageType storageType;

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

    // ========== 通用字段 ==========

    /**
     * 访问端点/服务地址
     * - MinIO/阿里云OSS/亚马逊S3: endpoint
     * - 腾讯云COS: region
     */
    @Schema(description = "访问端点/服务地址")
    private String endpoint;

    /**
     * 访问密钥ID
     * - MinIO: accessKey
     * - 阿里云OSS: accessKeyId
     * - 腾讯云COS: secretId
     * - 亚马逊S3: accessKey
     */
    @Schema(description = "访问密钥ID")
    private String accessKeyId;

    /**
     * 访问密钥密码
     * - MinIO: secretKey
     * - 阿里云OSS: accessKeySecret
     * - 腾讯云COS: secretKey
     * - 亚马逊S3: secretKey
     */
    @Schema(description = "访问密钥密码")
    private String accessKeySecret;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名,如果为空将返回相对路径")
    @Pattern(regexp = RegularConstants.Storage.DOMAIN, message = "文件访问域名格式不正确")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除")
    private boolean realDelete;

    // ========== 特定字段 ==========

    /**
     * 区域信息
     * - 亚马逊S3: region
     * - 腾讯云COS: region (同endpoint)
     */
    @Schema(description = "区域信息")
    private String region;

    /**
     * 验证请求参数
     */
    public void validate() {
        switch (storageType) {
            case MINIO:
                validateRequired(endpoint, "MinIO访问端点不能为空");
                validateRequired(accessKeyId, "MinIO访问密钥不能为空");
                validateRequired(accessKeySecret, "MinIO密钥不能为空");
                validateRequired(bucketName, "MinIO存储桶名称不能为空");
                validateDomain(endpoint, "MinIO访问端点域名格式不正确");
                validateBucketName(bucketName, "MinIO存储桶名称格式不正确");
                break;
            case ALIYUN_OSS:
                validateRequired(endpoint, "阿里云OSS访问端点不能为空");
                validateRequired(accessKeyId, "阿里云账号AccessKey不能为空");
                validateRequired(accessKeySecret, "阿里云账号AccessKey Secret不能为空");
                validateRequired(bucketName, "阿里云OSS存储空间名称不能为空");
                validateDomain(endpoint, "阿里云OSS访问端点域名格式不正确");
                break;
            case TENCENT_COS:
                validateRequired(endpoint, "腾讯云COS服务区域不能为空");
                validateRequired(accessKeyId, "腾讯云COS密钥ID不能为空");
                validateRequired(accessKeySecret, "腾讯云COS密钥Key不能为空");
                validateRequired(bucketName, "腾讯云COS存储桶名称不能为空");
                // 腾讯云COS的region和endpoint是同一个值
                if (region == null || region.trim().isEmpty()) {
                    region = endpoint;
                }
                break;
            case AMAZON_S3:
                validateRequired(endpoint, "亚马逊S3存储服务端点地址不能为空");
                validateRequired(accessKeyId, "亚马逊S3访问密钥ID不能为空");
                validateRequired(accessKeySecret, "亚马逊S3访问密钥密码不能为空");
                validateRequired(bucketName, "亚马逊S3存储桶名称不能为空");
                break;
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    private void validateDomain(String domain, String message) {
        if (domain != null && !domain.trim().isEmpty()) {
            if (!domain.matches(RegularConstants.Storage.DOMAIN)) {
                throw new IllegalArgumentException(message);
            }
        }
    }

    private void validateBucketName(String bucketName, String message) {
        if (bucketName != null && !bucketName.trim().isEmpty()) {
            if (!bucketName.matches(RegularConstants.Storage.BUCKET_NAME)) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
