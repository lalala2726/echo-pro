package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.storage.entity.FileTransferDto;
import cn.zhangchuangla.storage.service.MinioOperationService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * Minio 操作服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:03
 */
@Service
@Slf4j
public class MinioOperationServiceImpl implements MinioOperationService {

    private final ConfigCacheService configCacheService;

    public MinioOperationServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }

    @Override
    public FileTransferDto save(FileTransferDto fileTransferDto) {
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

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

            // 生成年月目录
            String yearMonthDir = FileUtils.generateYearMonthDir();

            // 获取文件扩展名
            String fileExtension = FileUtils.getFileExtension(fileName);

            // 生成唯一文件名
            String uuidFileName = FileUtils.generateFileName();

            // 构建对象名称（包含路径和扩展名）
            String objectName = FileUtils.buildFinalPath(yearMonthDir, uuidFileName + fileExtension);

            // 获取内容类型
            String contentType = FileUtils.generateFileContentType(fileName);

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType(contentType)
                            .build());

            // 返回文件URL
            String fileUrl = fileDomain + "/" + objectName;
            return FileTransferDto.builder()
                    .fileUrl(fileUrl) // 修正字段名称
                    .relativePath(objectName)
                    .build();
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }
}
