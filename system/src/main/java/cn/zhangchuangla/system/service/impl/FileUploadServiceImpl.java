package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.AliyunOSSConfig;
import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.config.MinioConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.system.service.FileUploadService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author zhangchuang
 * Created on 2025/3/21 23:43
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    private final ConfigCacheService configCacheService;

    public FileUploadServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }


    /**
     * 文件上传到阿里云OSS
     *
     * @param file MultipartFile
     * @return 文件访问路径
     */
    @Override
    public String AliyunOssFileUpload(MultipartFile file) {
        // 验证文件是否有效
        if (!FileUtils.validateFile(file)) {
            log.error("文件上传失败：文件为空或无效");
            throw new FileException(ResponseCode.FileNameIsNull);
        }

        AliyunOSSConfigEntity aliyunOSSConfig = configCacheService.getAliyunOSSConfig();

        // 获取 OSS 配置
        String bucketName = aliyunOSSConfig.getBucketName();
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String fileDomain = aliyunOSSConfig.getFileDomain();
        String bucketPath = aliyunOSSConfig.getBucketPath();

        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        // 生成存储路径
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = FileUtils.generateUUID() + FileUtils.getFileExtension(file.getOriginalFilename());

        // 拼接完整路径：bucketPath/日期路径/文件名
        // 确保 bucketPath 格式正确，避免多余的斜杠
        String normalizedBucketPath = normalizePath(bucketPath);
        String uploadPath = normalizedBucketPath.isEmpty() ? datePath : normalizedBucketPath + "/" + datePath;
        String uploadFileName = uploadPath + "/" + fileName;

        // 配置文件元数据（确保浏览器可直接预览）
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setHeader("Content-Disposition", "inline"); // 预览模式
        metadata.setContentType(FileUtils.determineContentType(file));

        // 执行上传
        try (InputStream inputStream = file.getInputStream()) {
            log.info("开始上传文件到阿里云OSS，存储路径: {}", uploadFileName);
            PutObjectResult result = ossClient.putObject(bucketName, uploadFileName, inputStream, metadata);

            String fileUrl = fileDomain + uploadFileName;
            log.info("文件上传成功，访问地址: {}", fileUrl);

            return result != null ? fileUrl : null;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return null;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 规范化路径格式
     * - 去除开头和结尾的斜杠
     * - 处理空值情况
     */
    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        // 去除开头和结尾的斜杠
        String normalizedPath = path.trim();
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }

        return normalizedPath;
    }

    /**
     * 文件上传到Minio
     *
     * @param file 文件
     * @return 文件访问路径
     */
    @Override
    public String MinioFileUpload(MultipartFile file) {
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

    /**
     * 本地文件上传
     *
     * @param multipartFile 文件
     * @return 文件访问路径
     */
    @Override
    public String localFileUpload(MultipartFile multipartFile) {
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        String uploadPath = localFileConfig.getUploadPath();
        String originalFileName = multipartFile.getOriginalFilename();

        try {
            // 获取原始文件名并提取后缀
            if (originalFileName == null) {
                throw new FileException(ResponseCode.FileNameIsNull);
            }
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // 生成年月格式的目录
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
            String yearMonthDir = yearMonthFormat.format(new Date());

            // 生成新的文件名
            String uuidFileName = String.format("%s%s",
                    System.currentTimeMillis(),
                    UUID.randomUUID().toString().substring(0, 8));

            // 只保留小写字母和数字
            Pattern pattern = Pattern.compile("[^a-z0-9]");
            uuidFileName = pattern.matcher(uuidFileName).replaceAll("");

            // 设置目标路径，包含年月目录
            Path path = Paths.get(uploadPath + File.separator + yearMonthDir + File.separator + uuidFileName + fileExtension);
            // 创建目录，如果不存在则创建
            Files.createDirectories(path.getParent());
            // 将文件保存到目标路径
            multipartFile.transferTo(path.toFile());

            return Constants.RESOURCE_PREFIX + "/" + yearMonthDir + "/" + uuidFileName + fileExtension;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }
}
