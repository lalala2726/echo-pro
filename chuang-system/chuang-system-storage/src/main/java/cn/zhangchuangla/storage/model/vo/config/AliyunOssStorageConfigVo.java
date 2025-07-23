package cn.zhangchuangla.storage.model.vo.config;

import cn.zhangchuangla.common.excel.annotation.Excel;
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
    @Excel(name = "访问端点", sort = 1)
    private String endpoint;

    /**
     * 阿里云账号AccessKey
     */
    @Schema(description = "阿里云账号AccessKey")
    @Excel(name = "AccessKey", sort = 2)
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
    @Excel(name = "存储空间名称", sort = 3)
    private String bucketName;

    /**
     * 文件访问域名
     */
    @Schema(description = "文件访问域名")
    @Excel(name = "文件访问域名", sort = 4)
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除")
    @Excel(name = "是否真实删除", sort = 5)
    private Boolean realDelete;

}
