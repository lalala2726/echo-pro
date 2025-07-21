package cn.zhangchuangla.storage.model.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 亚马逊云S3配置视图对象
 *
 * @author Chuang
 * <p>
 * created on 2025/7/20 22:06
 */
@Data
@Schema(description = "亚马逊云S3配置视图对象")
public class AmazonS3StorageConfigVo {

    /**
     * 存储服务地址端点
     * <p>
     * 对于兼容S3的服务：具体的服务端点地址
     */
    @Schema(description = "存储服务地址端点")
    private String endpoint;

    /**
     * 访问密钥ID
     */
    @Schema(description = "访问密钥ID")
    private String accessKey;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 存储桶区域
     * <p>
     * AWS S3区域代码，如：us-east-1、us-west-2、ap-northeast-1等
     * 对于兼容S3的服务，使用相应的区域配置
     */
    @Schema(description = "存储桶区域")
    private String region;

    /**
     * 文件访问域名
     * <p>
     * 用于构建文件的公开访问URL
     */
    @Schema(description = "文件访问域名")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除")
    private Boolean realDelete;

}
