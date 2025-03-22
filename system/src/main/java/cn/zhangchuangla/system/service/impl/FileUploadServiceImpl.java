package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.FileInfo;
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
                    fileInfo.getContentType(),
                    false
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
                    fileInfo.getContentType(),
                    false
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
                    false
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
                    storageType,
                    false
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
                        storageType,
                        true
                );

                result.setCompressedUrl(compressedUrl);

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
     * 保存文件记录到数据库中
     *
     * @param fileUrl       原始文件URL
     * @param compressedUrl 压缩文件URL
     * @param fileInfo      文件信息
     * @param storageType   存储位置
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
            record.setMd5(fileInfo.getMd5());
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
     * 上传字节数组
     *
     * @param data        字节数组
     * @param fileName    文件名
     * @param contentType 文件类型
     * @param storageType 存储类型
     */
    private String uploadByteArray(byte[] data, String fileName, String contentType, String storageType, boolean isCompressed) throws IOException {
        if (Constants.LOCAL_FILE_UPLOAD.equals(storageType)) {
            return localUploadBytes(data, fileName, isCompressed);
        } else if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
            return minioUploadBytes(data, fileName, contentType, isCompressed);
        } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
            return aliyunOssUploadBytes(data, fileName, contentType, isCompressed);
        } else {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "不支持的存储类型: " + storageType);
        }
    }

    /**
     * 文件路径结果类
     *
     * @param relativePath 相对路径（不含文件名）
     * @param fileName     文件名（含扩展名）
     */
    private record FilePathResult(String relativePath, String fileName) {

        public String getFullRelativePath() {
            return relativePath + "/" + fileName;
        }
    }

    /**
     * 生成文件存储路径和文件名
     *
     * @param fileName     原始文件名
     * @param isCompressed 是否为压缩资源
     * @return 包含路径信息的对象
     */
    private FilePathResult generateFilePath(String fileName, boolean isCompressed) {
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

        // 根据压缩状态确定子目录
        String subDir = isCompressed ? "compressed" : "original";

        // 构建完整文件名
        String finalFileName = uuidFileName + fileExtension;

        // 构建相对路径
        String relativePath = yearMonthDir + "/" + subDir;

        return new FilePathResult(relativePath, finalFileName);
    }

    /**
     * 本地上传字节数组
     */
    private String localUploadBytes(byte[] data, String fileName, boolean isCompressed) throws IOException {
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        String uploadPath = localFileConfig.getUploadPath();

        // 使用提取的方法生成文件路径
        FilePathResult pathResult = generateFilePath(fileName, isCompressed);

        // 设置目标路径
        Path directory = Paths.get(uploadPath + File.separator + pathResult.relativePath().replace("/", File.separator));
        Path filePath = directory.resolve(pathResult.fileName());

        // 创建目录，如果不存在则创建
        Files.createDirectories(directory);

        // 写入文件
        Files.write(filePath, data);

        return Constants.RESOURCE_PREFIX + "/" + pathResult.getFullRelativePath();
    }

    /**
     * MinIO上传字节数组
     */
    private String minioUploadBytes(byte[] data, String fileName, String contentType, boolean isCompressed) throws IOException {
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

            // 使用提取的方法生成文件路径
            FilePathResult pathResult = generateFilePath(fileName, isCompressed);

            // 构建对象名
            String objectName = pathResult.getFullRelativePath();

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
    private String aliyunOssUploadBytes(byte[] data, String fileName, String contentType, boolean isCompressed) throws IOException {
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
            // 使用提取的方法生成文件路径
            FilePathResult pathResult = generateFilePath(fileName, isCompressed);

            // 拼接完整路径
            String normalizedBucketPath = normalizePath(bucketPath);
            String uploadPath = normalizedBucketPath.isEmpty() ?
                    pathResult.relativePath() :
                    normalizedBucketPath + "/" + pathResult.relativePath();
            String uploadFileName = uploadPath + "/" + pathResult.fileName();

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