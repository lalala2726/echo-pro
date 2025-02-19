package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.MinioConfig;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.system.service.MinioFileService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangchuang
 * Created on 2025/2/17 15:40
 */
@Slf4j
@Service
public class MinioFileServiceImpl implements MinioFileService {

    private final MinioConfig minioConfig;


    public MinioFileServiceImpl(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }


    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问路径
     */
    @Override
    public String uploadFile(MultipartFile file) {

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

            // 生成唯一的文件名并按年月生成目录
            String fileName = FileUtils.generateUUID();
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String objectName = datePath + "/" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            // 返回文件的访问路径
            return fileDomain + "/" + objectName;
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new FileException("文件上传失败");
        }
    }
}
