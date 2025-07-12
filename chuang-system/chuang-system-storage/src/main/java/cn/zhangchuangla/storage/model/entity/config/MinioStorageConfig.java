package cn.zhangchuangla.storage.model.entity.config;

import cn.zhangchuangla.common.core.annotation.DataMasking;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * minio 配置实体类
 *
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:41
 */
@Data
@Schema(description = "minio 配置实体类")
public class MinioStorageConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 254651044373823297L;

    /**
     * 访问端点
     */
    private String endpoint;

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 密钥
     */
    @DataMasking(prefixKeep = 3, suffixKeep = 3)
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 文件访问域名
     */
    private String fileDomain;

    /**
     * 存储桶区域
     */
    private String bucketRegion;

    /**
     * 是否真实删除
     */
    private boolean realDelete;


}
