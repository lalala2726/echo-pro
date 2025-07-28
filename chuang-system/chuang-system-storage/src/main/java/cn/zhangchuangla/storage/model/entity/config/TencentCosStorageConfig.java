package cn.zhangchuangla.storage.model.entity.config;

import com.alibaba.fastjson2.JSON;
import lombok.Data;

/**
 * 腾讯云COS配置实体类
 *
 * @author Chuang
 */
@Data
public class TencentCosStorageConfig {

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
     * 是否真实删除
     */
    private boolean realDelete;

    /**
     * 转换为JSON字符串
     *
     * @return JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
