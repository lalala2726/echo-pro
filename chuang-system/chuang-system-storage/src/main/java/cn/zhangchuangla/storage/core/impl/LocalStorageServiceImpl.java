package cn.zhangchuangla.storage.core.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.constant.StorageConstants;
import cn.zhangchuangla.storage.FileInfo;
import cn.zhangchuangla.storage.StorageType;
import cn.zhangchuangla.storage.config.StorageSystemProperties;
import cn.zhangchuangla.storage.core.StorageService;
import cn.zhangchuangla.storage.exception.StorageException;
import cn.zhangchuangla.storage.util.StoragePathUtils;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.LOCAL_STORAGE_SERVICE)
public class LocalStorageServiceImpl implements StorageService {

    private static final List<String> SUPPORTED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp");
    private static final int THUMBNAIL_WIDTH = 150;
    private static final int THUMBNAIL_HEIGHT = 150;
    private static final String THUMBNAIL_SUB_PATH = "thumbnails";
    private final StorageSystemProperties.LocalConfig config;

    public LocalStorageServiceImpl(StorageSystemProperties properties) {
        if (properties == null || properties.getLocal() == null) {
            log.error("LocalConfig is null. LocalStorageService cannot be initialized correctly.");
            throw new StorageException("本地存储配置未初始化");
        }
        this.config = properties.getLocal();
        if (!StringUtils.hasText(this.config.getRootPathOrBucketName())) {
            log.error("Local storage rootPathOrBucketName is not configured.");
            throw new StorageException("本地存储的rootPathOrBucketName未配置");
        }
    }

    private Path getAbsolutePath(String relativePath) {
        return Paths.get(config.getRootPathOrBucketName(), relativePath).toAbsolutePath().normalize();
    }

    private String getRelativePath(String subPath, String originalFileName) {
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
        if (inputStream == null) {
            throw new StorageException("文件上传失败: 输入流不能为空");
        }
        if (!StringUtils.hasText(originalFileName)) {
            throw new StorageException("文件上传失败: 原始文件名不能为空");
        }

        String relativePath = getRelativePath(subPath, originalFileName);
        Path absolutePath = getAbsolutePath(relativePath);
        Path directory = absolutePath.getParent();

        try {
            if (directory != null && !Files.exists(directory)) {
                Files.createDirectories(directory);
                log.info("Created directory: {}", directory);
            }

            long size = Files.copy(inputStream, absolutePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded locally: {} ({} bytes)", absolutePath, size);

            String url = StringUtils.hasText(config.getFileDomain()) ?
                    StoragePathUtils.concatUrl(config.getFileDomain(), relativePath) :
                    StoragePathUtils.concatUrl(Constants.RESOURCE_PREFIX, relativePath);

            return FileInfo.builder()
                    .originalFileName(originalFileName)
                    .newFileName(absolutePath.getFileName().toString())
                    .relativePath(relativePath)
                    .url(url)
                    .size(size)
                    .contentType(contentType)
                    .storageType(StorageType.LOCAL)
                    .build();

        } catch (IOException e) {
            log.error("Local file upload failed for: {}", originalFileName, e);
            throw new StorageException("本地文件上传失败: " + originalFileName, e);
        }
    }

    @Override
    public FileInfo uploadImage(MultipartFile file, String subPath) {
        FileInfo originalFileInfo = uploadFile(file, subPath);

        if (originalFileInfo != null && isSupportedImageType(originalFileInfo.getContentType())) {
            try {
                Path originalAbsolutePath = getAbsolutePath(originalFileInfo.getRelativePath());
                String thumbnailName = "thumb_" + originalFileInfo.getNewFileName();
                String originalFileSubPathDir = FilenameUtils.getPath(originalFileInfo.getRelativePath());
                String thumbnailRelativeDir = FilenameUtils.concat(originalFileSubPathDir, THUMBNAIL_SUB_PATH);
                Path thumbnailDirectory = getAbsolutePath(thumbnailRelativeDir);

                if (!Files.exists(thumbnailDirectory)) {
                    Files.createDirectories(thumbnailDirectory);
                    log.info("Created thumbnail directory: {}", thumbnailDirectory);
                }

                Path thumbnailAbsolutePath = thumbnailDirectory.resolve(thumbnailName);

                Thumbnails.of(originalAbsolutePath.toFile())
                        .size(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT)
                        .toFile(thumbnailAbsolutePath.toFile());
                log.info("Thumbnail generated and saved locally: {}", thumbnailAbsolutePath);

                String thumbnailRelativePath = FilenameUtils.concat(thumbnailRelativeDir, thumbnailName);
                String thumbnailUrl = StringUtils.hasText(config.getFileDomain()) ?
                        StoragePathUtils.concatUrl(config.getFileDomain(), thumbnailRelativePath) :
                        StoragePathUtils.concatUrl(Constants.RESOURCE_PREFIX, thumbnailRelativePath);

                originalFileInfo.setThumbnailUrl(thumbnailUrl);
                originalFileInfo.setThumbnailPath(thumbnailRelativePath);

            } catch (IOException e) {
                log.error("Failed to generate or save thumbnail for image: {}", originalFileInfo.getOriginalFileName(), e);
            } catch (Exception e) {
                log.error("An unexpected error occurred during thumbnail generation for image: {}", originalFileInfo.getOriginalFileName(), e);
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
            log.warn("Delete_file_local: Relative path is empty.");
            return false;
        }
        Path absolutePath = getAbsolutePath(relativePath);
        try {
            if (Files.exists(absolutePath)) {
                Files.delete(absolutePath);
                log.info("File deleted locally: {}", absolutePath);
                return true;
            }
            log.warn("File not found for deletion (local): {}", absolutePath);
            return false;
        } catch (IOException e) {
            log.error("Failed to delete file locally: {}", absolutePath, e);
            throw new StorageException("本地文件删除失败: " + relativePath, e);
        }
    }

    @Override
    public void deleteFiles(List<String> relativePaths) {
        if (relativePaths == null || relativePaths.isEmpty()) {
            return;
        }
        relativePaths.forEach(this::deleteFile);
    }

    @Override
    public InputStream downloadFile(String relativePath) {
        Path absolutePath = getAbsolutePath(relativePath);
        try {
            if (Files.exists(absolutePath)) {
                return Files.newInputStream(absolutePath);
            }
            log.warn("File not found for download (local): {}", absolutePath);
            return null;
        } catch (IOException e) {
            log.error("Failed to download file locally: {}", absolutePath, e);
            throw new StorageException("本地文件下载失败: " + relativePath, e);
        }
    }

    @Override
    public String getFileUrl(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return null;
        }
        return StringUtils.hasText(config.getFileDomain()) ?
                StoragePathUtils.concatUrl(config.getFileDomain(), relativePath) :
                StoragePathUtils.concatUrl(Constants.RESOURCE_PREFIX, relativePath);
    }

    @Override
    public boolean fileExists(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return true;
        }
        return !Files.exists(getAbsolutePath(relativePath));
    }

    @Override
    public FileInfo moveToTrash(String relativePath) {
        if (config.isEnableTrash()) {
            log.warn("Trash is not enabled. Cannot move file to trash: {}", relativePath);
            return null;
        }
        if (!StringUtils.hasText(relativePath)) {
            throw new StorageException("Relative path cannot be empty for moving to trash.");
        }

        Path sourceAbsolutePath = getAbsolutePath(relativePath);
        if (!Files.exists(sourceAbsolutePath)) {
            log.warn("File not found to move to trash: {}", sourceAbsolutePath);
            return null;
        }

        String trashSubPath = StoragePathUtils.generatePath(config.getTrashDirectoryName(), FilenameUtils.getName(relativePath));
        Path trashAbsolutePath = getAbsolutePath(trashSubPath);
        Path trashDirectory = trashAbsolutePath.getParent();

        try {
            if (trashDirectory != null && !Files.exists(trashDirectory)) {
                Files.createDirectories(trashDirectory);
                log.info("Created trash directory: {}", trashDirectory);
            }
            Files.move(sourceAbsolutePath, trashAbsolutePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File moved to trash: {} -> {}", sourceAbsolutePath, trashAbsolutePath);

            FileInfo fileInfo = FileInfo.builder()
                    .originalFileName(FilenameUtils.getName(relativePath))
                    .newFileName(trashAbsolutePath.getFileName().toString())
                    .relativePath(relativePath)
                    .originalTrashPath(trashSubPath)
                    .storageType(StorageType.LOCAL)
                    .build();

            return fileInfo;
        } catch (IOException e) {
            log.error("Failed to move file to trash (local): {}", sourceAbsolutePath, e);
            throw new StorageException("移动文件到回收站失败: " + relativePath, e);
        }
    }

    @Override
    public FileInfo restoreFromTrash(String trashPath) {
        if (!StringUtils.hasText(trashPath) || !trashPath.startsWith(config.getTrashDirectoryName())) {
            log.warn("Invalid trash path: {}", trashPath);
            throw new StorageException("无效的回收站路径: " + trashPath);
        }

        Path trashAbsolutePath = getAbsolutePath(trashPath);
        if (!Files.exists(trashAbsolutePath)) {
            log.warn("File not found in trash: {}", trashAbsolutePath);
            return null;
        }

        String originalRelativePath = trashPath.substring(config.getTrashDirectoryName().length() + 1);
        Path restoredAbsolutePath = getAbsolutePath(originalRelativePath);
        Path restoredDirectory = restoredAbsolutePath.getParent();

        try {
            if (restoredDirectory != null && !Files.exists(restoredDirectory)) {
                Files.createDirectories(restoredDirectory);
            }
            Files.move(trashAbsolutePath, restoredAbsolutePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File restored from trash: {} -> {}", trashAbsolutePath, restoredAbsolutePath);

            String url = StringUtils.hasText(config.getFileDomain()) ?
                    StoragePathUtils.concatUrl(config.getFileDomain(), originalRelativePath) :
                    StoragePathUtils.concatUrl(Constants.RESOURCE_PREFIX, originalRelativePath);

            return FileInfo.builder()
                    .originalFileName(FilenameUtils.getName(originalRelativePath))
                    .newFileName(restoredAbsolutePath.getFileName().toString())
                    .relativePath(originalRelativePath)
                    .url(url)
                    .size(Files.size(restoredAbsolutePath))
                    .contentType(Files.probeContentType(restoredAbsolutePath))
                    .storageType(StorageType.LOCAL)
                    .originalTrashPath(null)
                    .build();
        } catch (IOException e) {
            log.error("Failed to restore file from trash (local): {}", trashAbsolutePath, e);
            throw new StorageException("从回收站恢复文件失败: " + trashPath, e);
        }
    }
}
