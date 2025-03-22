package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.result.FileUploadResult;
import cn.zhangchuangla.common.utils.FileOperationUtils;
import cn.zhangchuangla.common.utils.ImageUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.URLUtils;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.service.FileManagementService;
import cn.zhangchuangla.system.service.FileUploadService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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
    private final FileManagementService fileManagementService;

    /**
     * 文件信息内部类，用于存储从MultipartFile中提取的信息
     * 避免多次读取MultipartFile导致的失效问题
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FileInfo {
        private String originalFilename;
        private String contentType;
        private long size;
        private byte[] content;
        private String fileExtension;

        /**
         * 从MultipartFile创建FileInfo对象
         * @param file MultipartFile对象
         * @return FileInfo对象
         * @throws IOException 如果读取文件失败
         */
        public static FileInfo fromMultipartFile(MultipartFile file) throws IOException {
            if (file == null || file.isEmpty()) {
                throw new FileException(ResponseCode.FileNameIsNull);
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            return new FileInfo(
                    originalFilename,
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes(), // 立即读取文件内容到内存
                    fileExtension
            );
        }
    }

    @Autowired
    public FileUploadServiceImpl(ConfigCacheService configCacheService, FileManagementService fileManagementService) {
        this.configCacheService = configCacheService;
        this.fileManagementService = fileManagementService;
    }

    /**
     * 文件上传到阿里云OSS
     *
     * @param file MultipartFile
     * @return 文件访问路径
     */
    @Override
    public String AliyunOssFileUpload(MultipartFile file) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);
            return aliyunOssUploadBytes(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }

    /**
     * 文件上传到Minio
     *
     * @param file 文件
     * @return 文件访问路径
     */
    @Override
    public String MinioFileUpload(MultipartFile file) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);
            return minioUploadBytes(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed);
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
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(multipartFile);
            return localUploadBytes(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
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

        try {
            // 将MultipartFile转换为FileInfo，一次性读取所有信息
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);

            // 构建结果对象
            FileUploadResult result = FileUploadResult.builder()
                    .fileName(fileInfo.getOriginalFilename())
                    .fileType(fileInfo.getContentType())
                    .originalSize(fileInfo.getSize())
                    .build();

            // 检查是否为图片
            boolean isImage = ImageUtils.isImage(fileInfo.getContentType());
            result.setImage(isImage);

            // 上传原始文件
            String originalUrl = uploadByteArray(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType(),
                    storageType
            );
            result.setOriginalUrl(originalUrl);

            // 如果是图片文件，进行压缩处理
            if (isImage) {
                // 压缩图片
                byte[] compressedBytes = ImageUtils.compressImage(
                        fileInfo.getContent(), DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT, DEFAULT_QUALITY);

                // 设置压缩文件大小
                result.setCompressedSize(compressedBytes.length);

                // 生成压缩文件名
                String compressedFileName = FileOperationUtils.generateCompressedFileName(fileInfo.getOriginalFilename());

                // 直接上传压缩后的字节数组
                String compressedUrl = uploadByteArray(
                        compressedBytes,
                        compressedFileName,
                        fileInfo.getContentType(),
                        storageType
                );

                result.setCompressedUrl(compressedUrl);

                // 保存压缩文件记录
                if (fileManagementService != null) {
                    saveCompressedFileRecord(
                            compressedUrl,
                            compressedFileName,
                            fileInfo.getContentType(),
                            compressedBytes.length,
                            storageType
                    );
                }
            }

            // 保存原始文件记录
            if (fileManagementService != null) {
                saveFileRecord(
                        originalUrl,
                        result.getCompressedUrl(),
                        fileInfo,
                        storageType
                );
            }

            return result;
        } catch (IOException e) {
            log.error("文件处理异常: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "文件处理失败: " + e.getMessage());
        }
    }

    /**
     * 保存原始文件记录
     */
    private void saveFileRecord(String fileUrl, String compressedUrl, FileInfo fileInfo, String storageType) {
        try {
            // 获取当前用户信息
            Long userId = SecurityUtils.getUserId();
            String userName = SecurityUtils.getUsername();

            // 从URL中提取路径
            String filePath = URLUtils.extractPathFromUrl(fileUrl);

            // 获取存储桶名称（仅对MinIO和OSS有效）
            String bucketName = null;
            if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getMinioConfig().getBucketName();
            } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getAliyunOSSConfig().getBucketName();
            }

            // 创建文件记录
            FileManagement record = new FileManagement();
            record.setFileName(fileInfo.getOriginalFilename());
            record.setOriginalFileName(fileInfo.getOriginalFilename());
            record.setFilePath(filePath);
            record.setFileUrl(fileUrl);
            record.setFileSize(fileInfo.getSize());
            record.setFileType(fileInfo.getContentType());
            record.setFileExtension(fileInfo.getFileExtension());
            record.setStorageType(storageType);
            record.setBucketName(bucketName);
            record.setUploaderId(userId);
            record.setUploaderName(userName);
            record.setCreateBy(Constants.SYSTEM_CREATE);
            record.setUploadTime(new Date());
            record.setPreviewImage(compressedUrl);

            fileManagementService.save(record);
        } catch (Exception e) {
            log.error("保存文件记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响上传流程
        }
    }

    /**
     * 根据存储类型上传文件
     *
     * @param file        文件
     * @param storageType 存储类型
     * @return 文件URL
     */
    private String uploadFile(MultipartFile file, String storageType) {
        try {
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);
            return uploadByteArray(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType(),
                    storageType
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传字节数组
     *
     * @param data        字节数组
     * @param fileName    文件名
     * @param contentType 文件类型
     * @param storageType 存储类型
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
            String objectName = datePath + "/" + FileOperationUtils.generateUUID();

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
            String fileNameWithExt = FileOperationUtils.generateUUID();

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
            String fileExtension = URLUtils.extractExtensionFromUrl(fileUrl);

            // 获取存储桶名称（仅对MinIO和OSS有效）
            String bucketName = null;
            if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getMinioConfig().getBucketName();
            } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getAliyunOSSConfig().getBucketName();
            }

            // 创建文件记录
            FileManagement record = new FileManagement();
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

            fileManagementService.save(record);

            log.info("压缩文件信息保存成功 - 文件名: {}, 大小: {}, 存储类型: {}, 访问URL: {}",
                    fileName, fileSize, storageType, fileUrl);
        } catch (Exception e) {
            log.error("保存压缩文件记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响上传流程
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
}