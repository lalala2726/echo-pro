package cn.zhangchuangla.storage.core.impl;

import cn.zhangchuangla.storage.FileInfo;
import cn.zhangchuangla.storage.StorageType;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.core.StorageService;
import cn.zhangchuangla.storage.exception.StorageException;
import cn.zhangchuangla.storage.util.StoragePathUtils;
import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
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
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Chuang
 */
@Slf4j
@Service("aliyunOssStorageService")
public class AliyunOssStorageServiceImpl implements StorageService {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    private static final int THUMBNAIL_WIDTH = 150;
    private static final int THUMBNAIL_HEIGHT = 150;
    private static final String THUMBNAIL_SUB_PATH = "thumbnails";
    private final StorageSystemProperties.AliyunOssConfig config;
    private OSS ossClient;

    public AliyunOssStorageServiceImpl(StorageSystemProperties properties) {
        if (properties == null || properties.getAliyunOss() == null) {
            log.error("AliyunOssConfig is null. AliyunOssStorageService cannot be initialized correctly.");
            throw new StorageException("阿里云OSS存储配置未初始化");
        }
        this.config = properties.getAliyunOss();
        if (!StringUtils.hasText(this.config.getEndpoint()) ||
                !StringUtils.hasText(this.config.getAccessKeyId()) ||
                !StringUtils.hasText(this.config.getAccessKeySecret()) ||
                !StringUtils.hasText(this.config.getRootPathOrBucketName())) {
            log.error("Aliyun OSS storage essential properties (endpoint, accessKeyId, accessKeySecret, rootPathOrBucketName) are not fully configured.");
            throw new StorageException("阿里云OSS存储基本配置不完整");
        }
    }

    private OSS getClient() {
        if (ossClient == null) {
            try {
                ossClient = new OSSClientBuilder().build(config.getEndpoint(), config.getAccessKeyId(), config.getAccessKeySecret());
                ensureBucketExists(config.getRootPathOrBucketName());
            } catch (Exception e) {
                log.error("Failed to initialize Aliyun OSS client or ensure bucket exists", e);
                throw new StorageException("Aliyun OSS客户端初始化失败", e);
            }
        }
        return ossClient;
    }

    private void ensureBucketExists(String bucketName) {
        try {
            if (!getClient().doesBucketExist(bucketName)) {
                getClient().createBucket(bucketName);
                log.info("Aliyun OSS Bucket '{}' created successfully.", bucketName);
            } else {
                log.debug("Aliyun OSS Bucket '{}' already exists.", bucketName);
            }
        } catch (Exception e) {
            log.error("Error ensuring Aliyun OSS bucket '{}' exists", bucketName, e);
            throw new StorageException("检查或创建Aliyun OSS存储桶失败: " + bucketName, e);
        }
    }

    private String getObjectName(String subPath, String originalFileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        String extension = FilenameUtils.getExtension(originalFileName);
        String safeOriginalFileName = FilenameUtils.getName(originalFileName);
        String newFileName = safeOriginalFileName + "_" + timestamp + "_" + randomSuffix + "." + extension;
        return StoragePathUtils.generatePath(subPath, newFileName);
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
        long size = -1L;
        try (InputStream closableInputStream = inputStream) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

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
            metadata.setContentLength(size);

            if (size == 0) {
                log.warn("Input stream for Aliyun OSS file {} is empty.", originalFileName);
            }

            try (ByteArrayInputStream repeatableInputStream = new ByteArrayInputStream(bytes)) {
                getClient().putObject(config.getRootPathOrBucketName(), objectName, repeatableInputStream, metadata);
            }

            log.info("File uploaded to Aliyun OSS: {}/{}", config.getRootPathOrBucketName(), objectName);

            String url = getFileUrl(objectName);

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(FilenameUtils.getName(objectName))
                    .relativePath(objectName)
                    .url(url)
                    .size(size)
                    .contentType(contentType)
                    .storageType(StorageType.ALIYUN_OSS)
                    .build();
        } catch (OSSException | ClientException | IOException e) {
            log.error("Aliyun OSS file upload failed for object: {}", objectName, e);
            throw new StorageException("Aliyun OSS文件上传失败: " + originalFileName, e);
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
                ObjectMetadata thumbMetadata = new ObjectMetadata();
                thumbMetadata.setContentType(originalFileInfo.getContentType());
                thumbMetadata.setContentLength(thumbnailBytes.length);

                try (InputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailBytes)) {
                    getClient().putObject(config.getRootPathOrBucketName(), thumbnailObjectName, thumbnailInputStream, thumbMetadata);
                }

                log.info("Thumbnail uploaded to Aliyun OSS: {}/{}", config.getRootPathOrBucketName(), thumbnailObjectName);

                String thumbnailUrl = getFileUrl(thumbnailObjectName);
                originalFileInfo.setThumbnailUrl(thumbnailUrl);
                originalFileInfo.setThumbnailPath(thumbnailObjectName);

            } catch (IOException e) {
                log.error("IO error during Aliyun OSS thumbnail generation for image: {}", originalFileInfo.getOriginalFileName(), e);
            } catch (OSSException | ClientException e) {
                log.error("Aliyun OSS thumbnail upload failed for image: {}", originalFileInfo.getOriginalFileName(), e);
            } catch (Exception e) {
                log.error("An unexpected error occurred during Aliyun OSS thumbnail generation: {}", originalFileInfo.getOriginalFileName(), e);
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
            getClient().deleteObject(config.getRootPathOrBucketName(), relativePath);
            log.info("File deleted from Aliyun OSS: {}/{}", config.getRootPathOrBucketName(), relativePath);
            return true;
        } catch (OSSException | ClientException e) {
            log.error("Failed to delete file from Aliyun OSS: {}", relativePath, e);
            throw new StorageException("Aliyun OSS文件删除失败: " + relativePath, e);
        }
    }

    @Override
    public void deleteFiles(List<String> relativePaths) {
        if (relativePaths == null || relativePaths.isEmpty()) {
            return;
        }
        List<String> validPaths = relativePaths.stream().filter(StringUtils::hasText).collect(Collectors.toList());
        if (validPaths.isEmpty()) {
            return;
        }

        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(config.getRootPathOrBucketName());
            deleteObjectsRequest.setKeys(validPaths);
            deleteObjectsRequest.setQuiet(false);
            DeleteObjectsResult deleteObjectsResult = getClient().deleteObjects(deleteObjectsRequest);
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            if (deletedObjects.size() != validPaths.size()) {
                log.warn("Aliyun OSS batch delete: Not all requested files were deleted. Requested: {}, Deleted: {}", validPaths.size(), deletedObjects.size());
            }
            deletedObjects.forEach(deleted -> log.info("Successfully deleted from Aliyun OSS: {}", deleted));
        } catch (OSSException | ClientException e) {
            log.error("Failed to batch delete files from Aliyun OSS", e);
            throw new StorageException("Aliyun OSS批量文件删除失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        try {
            OSSObject ossObject = getClient().getObject(config.getRootPathOrBucketName(), relativePath);
            return ossObject.getObjectContent();
        } catch (OSSException e) {
            if ("NoSuchKey".equals(e.getErrorCode())) {
                log.warn("File not found in Aliyun OSS for download: {}/{}", config.getRootPathOrBucketName(), relativePath);
                return null;
            }
            log.error("Error downloading file from Aliyun OSS: {}", relativePath, e);
            throw new StorageException("Aliyun OSS文件下载失败: " + relativePath, e);
        } catch (ClientException e) {
            log.error("Error downloading file from Aliyun OSS (ClientException): {}", relativePath, e);
            throw new StorageException("Aliyun OSS文件下载失败: " + relativePath, e);
        }
    }

    @Override
    public String getFileUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        if (StringUtils.hasText(config.getFileDomain())) {
            return StoragePathUtils.concatUrl(config.getFileDomain(), relativePath);
        }
        try {
            Date expiration = new Date(new Date().getTime() + 3600 * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(config.getRootPathOrBucketName(), relativePath, HttpMethod.GET);
            request.setExpiration(expiration);
            URL signedUrl = getClient().generatePresignedUrl(request);
            return signedUrl.toString();
        } catch (OSSException | ClientException e) {
            log.error("Error generating presigned URL for Aliyun OSS object: {}", relativePath, e);
            throw new StorageException("获取Aliyun OSS文件URL失败: " + relativePath, e);
        }
    }

    @Override
    public boolean fileExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return true;
        }
        try {
            return !getClient().doesObjectExist(config.getRootPathOrBucketName(), relativePath);
        } catch (OSSException | ClientException e) {
            log.error("Error checking file existence in Aliyun OSS: {}", relativePath, e);
            throw new StorageException("检查Aliyun OSS文件是否存在失败: " + relativePath, e);
        }
    }

    private void copyObject(String sourceBucket, String sourceObject, String destBucket, String destObject) {
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucket, sourceObject, destBucket, destObject);
            getClient().copyObject(copyObjectRequest);
        } catch (OSSException | ClientException e) {
            throw new StorageException("Aliyun OSS文件复制失败: " + sourceObject + " -> " + destObject, e);
        }
    }

    @Override
    public FileInfo moveToTrash(String relativePath) {
        if (config.isEnableTrash()) {
            log.warn("Trash is not enabled for Aliyun OSS. File will not be moved: {}", relativePath);
            return null;
        }
        if (!StringUtils.hasText(relativePath) || fileExists(relativePath)) {
            log.warn("File not found or path is empty, cannot move to Aliyun OSS trash: {}", relativePath);
            return null;
        }

        String trashObjectPath = StoragePathUtils.generatePath(config.getTrashDirectoryName(), FilenameUtils.getName(relativePath));
        String bucketName = config.getRootPathOrBucketName();

        try {
            copyObject(bucketName, relativePath, bucketName, trashObjectPath);
            deleteFile(relativePath);
            log.info("File moved to Aliyun OSS trash: {} -> {}", relativePath, trashObjectPath);
            return FileInfo.builder()
                    .originalFileName(FilenameUtils.getName(relativePath))
                    .newFileName(FilenameUtils.getName(trashObjectPath))
                    .relativePath(relativePath)
                    .originalTrashPath(trashObjectPath)
                    .storageType(StorageType.ALIYUN_OSS)
                    .build();
        } catch (Exception e) {
            log.error("Failed to move file to Aliyun OSS trash: {}", relativePath, e);
            throw new StorageException("移动文件到Aliyun OSS回收站失败: " + relativePath, e);
        }
    }

    @Override
    public FileInfo restoreFromTrash(String trashPath) {
        if (!StringUtils.hasText(trashPath) || !trashPath.startsWith(config.getTrashDirectoryName())) {
            throw new StorageException("无效的Aliyun OSS回收站路径: " + trashPath);
        }
        if (fileExists(trashPath)) {
            log.warn("File not found in Aliyun OSS trash: {}", trashPath);
            return null;
        }

        String originalFileName = FilenameUtils.getName(trashPath);
        String restoredRelativePath = getObjectName(null, originalFileName);
        String bucketName = config.getRootPathOrBucketName();

        try {
            copyObject(bucketName, trashPath, bucketName, restoredRelativePath);
            deleteFile(trashPath);
            log.info("File restored from Aliyun OSS trash: {} -> {}", trashPath, restoredRelativePath);

            String url = getFileUrl(restoredRelativePath);
            ObjectMetadata metadata = getClient().getObjectMetadata(bucketName, restoredRelativePath);

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(FilenameUtils.getName(restoredRelativePath))
                    .relativePath(restoredRelativePath)
                    .url(url)
                    .size(metadata.getContentLength())
                    .contentType(metadata.getContentType())
                    .storageType(StorageType.ALIYUN_OSS)
                    .build();
        } catch (Exception e) {
            log.error("Failed to restore file from Aliyun OSS trash: {}", trashPath, e);
            throw new StorageException("从Aliyun OSS回收站恢复文件失败: " + trashPath, e);
        }
    }
}
