package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.ImageUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.URLUtils;
import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import cn.zhangchuangla.system.model.response.FileUploadResult;
import cn.zhangchuangla.system.service.FileUploadRecordService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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
 * 文件上传服务实现类
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    // 图片压缩配置
    private static final int DEFAULT_MAX_WIDTH = 800;
    private static final int DEFAULT_MAX_HEIGHT = 800;
    private static final float DEFAULT_QUALITY = 0.75f;
    private final ConfigCacheService configCacheService;
    private final FileUploadRecordService fileUploadRecordService;

    @Autowired
    public FileUploadServiceImpl(ConfigCacheService configCacheService, FileUploadRecordService fileUploadRecordService) {
        this.configCacheService = configCacheService;
        this.fileUploadRecordService = fileUploadRecordService;
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

    /**
     * 智能上传，如果是图片等资源将会返回两个两个URL，分别是原始URL和压缩URL
     *
     * @param file        上传的文件
     * @param storageType 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     * @return 返回文件上传结果
     */
    @Override
    public FileUploadResult uploadWithImageProcess(MultipartFile file, String storageType) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "上传文件不能为空");
        }

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        // 构建结果对象
        FileUploadResult result = FileUploadResult.builder()
                .fileName(originalFilename)
                .fileType(contentType)
                .originalSize(file.getSize())
                .build();

        try {
            // 检查是否为图片
            boolean isImage = ImageUtils.isImage(contentType);
            result.setImage(isImage);

            // 上传原始文件
            String originalUrl = uploadFile(file, storageType);
            result.setOriginalUrl(originalUrl);

            // 如果是图片文件，进行压缩处理
            if (isImage) {
                // 压缩图片
                byte[] imageBytes = file.getBytes();
                byte[] compressedBytes = ImageUtils.compressImage(
                        imageBytes, DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT, DEFAULT_QUALITY);

                // 设置压缩文件大小
                result.setCompressedSize(compressedBytes.length);

                // 生成压缩文件名
                String compressedFileName = FileUtils.generateCompressedFileName(originalFilename);

                // 直接上传压缩后的字节数组
                String compressedUrl = uploadByteArray(
                        compressedBytes,
                        compressedFileName,
                        contentType,
                        storageType
                );

                result.setCompressedUrl(compressedUrl);

                // 保存压缩文件记录
                if (fileUploadRecordService != null) {
                    // 创建文件记录但不使用MockMultipartFile
                    saveCompressedFileRecord(
                            compressedUrl,
                            compressedFileName,
                            contentType,
                            compressedBytes.length,
                            storageType
                    );
                }
            }

            // 保存原始文件记录
            if (fileUploadRecordService != null) {
                fileUploadRecordService.saveFileInfo(originalUrl, result.getCompressedUrl(), file, storageType);
            }

            return result;
        } catch (IOException e) {
            log.error("文件处理异常: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "文件处理失败: " + e.getMessage());
        }
    }

    /**
     * 根据存储类型选择合适的上传方法
     */
    private String uploadFile(MultipartFile file, String storageType) {
        if (Constants.LOCAL_FILE_UPLOAD.equals(storageType)) {
            return localFileUpload(file);
        } else if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
            return MinioFileUpload(file);
        } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
            return AliyunOssFileUpload(file);
        } else {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "不支持的存储类型: " + storageType);
        }
    }

    /**
     * 根据存储类型上传字节数组
     */
    private String uploadByteArray(byte[] data, String fileName, String contentType, String storageType) throws IOException {
        if (Constants.LOCAL_FILE_UPLOAD.equals(storageType)) {
            return localUploadBytes(data, fileName, contentType);
        } else if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
            return minioUploadBytes(data, fileName, contentType);
        } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
            return aliyunOssUploadBytes(data, fileName, contentType);
        } else {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "不支持的存储类型: " + storageType);
        }
    }

    /**
     * 本地上传字节数组
     */
    private String localUploadBytes(byte[] data, String fileName, String contentType) throws IOException {
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        String uploadPath = localFileConfig.getUploadPath();

        // 生成年月格式的目录
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
        String yearMonthDir = yearMonthFormat.format(new Date());

        // 生成唯一文件名
        String fileExtension = "";
        if (fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        }

        String uuidFileName = String.format("%s%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8));

        // 只保留小写字母和数字
        Pattern pattern = Pattern.compile("[^a-z0-9]");
        uuidFileName = pattern.matcher(uuidFileName).replaceAll("");

        // 设置目标路径，包含年月目录
        Path directory = Paths.get(uploadPath + File.separator + yearMonthDir);
        Path filePath = directory.resolve(uuidFileName + fileExtension);

        // 创建目录，如果不存在则创建
        Files.createDirectories(directory);

        // 写入文件
        Files.write(filePath, data);

        return Constants.RESOURCE_PREFIX + "/" + yearMonthDir + "/" + uuidFileName + fileExtension;
    }

    /**
     * MinIO上传字节数组
     */
    private String minioUploadBytes(byte[] data, String fileName, String contentType) throws IOException {
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
            throw new IOException("MinIO上传失败: " + e.getMessage(), e);
        }
    }

    /**
     * 阿里云OSS上传字节数组
     */
    private String aliyunOssUploadBytes(byte[] data, String fileName, String contentType) throws IOException {
        AliyunOSSConfigEntity aliyunOSSConfig = configCacheService.getAliyunOSSConfig();

        // 获取OSS配置
        String bucketName = aliyunOSSConfig.getBucketName();
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String fileDomain = aliyunOSSConfig.getFileDomain();
        String bucketPath = aliyunOSSConfig.getBucketPath();

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        try {
            // 生成存储路径
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileNameWithExt = FileUtils.generateUUID();

            // 如果有扩展名，添加扩展名
            if (fileName.contains(".")) {
                fileNameWithExt += fileName.substring(fileName.lastIndexOf("."));
            }

            // 拼接完整路径
            String normalizedBucketPath = normalizePath(bucketPath);
            String uploadPath = normalizedBucketPath.isEmpty() ? datePath : normalizedBucketPath + "/" + datePath;
            String uploadFileName = uploadPath + "/" + fileNameWithExt;

            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setHeader("Content-Disposition", "inline");
            metadata.setContentType(contentType);

            // 上传文件
            ossClient.putObject(bucketName, uploadFileName, new ByteArrayInputStream(data), metadata);

            // 返回文件URL
            return fileDomain + uploadFileName;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 保存压缩文件记录
     */
    private void saveCompressedFileRecord(String fileUrl, String fileName, String contentType,
                                          long fileSize, String storageType) {
        try {
            // 获取当前用户信息
            Long userId = SecurityUtils.getUserId();
            String userName = SecurityUtils.getUsername();

            // 从URL中提取路径
            String filePath = URLUtils.extractPathFromUrl(fileUrl);

            // 从URL中提取文件扩展名
            String fileExtension = extractExtensionFromUrl(fileUrl);

            // 获取存储桶名称（仅对MinIO和OSS有效）
            String bucketName = null;
            if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getMinioConfig().getBucketName();
            } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getAliyunOSSConfig().getBucketName();
            }

            // 创建文件记录
            FileUploadRecord record = new FileUploadRecord();
            record.setFileName(fileName);
            record.setOriginalFileName(fileName);
            record.setFilePath(filePath);
            record.setFileUrl(fileUrl);
            record.setFileSize(fileSize);
            record.setFileType(contentType);
            record.setFileExtension(fileExtension);
            record.setStorageType(storageType);
            record.setBucketName(bucketName);
            record.setUploaderId(userId);
            record.setUploaderName(userName);
            record.setCreateBy(Constants.SYSTEM_CREATE);
            record.setUploadTime(new Date());

            fileUploadRecordService.save(record);

            log.info("压缩文件信息保存成功 - 文件名: {}, 大小: {}, 存储类型: {}, 访问URL: {}",
                    fileName, fileSize, storageType, fileUrl);
        } catch (Exception e) {
            log.error("保存压缩文件记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响上传流程
        }
    }

    /**
     * 从URL中提取文件扩展名
     */
    private String extractExtensionFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        // 移除URL参数
        String cleanUrl = url;
        int queryIndex = cleanUrl.indexOf('?');
        if (queryIndex > 0) {
            cleanUrl = cleanUrl.substring(0, queryIndex);
        }

        // 获取最后一个点之后的内容作为扩展名
        int lastDotIndex = cleanUrl.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return cleanUrl.substring(lastDotIndex);
        }

        return "";
    }


}
