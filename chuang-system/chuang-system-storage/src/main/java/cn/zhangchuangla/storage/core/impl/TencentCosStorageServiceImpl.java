package cn.zhangchuangla.storage.core.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.storage.FileInfo;
import cn.zhangchuangla.storage.StorageType;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.core.StorageService;
import cn.zhangchuangla.storage.exception.StorageException;
import cn.zhangchuangla.storage.util.StoragePathUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
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
@Service(StorageConstants.TENCENT_COS_STORAGE_SERVICE)
public class TencentCosStorageServiceImpl implements StorageService {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    private static final int THUMBNAIL_WIDTH = 150;
    private static final int THUMBNAIL_HEIGHT = 150;
    private static final String THUMBNAIL_SUB_PATH = "thumbnails";
    private final StorageSystemProperties.TencentCosConfig config;
    private COSClient cosClient;

    public TencentCosStorageServiceImpl(StorageSystemProperties properties) {
        if (properties == null || properties.getTencentCos() == null) {
            log.error("TencentCosConfig is null. TencentCosStorageService cannot be initialized correctly.");
            throw new StorageException("腾讯云COS存储配置未初始化");
        }
        this.config = properties.getTencentCos();
        if (!StringUtils.hasText(this.config.getRegion()) ||
                !StringUtils.hasText(this.config.getSecretId()) ||
                !StringUtils.hasText(this.config.getSecretKey()) ||
                !StringUtils.hasText(this.config.getRootPathOrBucketName())) {
            log.error("Tencent COS storage essential properties (region, secretId, secretKey, rootPathOrBucketName) are not fully configured.");
            throw new StorageException("腾讯云COS存储基本配置不完整");
        }
    }

    private COSClient getClient() {
        if (cosClient == null) {
            try {
                COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
                ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
                cosClient = new COSClient(cred, clientConfig);
                ensureBucketExists(config.getRootPathOrBucketName());
            } catch (Exception e) {
                log.error("Failed to initialize Tencent COS client or ensure bucket exists", e);
                throw new StorageException("Tencent COS客户端初始化失败", e);
            }
        }
        return cosClient;
    }

    private void ensureBucketExists(String bucketName) {
        try {
            if (!getClient().doesBucketExist(bucketName)) {
                getClient().createBucket(bucketName);
                log.info("Tencent COS Bucket '{}' created successfully.", bucketName);
            } else {
                log.debug("Tencent COS Bucket '{}' already exists.", bucketName);
            }
        } catch (CosServiceException e) {
            if (!"BucketAlreadyOwnedByYou".equals(e.getErrorCode())) {
                log.error("Error ensuring Tencent COS bucket '{}' exists (ServiceException)", bucketName, e);
                throw new StorageException("检查或创建Tencent COS存储桶失败: " + bucketName, e);
            }
            log.debug("Tencent COS Bucket '{}' already exists (owned by you).", bucketName);
        } catch (CosClientException e) {
            log.error("Error ensuring Tencent COS bucket '{}' exists (ClientException)", bucketName, e);
            throw new StorageException("检查或创建Tencent COS存储桶失败: " + bucketName, e);
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
        String bucketName = config.getRootPathOrBucketName();
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
                log.warn("Input stream for Tencent COS file {} is empty.", originalFileName);
            }

            try (ByteArrayInputStream repeatableInputStream = new ByteArrayInputStream(bytes)) {
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, repeatableInputStream, metadata);
                getClient().putObject(putObjectRequest);
            }

            log.info("File uploaded to Tencent COS: {}/{}", bucketName, objectName);

            String url = getFileUrl(objectName);

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(FilenameUtils.getName(objectName))
                    .relativePath(objectName)
                    .url(url)
                    .size(size)
                    .contentType(contentType)
                    .storageType(StorageType.TENCENT_COS)
                    .build();
        } catch (CosClientException | IOException e) {
            log.error("Tencent COS file upload failed for object: {}", objectName, e);
            throw new StorageException("Tencent COS文件上传失败: " + originalFileName, e);
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
                    PutObjectRequest thumbPutRequest = new PutObjectRequest(config.getRootPathOrBucketName(),
                            thumbnailObjectName,
                            thumbnailInputStream,
                            thumbMetadata);
                    getClient().putObject(thumbPutRequest);
                }

                log.info("Thumbnail uploaded to Tencent COS: {}/{}", config.getRootPathOrBucketName(), thumbnailObjectName);

                String thumbnailUrl = getFileUrl(thumbnailObjectName);
                originalFileInfo.setThumbnailUrl(thumbnailUrl);
                originalFileInfo.setThumbnailPath(thumbnailObjectName);

            } catch (IOException e) {
                log.error("IO error during Tencent COS thumbnail generation for image: {}", originalFileInfo.getOriginalFileName(), e);
            } catch (CosClientException e) {
                log.error("Tencent COS thumbnail upload failed for image: {}", originalFileInfo.getOriginalFileName(), e);
            } catch (Exception e) {
                log.error("An unexpected error occurred during Tencent COS thumbnail generation: {}", originalFileInfo.getOriginalFileName(), e);
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
        String bucketName = config.getRootPathOrBucketName();
        try {
            getClient().deleteObject(bucketName, relativePath);
            log.info("File deleted from Tencent COS: {}/{}", bucketName, relativePath);
            return true;
        } catch (CosClientException e) {
            log.error("Failed to delete file from Tencent COS: {}", relativePath, e);
            throw new StorageException("Tencent COS文件删除失败: " + relativePath, e);
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
        String bucketName = config.getRootPathOrBucketName();

        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
            List<DeleteObjectsRequest.KeyVersion> keys = validPaths.stream()
                    .map(DeleteObjectsRequest.KeyVersion::new)
                    .collect(Collectors.toList());
            deleteObjectsRequest.setKeys(keys);
            deleteObjectsRequest.setQuiet(false);

            DeleteObjectsResult deleteObjectsResult = getClient().deleteObjects(deleteObjectsRequest);
            List<DeleteObjectsResult.DeletedObject> deletedObjects = deleteObjectsResult.getDeletedObjects();

            if (deletedObjects.size() != validPaths.size()) {
                log.warn("Tencent COS batch delete: Not all requested files were deleted. Requested: {}, Deleted: {}", validPaths.size(), deletedObjects.size());
            }
            deletedObjects.forEach(deleted -> log.info("Successfully deleted from Tencent COS: {}", deleted.getKey()));

        } catch (CosClientException e) {
            log.error("Failed to batch delete files from Tencent COS", e);
            throw new StorageException("Tencent COS批量文件删除失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        String bucketName = config.getRootPathOrBucketName();
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, relativePath);
            COSObject cosObject = getClient().getObject(getObjectRequest);
            return cosObject.getObjectContent();
        } catch (CosServiceException e) {
            if ("NoSuchKey".equalsIgnoreCase(e.getErrorCode())) {
                log.warn("File not found in Tencent COS for download: {}/{}", bucketName, relativePath);
                return null;
            }
            log.error("Error downloading file from Tencent COS (ServiceException): {}", relativePath, e);
            throw new StorageException("Tencent COS文件下载失败: " + relativePath, e);
        } catch (CosClientException e) {
            log.error("Error downloading file from Tencent COS (ClientException): {}", relativePath, e);
            throw new StorageException("Tencent COS文件下载失败: " + relativePath, e);
        }
    }

    @Override
    public String getFileUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        String bucketName = config.getRootPathOrBucketName();
        if (StringUtils.hasText(config.getFileDomain())) {
            return StoragePathUtils.concatUrl(config.getFileDomain(), relativePath);
        }
        try {
            Date expirationTime = new Date(System.currentTimeMillis() + 3600 * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, relativePath, HttpMethodName.GET);
            request.setExpiration(expirationTime);
            URL signedUrl = getClient().generatePresignedUrl(request);
            return signedUrl.toString();
        } catch (CosClientException e) {
            log.error("Error generating presigned URL for Tencent COS object: {}", relativePath, e);
            throw new StorageException("获取Tencent COS文件URL失败: " + relativePath, e);
        }
    }

    @Override
    public boolean fileExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return true;
        }
        String bucketName = config.getRootPathOrBucketName();
        try {
            return !getClient().doesObjectExist(bucketName, relativePath);
        } catch (CosClientException e) {
            log.error("Error checking file existence in Tencent COS: {}", relativePath, e);
            throw new StorageException("检查Tencent COS文件是否存在失败: " + relativePath, e);
        }
    }

    private void copyObject(String sourceKey, String destinationKey) {
        String bucketName = config.getRootPathOrBucketName();
        try {
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                    bucketName, sourceKey, bucketName, destinationKey);
            getClient().copyObject(copyObjectRequest);
        } catch (CosClientException e) {
            throw new StorageException("Tencent COS文件复制失败: " + sourceKey + " -> " + destinationKey, e);
        }
    }

    @Override
    public FileInfo moveToTrash(String relativePath) {
        if (config.isEnableTrash()) {
            log.warn("Trash is not enabled for Tencent COS. File will not be moved: {}", relativePath);
            return null;
        }
        if (!StringUtils.hasText(relativePath) || fileExists(relativePath)) {
            log.warn("File not found or path is empty, cannot move to Tencent COS trash: {}", relativePath);
            return null;
        }

        String trashObjectPath = StoragePathUtils.generatePath(config.getTrashDirectoryName(), FilenameUtils.getName(relativePath));
        try {
            copyObject(relativePath, trashObjectPath);
            deleteFile(relativePath);
            log.info("File moved to Tencent COS trash: {} -> {}", relativePath, trashObjectPath);
            return FileInfo.builder()
                    .originalFileName(FilenameUtils.getName(relativePath))
                    .newFileName(FilenameUtils.getName(trashObjectPath))
                    .relativePath(relativePath)
                    .originalTrashPath(trashObjectPath)
                    .storageType(StorageType.TENCENT_COS)
                    .build();
        } catch (Exception e) {
            log.error("Failed to move file to Tencent COS trash: {}", relativePath, e);
            throw new StorageException("移动文件到Tencent COS回收站失败: " + relativePath, e);
        }
    }

    @Override
    public FileInfo restoreFromTrash(String trashPath) {
        if (!StringUtils.hasText(trashPath) || !trashPath.startsWith(config.getTrashDirectoryName())) {
            throw new StorageException("无效的Tencent COS回收站路径: " + trashPath);
        }
        if (fileExists(trashPath)) {
            log.warn("File not found in Tencent COS trash: {}", trashPath);
            return null;
        }

        String originalFileName = FilenameUtils.getName(trashPath);
        String restoredRelativePath = getObjectName(null, originalFileName);
        String bucketName = config.getRootPathOrBucketName();

        try {
            copyObject(trashPath, restoredRelativePath);
            deleteFile(trashPath);
            log.info("File restored from Tencent COS trash: {} -> {}", trashPath, restoredRelativePath);

            String url = getFileUrl(restoredRelativePath);
            ObjectMetadata metadata = getClient().getObjectMetadata(bucketName, restoredRelativePath);

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(FilenameUtils.getName(restoredRelativePath))
                    .relativePath(restoredRelativePath)
                    .url(url)
                    .size(metadata.getContentLength())
                    .contentType(metadata.getContentType())
                    .storageType(StorageType.TENCENT_COS)
                    .build();
        } catch (Exception e) {
            log.error("Failed to restore file from Tencent COS trash: {}", trashPath, e);
            throw new StorageException("从Tencent COS回收站恢复文件失败: " + trashPath, e);
        }
    }
}
