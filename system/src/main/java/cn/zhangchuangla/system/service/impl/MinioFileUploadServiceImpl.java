package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.system.service.MinioFileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:28
 */
@Service
@Slf4j
public class MinioFileUploadServiceImpl implements MinioFileUploadService {

    private final ConfigCacheService configCacheService;

    @Autowired
    public MinioFileUploadServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }


    @Override
    public String minioUploadBytes(byte[] data, String fileName, String contentType) {
        MinioConfigEntity minioConfig = configCacheService.getMinioConfig();
        String endpoint = minioConfig.getEndpoint();
        String accessKey = minioConfig.getAccessKey();
        String secretKey = minioConfig.getSecretKey();
        String bucketName = minioConfig.getBucketName();
        String fileDomain = minioConfig.getFileDomain();

        try {
            // 创建Minio客户端
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            // 检查存储桶是否存在
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                // 如果存储桶不存在，则创建
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            // 生成存储路径
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String objectName = datePath + "/" + FileUtils.generateUUID();

            // 如果有扩展名，添加扩展名
            if (fileName.contains(".")) {
                objectName += fileName.substring(fileName.lastIndexOf("."));
            }

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType(contentType)
                            .build()
            );

            // 返回文件URL
            return fileDomain + "/" + objectName;
        } catch (Exception e) {
            log.error("MinIO上传失败: {}", e.getMessage(), e);
        }
        return endpoint;
    }
}
