package cn.zhangchuangla.storage.model.vo.config;

import cn.zhangchuangla.storage.model.entity.config.AliyunOSSStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.TencentCOSStorageConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件配置表
 *
 * @author Chuang
 */
@Data
@Schema(description = "文件配置列表视图")
public class StorageFileConfigListVo {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String storageName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名")
    private String storageKey;

    /**
     * 存储类型
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 是否主配置 1-是 0-否
     */
    @Schema(description = "是否主配置，1-是 0-否")
    private Integer isMaster;


    /**
     * Minio配置
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MinioStorageConfig minioConfig;

    /**
     * 阿里云配置
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AliyunOSSStorageConfig aliyunOssConfig;

    /**
     * 腾讯云配置
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TencentCOSStorageConfig tencentCosConfig;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

}
