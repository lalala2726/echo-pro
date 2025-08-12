package cn.zhangchuangla.system.storage.model.vo.config;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 腾讯云COS配置视图对象
 *
 * @author Chuang
 */
@Data
@Schema(description = "腾讯云COS配置视图对象")
public class TencentCosStorageConfigVo {

    /**
     * 服务区域
     */
    @Schema(description = "服务区域", type = "string", example = "ap-beijing")
    @Excel(name = "服务区域", sort = 1)
    private String region;

    /**
     * 密钥ID
     */
    @Schema(description = "密钥ID", type = "string", example = "AKIxxxxxxxxxxxxxxxx")
    @Excel(name = "密钥ID", sort = 2)
    private String secretId;

    /**
     * 密钥Key
     */
    @Schema(description = "密钥Key", type = "string", example = "xxxxxxxxxxxxxxxxxxxxxxxxxxxx")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", type = "string", example = "my-bucket-1250000000")
    @Excel(name = "存储桶名称", sort = 3)
    private String bucketName;

    /**
     * 文件域名
     */
    @Schema(description = "文件域名", type = "string", example = "https://my-bucket-1250000000.cos.ap-beijing.myqcloud.com")
    @Excel(name = "文件域名", sort = 4)
    private String fileDomain;

    /**
     * 是否真实删除
     */
    @Schema(description = "是否真实删除", type = "boolean", example = "true")
    @Excel(name = "是否真实删除", sort = 5)
    private boolean realDelete;

}
