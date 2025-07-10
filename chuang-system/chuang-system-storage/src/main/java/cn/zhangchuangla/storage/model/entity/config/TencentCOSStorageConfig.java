package cn.zhangchuangla.storage.model.entity.config;

import cn.zhangchuangla.common.core.annotation.DataMasking;
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
    @DataMasking(prefixKeep = 3, suffixKeep = 3)
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
     * 是否真实删除
     */
    private boolean realDelete;
}
