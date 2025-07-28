package cn.zhangchuangla.storage.model.vo.config;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/20 22:13
 */
@Schema(description = "文件存储配置配置统一视图对象")
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StorageConfigUnifiedVo {

    /**
     * 主键
     */
    @Schema(description = "主键")
    @Excel(name = "主键", sort = 1)
    private Long id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    @Excel(name = "参数名称", sort = 2)
    private String storageName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名")
    @Excel(name = "参数键名", sort = 3)
    private String storageKey;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    @Excel(name = "存储类型", sort = 4)
    private String storageType;

    /**
     * 是否主配置
     */
    @Schema(description = "是否主配置")
    @Excel(name = "是否主配置", sort = 5)
    private Boolean isPrimary;

    /**
     * 阿里云OSS配置
     */
    @Schema(description = "阿里云OSS配置")
    @Excel(name = "阿里云OSS配置", expandObject = true, expandPrefix = "阿里云-", expandIsNullExport = false, sort = 10)
    private AliyunOssStorageConfigVo aliyunOssStorageConfigVo;

    /**
     * 亚马逊S3配置
     */
    @Schema(description = "亚马逊S3配置")
    @Excel(name = "亚马逊S3配置", expandObject = true, expandPrefix = "S3-", sort = 20)
    private AmazonS3StorageConfigVo amazonS3StorageConfigVo;

    /**
     * Minio配置
     */
    @Schema(description = "Minio配置")
    @Excel(name = "Minio配置", expandObject = true, expandPrefix = "Minio-", sort = 30)
    private MinioStorageConfigVo minioStorageConfigVo;

    /**
     * 腾讯云COS配置
     */
    @Schema(description = "腾讯云COS配置")
    @Excel(name = "腾讯云COS配置", expandObject = true, expandPrefix = "腾讯云-", sort = 40)
    private TencentCosStorageConfigVo tencentCosStorageConfigVo;


}
