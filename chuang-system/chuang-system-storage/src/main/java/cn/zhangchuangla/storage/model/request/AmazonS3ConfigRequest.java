package cn.zhangchuangla.storage.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 亚马逊S3存储配置请求类
 * <p>
 * 兼容标准S3协议的存储服务配置，支持：
 * <ul>
 *   <li>亚马逊S3原生服务</li>
 *   <li>其他兼容S3协议的存储服务（如华为云OBS、七牛云Kodo等）</li>
 *   <li>私有化部署的S3兼容存储</li>
 * </ul>
 *
 * @author Chuang
 * @since 2025/7/4
 */
@Data
@Schema(description = "亚马逊S3存储配置请求类")
public class AmazonS3ConfigRequest {

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
     * 存储服务地址端点
     * <p>
     * 对于兼容S3的服务：具体的服务端点地址
     */
    @Schema(description = "存储服务端点地址", example = "https://s3.amazonaws.com")
    @NotBlank(message = "存储服务端点地址不能为空")
    private String endpoint;

    /**
     * 访问密钥ID
     */
    @Schema(description = "访问密钥ID", example = "AKIAIOSFODNN7EXAMPLE")
    @NotBlank(message = "访问密钥ID不能为空")
    private String accessKey;

    /**
     * 访问密钥密码
     */
    @Schema(description = "访问密钥密码")
    @NotBlank(message = "访问密钥密码不能为空")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "my-bucket")
    @NotBlank(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 存储桶区域
     * <p>
     * AWS S3区域代码，如：us-east-1、us-west-2、ap-northeast-1等
     * 对于兼容S3的服务，使用相应的区域配置
     */
    @Schema(description = "存储桶区域", example = "us-east-1")
    private String region;

    /**
     * 文件访问域名
     * <p>
     * 用于构建文件的公开访问URL
     */
    @Schema(description = "文件访问域名", example = "https://cdn.example.com")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除")
    private boolean realDelete;
}
