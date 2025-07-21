package cn.zhangchuangla.storage.model.vo.config;

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
    private Long id;

    /**
     * 参数名称
     */
    private String storageName;

    /**
     * 参数键名
     */
    private String storageKey;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 是否主配置
     */
    private Boolean isPrimary;

    /**
     * 阿里云OSS配置
     */
    private AliyunOssStorageConfigVo aliyunOssStorageConfigVo;

    /**
     * 腾讯云COS配置
     */
    private AmazonS3StorageConfigVo amazonS3StorageConfigVo;

    /**
     * Minio配置
     */
    private MinioStorageConfigVo minioStorageConfigVo;

    /**
     * 腾讯云COS配置
     */
    private TencentCosStorageConfigVo tencentCosStorageConfigVo;


}
