package cn.zhangchuangla.common.model.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 阿里云OSS配置实体类
 *
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:40
 */
@Data
@Schema(description = "阿里云OSS配置实体类")
public class AliyunOSSConfigEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 6412320965763904069L;

    /**
     * 访问端点
     */
    private String endpoint;


    /**
     * 阿里云账号AccessKey
     */
    private String accessKeyId;

    /**
     * 阿里云账号AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 存储空间名称
     */
    private String bucketName;

    /**
     * 文件访问域名
     */
    private String fileDomain;

    /**
     * 是否开启回收站 0:关闭 1:开启
     */
    private Integer enableTrash;


}
