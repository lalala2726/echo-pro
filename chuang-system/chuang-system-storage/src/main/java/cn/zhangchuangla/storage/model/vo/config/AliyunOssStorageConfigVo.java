package cn.zhangchuangla.storage.model.vo.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 阿里云OSS配置视图对象
 *
 * @author Chuang
 * <p>
 * created on 2025/7/20 22:06
 */
@Data
@Schema(description = "阿里云OSS配置视图对象")
public class AliyunOssStorageConfigVo {

    /**
     * 访问端点
     */
    @Schema(description = "访问端点")
    private String endpoint;

    /**
     * 阿里云账号AccessKey
     */
    @Schema(description = "阿里云账号AccessKey")
    private String accessKeyId;

    /**
     * 阿里云账号AccessKey Secret
     */
    @Schema(description = "阿里云账号AccessKey Secret")
    private String accessKeySecret;

    /**
     * 存储空间名称
     */
    @Schema(description = "存储空间名称")
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名")
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除")
    private Boolean realDelete;

}
