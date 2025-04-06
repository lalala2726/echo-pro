package cn.zhangchuangla.common.model.entity;

import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.model.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:55
 */
@Data
@Component
public class UploadProperties {

    /**
     * 文件上传类型
     */
    private String fileUploadType;

    /**
     * 阿里云OSS配置文件
     */
    private AliyunOSSConfigEntity aliyunOSSConfigEntity;

    /**
     * 本地上传配置文件
     */
    private LocalFileConfigEntity localFileConfigEntity;

    /**
     * Minio上传配置文件
     */
    private MinioConfigEntity minioConfigEntity;
}
