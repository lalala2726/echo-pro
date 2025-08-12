package cn.zhangchuangla.system.storage.model.vo.config;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Minio配置视图对象
 *
 * @author Chuang
 * <p>
 * created on 2025/7/20 22:06
 */
@Data
@Schema(description = "Minio配置视图对象")
public class MinioStorageConfigVo {


    /**
     * 访问端点
     */
    @Schema(description = "访问端点", type = "string", example = "http://localhost:9000")
    @Excel(name = "访问端点", sort = 1)
    private String endpoint;

    /**
     * 访问密钥
     */
    @Schema(description = "访问密钥", type = "string", example = "minioadmin")
    @Excel(name = "访问密钥", sort = 2)
    private String accessKey;

    /**
     * 密钥
     */
    @Schema(description = "密钥", type = "string", example = "minioadmin")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", type = "string", example = "my-bucket")
    @Excel(name = "存储桶名称", sort = 3)
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名", type = "string", example = "http://localhost:9000/my-bucket")
    @Excel(name = "文件访问域名", sort = 4)
    private String fileDomain;

    /**
     * 存储桶区域
     */
    @Schema(description = "存储桶区域", type = "string", example = "us-east-1")
    @Excel(name = "存储桶区域", sort = 5)
    private String bucketRegion;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除", type = "boolean", example = "true")
    @Excel(name = "是否真实删除", sort = 6)
    private Boolean realDelete;


}
