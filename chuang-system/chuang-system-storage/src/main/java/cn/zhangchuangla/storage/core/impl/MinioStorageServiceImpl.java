package cn.zhangchuangla.storage.core.impl;

import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.core.StorageService;
import cn.zhangchuangla.storage.enums.StorageType;
import cn.zhangchuangla.storage.exception.StorageException;
import cn.zhangchuangla.storage.model.entity.FileInfo;
import cn.zhangchuangla.storage.utils.StorageUtils;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.MINIO_STORAGE_SERVICE)
public class MinioStorageServiceImpl implements StorageService {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    private static final int THUMBNAIL_WIDTH = 150;
    private static final int THUMBNAIL_HEIGHT = 150;
    private static final String THUMBNAIL_SUB_PATH = "thumbnails";
    private final StorageSystemProperties.MinioConfig config;
    private MinioClient minioClient;

    public MinioStorageServiceImpl(StorageSystemProperties properties) {
        if (properties == null || properties.getMinio() == null) {
            log.error("MinioConfig is null. MinioStorageService cannot be initialized correctly.");
            throw new StorageException("MinIO存储配置未初始化");
        }
        this.config = properties.getMinio();
        if (!StringUtils.hasText(this.config.getEndpoint()) ||
                !StringUtils.hasText(this.config.getAccessKey()) ||
                !StringUtils.hasText(this.config.getSecretKey()) ||
                !StringUtils.hasText(this.config.getRootPathOrBucketName())) {
            log.error("MinIO storage essential properties (endpoint, accessKey, secretKey, rootPathOrBucketName) are not fully configured.");
            throw new StorageException("MinIO存储基本配置不完整");
        }
    }

    private MinioClient getClient() {
        if (minioClient == null) {
            try {
                minioClient = MinioClient.builder()
                        .endpoint(config.getEndpoint())
                        .credentials(config.getAccessKey(), config.getSecretKey())
                        .build();
                ensureBucketExists(config.getRootPathOrBucketName());
            } catch (Exception e) {
                log.error("Failed to initialize Minio client or ensure bucket exists", e);
                throw new StorageException("MinIO客户端初始化失败", e);
            }
        }
        return minioClient;
    }

    private void ensureBucketExists(String bucketName) {
        try {
            boolean found = getClient().bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                getClient().makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("MinIO Bucket '{}' created successfully.", bucketName);
            } else {
                log.debug("MinIO Bucket '{}' already exists.", bucketName);
            }
        } catch (Exception e) {
            log.error("Error ensuring MinIO bucket '{}' exists", bucketName, e);
            throw new StorageException("检查或创建MinIO存储桶失败: " + bucketName, e);
        }
    }

    private String getObjectName(String subPath, String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String extension = FilenameUtils.getExtension(originalFileName);
        String safeOriginalFileName = FilenameUtils.getName(originalFileName);
        String newFileName = safeOriginalFileName + "_" + timestamp + "_" + randomSuffix + "." + extension;
        return StorageUtils.generatePath(subPath, newFileName);
    }

    @Override
    public FileInfo uploadFile(MultipartFile file, String subPath) {
        try {
            return uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), subPath);
        } catch (IOException e) {
            log.error("Failed to get InputStream from MultipartFile: {}", file.getOriginalFilename(), e);
            throw new StorageException("文件上传失败: 无法读取文件内容", e);
        }
    }

    @Override
    public FileInfo uploadFile(InputStream inputStream, String originalFileName, String contentType, String subPath) {
        if (inputStream == null || !StringUtils.hasText(originalFileName)) {
            throw new StorageException("输入流或原始文件名不能为空");
        }
        String objectName = getObjectName(subPath, originalFileName);
        long size;
        try (InputStream closableInputStream = inputStream) {
            byte[] bytes;
            if (closableInputStream instanceof ByteArrayInputStream) {
                size = closableInputStream.available();
                bytes = ((ByteArrayInputStream) closableInputStream).readAllBytes();
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                closableInputStream.transferTo(baos);
                bytes = baos.toByteArray();
                size = bytes.length;
            }

            if (size == 0) {
                log.warn("Input stream for file {} is empty.", originalFileName);
            }

            try (ByteArrayInputStream repeatableInputStream = new ByteArrayInputStream(bytes)) {
                PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                        .bucket(config.getRootPathOrBucketName())
                        .object(objectName)
                        .stream(repeatableInputStream, size, -1)
                        .contentType(contentType)
                        .build();
                getClient().putObject(putObjectArgs);
            }

            log.info("File uploaded to MinIO: {}/{}", config.getRootPathOrBucketName(), objectName);

            String url = getFileUrl(objectName);

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(FilenameUtils.getName(objectName))
                    .relativePath(objectName)
                    .url(url)
                    .size(size)
                    .contentType(contentType)
                    .storageType(StorageType.MINIO)
                    .build();
        } catch (Exception e) {
            log.error("MinIO file upload failed for object: {}", objectName, e);
            throw new StorageException("MinIO文件上传失败: " + originalFileName, e);
        }
    }

    @Override
    public FileInfo uploadImage(MultipartFile file, String subPath) {
        FileInfo originalFileInfo = uploadFile(file, subPath);

        if (originalFileInfo != null && isSupportedImageType(originalFileInfo.getContentType())) {
            try (InputStream originalInputStream = file.getInputStream()) {
                String thumbnailName = "thumb_" + originalFileInfo.getNewFileName();
                String originalFileSubPathDir = FilenameUtils.getPath(originalFileInfo.getRelativePath());
                String thumbnailRelativeDir = FilenameUtils.concat(originalFileSubPathDir, THUMBNAIL_SUB_PATH);
                String thumbnailObjectName = FilenameUtils.concat(thumbnailRelativeDir, thumbnailName);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Thumbnails.of(originalInputStream)
                        .size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                        .outputFormat(FilenameUtils.getExtension(originalFileInfo.getOriginalFileName()))
                        .toOutputStream(baos);

                byte[] thumbnailBytes = baos.toByteArray();
                try (InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailBytes)) {
                    PutObjectArgs thumbPutArgs = PutObjectArgs.builder()
                            .bucket(config.getRootPathOrBucketName())
                            .object(thumbnailObjectName)
                            .stream(thumbnailInputStream, thumbnailBytes.length, -1)
                            .contentType(originalFileInfo.getContentType())
                            .build();
                    getClient().putObject(thumbPutArgs);
                }

                log.info("Thumbnail uploaded to MinIO: {}/{}", config.getRootPathOrBucketName(), thumbnailObjectName);

                String thumbnailUrl = getFileUrl(thumbnailObjectName);
                originalFileInfo.setThumbnailUrl(thumbnailUrl);
                originalFileInfo.setThumbnailPath(thumbnailObjectName);

            } catch (IOException e) {
                log.error("IO error during MinIO thumbnail generation for image: {}", originalFileInfo.getOriginalFileName(), e);
            } catch (Exception e) {
                log.error("MinIO thumbnail generation/upload failed for image: {}", originalFileInfo.getOriginalFileName(), e);
            }
        }
        return originalFileInfo;
    }

    private boolean isSupportedImageType(String contentType) {
        if (!StringUtils.hasText(contentType)) {
            return false;
        }
        return SUPPORTED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    @Override
    public boolean deleteFile(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return false;
        }
        try {
            getClient().removeObject(RemoveObjectArgs.builder()
                    .bucket(config.getRootPathOrBucketName())
                    .object(relativePath)
                    .build());
            log.info("File deleted from MinIO: {}/{}", config.getRootPathOrBucketName(), relativePath);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", relativePath, e);
            throw new StorageException("MinIO文件删除失败: " + relativePath, e);
        }
    }

    @Override
    public void deleteFiles(List<String> relativePaths) {
        if (relativePaths == null || relativePaths.isEmpty()) {
            return;
        }
        List<DeleteObject> objects = new java.util.LinkedList<>();
        for (String path : relativePaths) {
            if (StringUtils.hasText(path)) {
                objects.add(new DeleteObject(path));
            }
        }
        if (objects.isEmpty()) {
            return;
        }

        try {
            Iterable<Result<DeleteError>> results = getClient().removeObjects(
                    RemoveObjectsArgs.builder().bucket(config.getRootPathOrBucketName()).objects(objects).build());
            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();
                log.error("Error in deleting object '{}'; Error: {}", error.objectName(), error.message());
            }
            log.info("Batch delete request sent to MinIO for {} objects.", objects.size());
        } catch (Exception e) {
            log.error("Failed to batch delete files from MinIO", e);
            throw new StorageException("MinIO批量文件删除失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        try {
            return getClient().getObject(
                    GetObjectArgs.builder()
                            .bucket(config.getRootPathOrBucketName())
                            .object(relativePath)
                            .build());
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                log.warn("File not found in MinIO for download: {}/{}", config.getRootPathOrBucketName(), relativePath);
                return null;
            }
            log.error("Error downloading file from MinIO: {}", relativePath, e);
            throw new StorageException("MinIO文件下载失败: " + relativePath, e);
        } catch (Exception e) {
            log.error("Error downloading file from MinIO: {}", relativePath, e);
            throw new StorageException("MinIO文件下载失败: " + relativePath, e);
        }
    }

    @Override
    public String getFileUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        if (StringUtils.hasText(config.getFileDomain())) {
            return StorageUtils.concatUrl(config.getFileDomain(), relativePath);
        }
        try {
            return getClient().getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(config.getRootPathOrBucketName())
                            .object(relativePath)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
        } catch (Exception e) {
            log.error("Error generating presigned URL for MinIO object: {}", relativePath, e);
            throw new StorageException("获取MinIO文件URL失败: " + relativePath, e);
        }
    }

    @Override
    public boolean fileExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return true;
        }
        try {
            getClient().statObject(
                    StatObjectArgs.builder().bucket(config.getRootPathOrBucketName()).object(relativePath).build());
            return false;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return true;
            }
            log.error("Error checking file existence in MinIO: {}", relativePath, e);
            throw new StorageException("检查MinIO文件是否存在失败: " + relativePath, e);
        } catch (Exception e) {
            log.error("Error checking file existence in MinIO: {}", relativePath, e);
            throw new StorageException("检查MinIO文件是否存在失败: " + relativePath, e);
        }
    }

    private void copyObject(String sourceObject, String destObject) {
        try {
            getClient().copyObject(
                    CopyObjectArgs.builder()
                            .bucket(config.getRootPathOrBucketName())
                            .object(destObject)
                            .source(
                                    CopySource.builder()
                                            .bucket(config.getRootPathOrBucketName())
                                            .object(sourceObject)
                                            .build())
                            .build());
        } catch (Exception e) {
            throw new StorageException("MinIO文件复制失败: " + sourceObject + " -> " + destObject, e);
        }
    }

    @Override
    public FileInfo moveToTrash(String relativePath) {
        if (config.isEnableTrash()) {
            log.warn("Trash is not enabled for MinIO. File will not be moved: {}", relativePath);
            return null;
        }
        if (!StringUtils.hasText(relativePath) || fileExists(relativePath)) {
            log.warn("File not found or path is empty, cannot move to trash: {}", relativePath);
            return null;
        }

        String trashObjectPath = StorageUtils.generatePath(config.getTrashDirectoryName(), FilenameUtils.getName(relativePath));
        try {
            copyObject(relativePath, trashObjectPath);
            deleteFile(relativePath);
            log.info("File moved to MinIO trash: {} -> {}", relativePath, trashObjectPath);
            return FileInfo.builder()
                    .originalFileName(FilenameUtils.getName(relativePath))
                    .newFileName(FilenameUtils.getName(trashObjectPath))
                    .relativePath(relativePath)
                    .originalTrashPath(trashObjectPath)
                    .storageType(StorageType.MINIO)
                    .build();
        } catch (Exception e) {
            log.error("Failed to move file to MinIO trash: {}", relativePath, e);
            throw new StorageException("移动文件到MinIO回收站失败: " + relativePath, e);
        }
    }

    @Override
    public FileInfo restoreFromTrash(String trashPath) {
        if (!StringUtils.hasText(trashPath) || !trashPath.startsWith(config.getTrashDirectoryName())) {
            throw new StorageException("无效的MinIO回收站路径: " + trashPath);
        }
        if (fileExists(trashPath)) {
            log.warn("File not found in MinIO trash: {}", trashPath);
            return null;
        }

        String originalFileName = FilenameUtils.getName(trashPath);
        String restoredRelativePath = getObjectName(null, originalFileName);

        try {
            copyObject(trashPath, restoredRelativePath);
            deleteFile(trashPath);
            log.info("File restored from MinIO trash: {} -> {}", trashPath, restoredRelativePath);

            String url = getFileUrl(restoredRelativePath);
            StatObjectResponse stat = getClient().statObject(StatObjectArgs.builder().bucket(config.getRootPathOrBucketName()).object(restoredRelativePath).build());

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(FilenameUtils.getName(restoredRelativePath))
                    .relativePath(restoredRelativePath)
                    .url(url)
                    .size(stat.size())
                    .contentType(stat.contentType())
                    .storageType(StorageType.MINIO)
                    .build();
        } catch (Exception e) {
            log.error("Failed to restore file from MinIO trash: {}", trashPath, e);
            throw new StorageException("从MinIO回收站恢复文件失败: " + trashPath, e);
        }
    }
}
