package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.service.MinioOperationService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

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

    private final SysFileConfigLoader sysFileConfigLoader;

    @Autowired
    public MinioOperationServiceImpl(SysFileConfigLoader sysFileConfigLoader) {
        this.sysFileConfigLoader = sysFileConfigLoader;
    }

    @Override
    public FileTransferDto save(FileTransferDto fileTransferDto) {
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();
        MinioConfigEntity minioConfig = sysFileConfigLoader.getMinioConfig();
        if (minioConfig == null) {
            throw new ProfileException("Minio配置文件为空！请你检查配置文件是否存在？");
        }
        String endpoint = minioConfig.getEndpoint();
        String accessKey = minioConfig.getAccessKey();
        String secretKey = minioConfig.getSecretKey();
        String bucketName = minioConfig.getBucketName();
        String fileDomain = minioConfig.getFileDomain();
        HashMap<String, String> result = new HashMap<>();
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
                            .build()
            );

            String fileUrl = FileUtils.buildFinalPath(fileDomain, objectName);

            // 返回文件URL
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
