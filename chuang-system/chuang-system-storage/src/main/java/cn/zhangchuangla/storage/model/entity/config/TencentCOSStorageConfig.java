package cn.zhangchuangla.storage.model.entity.config;

import lombok.Data;

/**
 * 腾讯云COS配置实体类
 *
 * @author Chuang
 */
@Data
public class TencentCOSStorageConfig {

    /**
     * 服务区域
     */
    private String region;

    /**
     * 密钥ID
     */
    private String secretId;

    /**
     * 密钥Key
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 文件域名
     */
    private String fileDomain;

    /**
     * 是否使用回收站 1 使用 0 不使用
     */
    private Integer enableTrash;
}
