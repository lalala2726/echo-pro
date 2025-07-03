package cn.zhangchuangla.storage.model.entity.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 亚马逊S3存储配置实体类
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
@Schema(description = "亚马逊S3存储配置实体类")
public class AmazonS3StorageConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -3456789012345678901L;

    /**
     * 存储服务地址端点
     * <p>
     * 对于AWS S3：https://s3.amazonaws.com 或 https://s3.region.amazonaws.com
     * 对于兼容S3的服务：具体的服务端点地址
     */
    @Schema(description = "存储服务端点地址", example = "https://s3.amazonaws.com")
    private String endpoint;

    /**
     * 访问密钥ID
     */
    @Schema(description = "访问密钥ID", example = "AKIAIOSFODNN7EXAMPLE")
    private String accessKey;

    /**
     * 访问密钥密码
     */
    @Schema(description = "访问密钥密码")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "my-bucket")
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
     * 是否开启回收站功能
     * <p>
     * 0: 关闭回收站，删除文件时直接物理删除
     * 1: 开启回收站，删除文件时移动到回收站目录
     */
    @Schema(description = "是否开启回收站 0:关闭 1:开启")
    private Integer enableTrash;
}
