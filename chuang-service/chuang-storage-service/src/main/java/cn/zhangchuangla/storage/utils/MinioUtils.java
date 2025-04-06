package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * Minio存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
public class MinioUtils {

    /**
     * 上传文件到Minio
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, MinioConfigEntity minioConfigEntity) {
        if (minioConfigEntity == null) throw new ProfileException("Minio配置文件为空！请你检查配置文件是否存在？");
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        String endpoint = minioConfigEntity.getEndpoint();
        String accessKey = minioConfigEntity.getAccessKey();
        String secretKey = minioConfigEntity.getSecretKey();
        String bucketName = minioConfigEntity.getBucketName();
        String fileDomain = minioConfigEntity.getFileDomain();
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
            String datePath = FileUtils.generateYearMonthDir();
            // 获取文件扩展名
            String fileExtension = FileUtils.getFileExtension(fileName);
            // 组合最终路径: 日期/文件夹类型/文件名
            String uuid = FileUtils.generateUUID();
            String objectName = FileUtils.buildFinalPath(datePath, uuid + fileExtension);

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .build());

            // 构建文件URL
            String fileUrl = "";
            if (!StringUtils.isEmpty(fileDomain)) {
                fileUrl = FileUtils.buildFinalPath(fileDomain, objectName);
            }
            // 返回文件URL
            return FileTransferDto.builder()
                    .fileUrl(fileUrl)
                    .relativePath(objectName)
                    .build();
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }
}
