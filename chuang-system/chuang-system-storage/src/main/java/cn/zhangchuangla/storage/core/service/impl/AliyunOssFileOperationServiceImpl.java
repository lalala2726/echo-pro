package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.async.StorageAsyncService;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.AliyunOSSStorageConfig;
import cn.zhangchuangla.storage.utils.AliyunOssOperationUtils;
import cn.zhangchuangla.storage.utils.StorageUtils;
import com.alibaba.fastjson2.JSON;
import com.aliyun.oss.OSS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * 阿里云OSS文件操作服务实现类
 * <p>
 * 提供阿里云OSS存储的完整文件操作功能，包括：
 * <ul>
 *   <li>普通文件上传</li>
 *   <li>图片上传（支持异步压缩生成预览图）</li>
 *   <li>文件删除（支持软删除和强制删除）</li>
 *   <li>文件恢复（从回收站恢复）</li>
 *   <li>回收站文件永久删除</li>
 * </ul>
 *
 * <p>目录结构：</p>
 * <pre>
 * resource/
 * ├── yyyy/MM/
 * │   ├── file/           # 普通文件存储
 * │   └── image/          # 图片文件存储
 * │       ├── original/   # 原图存储
 * │       └── preview/    # 预览图存储
 * └── trash/              # 回收站
 *     └── yyyy/MM/
 *         ├── file/
 *         └── image/
 *             ├── original/
 *             └── preview/
 * </pre>
 *
 * @author Chuang
 * @since 2025/7/3
 */
@Slf4j
@Service(StorageConstants.springBeanName.ALIYUN_OSS_STORAGE_SERVICE)
@RequiredArgsConstructor
public class AliyunOssFileOperationServiceImpl implements FileOperationService {

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final AliyunOssOperationUtils aliyunOssOperationUtils;
    private final StorageAsyncService storageAsyncService;

    /**
     * 上传普通文件到阿里云OSS
     * <p>
     * 将文件上传到阿里云OSS的指定路径，路径格式为：resource/yyyy/MM/file/文件名
     *
     * @param file 要上传的文件，不能为null
     * @return 上传成功后的文件信息，包含文件访问URL、相对路径等
     * @throws FileException 当文件上传失败时抛出
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        OSS ossClient = null;
        try {
            // 获取阿里云OSS配置
            AliyunOSSStorageConfig config = getAliyunOssConfig();
            ossClient = aliyunOssOperationUtils.createOssClient(config);
            String bucketName = config.getBucketName();
            String dateDir = StorageUtils.createDateDir();
            String newFileName = StorageUtils.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));

            // 确保存储桶存在
            aliyunOssOperationUtils.ensureBucketExists(ossClient, bucketName);

            // 构建文件路径 (包含RESOURCE前缀): resource/yyyy/MM/file/文件名
            String filePath = Paths.get(StorageConstants.dirName.RESOURCE, dateDir, StorageConstants.dirName.FILE, newFileName)
                    .toString();

            // 上传文件到阿里云OSS
            aliyunOssOperationUtils.uploadFile(ossClient, bucketName, filePath, file.getInputStream(),
                    file.getSize(), file.getContentType());

            // 构建返回的文件信息
            UploadedFileInfo uploadedFileInfo = new UploadedFileInfo();
            uploadedFileInfo.setFileOriginalName(file.getOriginalFilename());
            uploadedFileInfo.setFileName(newFileName);
            uploadedFileInfo.setFileRelativePath(filePath);
            uploadedFileInfo.setFileSize(file.getSize());
            uploadedFileInfo.setFileType(file.getContentType());
            uploadedFileInfo.setFileExtension(StorageUtils.getFileExtension(file.getOriginalFilename()));
            uploadedFileInfo.setFileUrl(config.getFileDomain() + "/" + filePath);

            log.info("阿里云OSS文件上传成功: {}", filePath);
            return uploadedFileInfo;

        } catch (Exception e) {
            log.error("阿里云OSS文件上传失败", e);
            throw new FileException("阿里云OSS文件上传失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                aliyunOssOperationUtils.closeOssClient(ossClient);
            }
        }
    }

    /**
     * 上传图片到阿里云OSS并异步生成预览图
     * <p>
     * 图片上传处理流程：
     * <ol>
     *   <li>上传原图到阿里云OSS的指定路径</li>
     *   <li>异步压缩原图并生成预览图</li>
     *   <li>返回包含原图和预览图信息的上传结果</li>
     * </ol>
     *
     * <p>目录结构：</p>
     * <ul>
     *   <li>原图路径：resource/yyyy/MM/image/original/文件名</li>
     *   <li>预览图路径：resource/yyyy/MM/image/preview/文件名</li>
     * </ul>
     *
     * @param file 要上传的图片文件，不能为null
     * @return 上传成功后的文件信息，包含原图和预览图的访问URL、相对路径等
     * @throws FileException 当图片上传失败时抛出
     */
    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) {
        OSS ossClient = null;
        try {
            // 获取阿里云OSS配置
            AliyunOSSStorageConfig config = getAliyunOssConfig();
            ossClient = aliyunOssOperationUtils.createOssClient(config);
            String bucketName = config.getBucketName();
            String dateDir = StorageUtils.createDateDir();
            String originalFilename = file.getOriginalFilename();
            String newFileName = StorageUtils.generateFileName(originalFilename);

            // 确保存储桶存在
            aliyunOssOperationUtils.ensureBucketExists(ossClient, bucketName);

            // 构建原图路径：resource/yyyy/MM/image/original/文件名
            String originalPath = Paths.get(StorageConstants.dirName.RESOURCE, dateDir, StorageConstants.dirName.IMAGE,
                    StorageConstants.dirName.ORIGINAL, newFileName).toString();

            // 构建预览图路径：resource/yyyy/MM/image/preview/文件名
            String previewPath = Paths.get(StorageConstants.dirName.RESOURCE, dateDir, StorageConstants.dirName.IMAGE,
                    StorageConstants.dirName.PREVIEW, newFileName).toString();

            // 上传原图到阿里云OSS
            aliyunOssOperationUtils.uploadFile(ossClient, bucketName, originalPath, file.getInputStream(),
                    file.getSize(), file.getContentType());

            // 异步生成预览图
            storageAsyncService.compressImageAliyunOss(bucketName, originalPath, previewPath,
                    StorageConstants.imageCompression.MAX_WIDTH, StorageConstants.imageCompression.MAX_HEIGHT,
                    StorageConstants.imageCompression.QUALITY, originalFilename);

            // 构建返回的文件信息
            UploadedFileInfo uploadedFileInfo = buildImageFileInfo(originalFilename, originalPath, previewPath, newFileName,
                    file.getContentType(), file.getSize());

            log.info("阿里云OSS图片上传成功: {} (异步生成预览图: {})", originalPath, previewPath);
            return uploadedFileInfo;

        } catch (Exception e) {
            log.error("阿里云OSS图片上传失败", e);
            throw new FileException("阿里云OSS图片上传失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                aliyunOssOperationUtils.closeOssClient(ossClient);
            }
        }
    }

    /**
     * 删除文件
     * <p>
     * 支持两种删除模式：
     * <ul>
     *   <li>软删除（默认）：将文件移动到回收站，保留文件数据</li>
     *   <li>强制删除：直接从阿里云OSS删除文件，不可恢复</li>
     * </ul>
     *
     * <p>回收站目录结构与原目录结构保持一致：</p>
     * <pre>
     * trash/yyyy/MM/image/original/文件名
     * trash/yyyy/MM/image/preview/文件名
     * </pre>
     *
     * @param fileOperationDto 文件操作DTO，包含文件路径信息
     * @param forceDelete      删除模式，true为强制删除，false为软删除
     * @return 软删除时返回包含回收站路径的DTO，强制删除时返回原DTO
     * @throws FileException 当删除操作失败时抛出
     */
    @Override
    public FileOperationDto delete(FileOperationDto fileOperationDto, boolean forceDelete) {
        OSS ossClient = null;
        try {
            // 获取阿里云OSS配置
            AliyunOSSStorageConfig config = getAliyunOssConfig();
            ossClient = aliyunOssOperationUtils.createOssClient(config);
            String bucketName = config.getBucketName();

            // 确保存储桶存在
            aliyunOssOperationUtils.ensureBucketExists(ossClient, bucketName);

            String originalPath = fileOperationDto.getOriginalRelativePath();
            String previewTrashPath = null;

            if (forceDelete) {
                // 强制删除，直接从阿里云OSS删除文件
                aliyunOssOperationUtils.deleteObject(ossClient, bucketName, originalPath);
                log.info("阿里云OSS文件强制删除成功: {}", originalPath);

                // 删除预览文件
                String previewPath = fileOperationDto.getPreviewRelativePath();
                if (previewPath != null && aliyunOssOperationUtils.objectExists(ossClient, bucketName, previewPath)) {
                    aliyunOssOperationUtils.deleteObject(ossClient, bucketName, previewPath);
                    log.info("阿里云OSS预览文件强制删除成功: {}", previewPath);
                }
            } else {
                // 软删除，移动到回收站
                // 处理原始文件移动到回收站
                String pathWithoutResourcePrefix = StorageUtils.removeResourcePrefix(originalPath);
                String originalTrashPath = Paths.get(StorageConstants.dirName.TRASH, pathWithoutResourcePrefix)
                        .toString();
                aliyunOssOperationUtils.moveObject(ossClient, bucketName, originalPath, originalTrashPath);

                // 处理预览文件移动到回收站
                String previewPath = fileOperationDto.getPreviewRelativePath();
                if (previewPath != null && aliyunOssOperationUtils.objectExists(ossClient, bucketName, previewPath)) {
                    String previewPathWithoutResourcePrefix = StorageUtils.removeResourcePrefix(previewPath);
                    previewTrashPath = Paths.get(StorageConstants.dirName.TRASH, previewPathWithoutResourcePrefix)
                            .toString();
                    aliyunOssOperationUtils.moveObject(ossClient, bucketName, previewPath, previewTrashPath);
                }

                log.info("阿里云OSS文件移动到回收站成功: {} -> {}", originalPath, originalTrashPath);

                // 更新FileOperationDto中的路径信息为回收站路径
                fileOperationDto.setOriginalTrashPath(originalTrashPath);
                fileOperationDto.setPreviewTrashPath(previewTrashPath);
            }

            return fileOperationDto;

        } catch (Exception e) {
            log.error("阿里云OSS文件删除失败", e);
            throw new FileException("阿里云OSS文件删除失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                aliyunOssOperationUtils.closeOssClient(ossClient);
            }
        }
    }

    /**
     * 从回收站恢复文件
     * <p>
     * 将回收站中的文件移动回原始位置，恢复文件的正常访问。
     * 同时处理原图和预览图的恢复。
     *
     * @param fileOperationDto 文件操作DTO，包含回收站路径信息
     * @return 恢复成功返回true，失败返回false
     * @throws FileException 当恢复操作失败时抛出
     */
    @Override
    public boolean restore(FileOperationDto fileOperationDto) {
        OSS ossClient = null;
        try {
            // 获取阿里云OSS配置
            AliyunOSSStorageConfig config = getAliyunOssConfig();
            ossClient = aliyunOssOperationUtils.createOssClient(config);
            String bucketName = config.getBucketName();

            // 确保存储桶存在
            aliyunOssOperationUtils.ensureBucketExists(ossClient, bucketName);

            String trashPath = fileOperationDto.getOriginalTrashPath();

            // 检查回收站文件是否存在
            if (!aliyunOssOperationUtils.objectExists(ossClient, bucketName, trashPath)) {
                log.warn("阿里云OSS回收站文件不存在: {}", trashPath);
                return false;
            }

            // 构建恢复后的路径：从回收站路径恢复到resource路径
            String restoredPath;
            if (trashPath.startsWith(StorageConstants.dirName.TRASH + "/")) {
                String pathAfterTrash = trashPath.substring((StorageConstants.dirName.TRASH + "/").length());
                restoredPath = Paths.get(StorageConstants.dirName.RESOURCE, pathAfterTrash)
                        .toString();
            } else {
                log.error("阿里云OSS文件路径格式错误，不是有效的回收站路径: {}", trashPath);
                return false;
            }

            // 移动文件从回收站到正常位置
            aliyunOssOperationUtils.moveObject(ossClient, bucketName, trashPath, restoredPath);

            // 处理预览文件恢复
            String previewTrashPath = fileOperationDto.getPreviewTrashPath();
            String restoredPreviewPath = null;
            if (previewTrashPath != null && aliyunOssOperationUtils.objectExists(ossClient, bucketName, previewTrashPath)) {
                if (previewTrashPath.startsWith(StorageConstants.dirName.TRASH + "/")) {
                    String previewPathAfterTrash = previewTrashPath.substring((StorageConstants.dirName.TRASH + "/").length());
                    restoredPreviewPath = Paths.get(StorageConstants.dirName.RESOURCE, previewPathAfterTrash)
                            .toString();
                    aliyunOssOperationUtils.moveObject(ossClient, bucketName, previewTrashPath, restoredPreviewPath);
                }
            }

            // 更新FileOperationDto中的路径信息
            fileOperationDto.setOriginalRelativePath(restoredPath);
            fileOperationDto.setPreviewRelativePath(restoredPreviewPath);

            log.info("阿里云OSS文件恢复成功: {} -> {}", trashPath, restoredPath);
            return true;

        } catch (Exception e) {
            log.error("阿里云OSS文件恢复失败", e);
            throw new FileException("阿里云OSS文件恢复失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                aliyunOssOperationUtils.closeOssClient(ossClient);
            }
        }
    }

    /**
     * 永久删除回收站中的文件
     * <p>
     * 从阿里云OSS中彻底删除回收站中的文件，包括原图和预览图。
     * 此操作不可恢复，请谨慎使用。
     *
     * @param fileOperationDto 文件操作DTO，包含回收站路径信息
     * @throws FileException 当删除操作失败时抛出
     */
    @Override
    public void deleteTrashFile(FileOperationDto fileOperationDto) {
        OSS ossClient = null;
        try {
            // 获取阿里云OSS配置
            AliyunOSSStorageConfig config = getAliyunOssConfig();
            ossClient = aliyunOssOperationUtils.createOssClient(config);
            String bucketName = config.getBucketName();

            // 确保存储桶存在
            aliyunOssOperationUtils.ensureBucketExists(ossClient, bucketName);

            // 删除回收站中的原文件
            String trashPath = fileOperationDto.getOriginalTrashPath();
            if (aliyunOssOperationUtils.objectExists(ossClient, bucketName, trashPath)) {
                aliyunOssOperationUtils.deleteObject(ossClient, bucketName, trashPath);
                log.info("阿里云OSS回收站文件删除成功: {}", trashPath);
            }

            // 删除回收站中的预览文件
            String previewTrashPath = fileOperationDto.getPreviewTrashPath();
            if (previewTrashPath != null && aliyunOssOperationUtils.objectExists(ossClient, bucketName, previewTrashPath)) {
                aliyunOssOperationUtils.deleteObject(ossClient, bucketName, previewTrashPath);
                log.info("阿里云OSS回收站预览文件删除成功: {}", previewTrashPath);
            }

        } catch (Exception e) {
            log.error("阿里云OSS回收站文件删除失败", e);
            throw new FileException("阿里云OSS回收站文件删除失败: " + e.getMessage());
        } finally {
            if (ossClient != null) {
                aliyunOssOperationUtils.closeOssClient(ossClient);
            }
        }
    }

    /**
     * 构建图片文件信息
     * <p>
     * 根据上传的图片信息构建返回给客户端的文件信息对象，
     * 包含原图和预览图的URL、路径等信息。
     *
     * @param originalFileName  原始文件名
     * @param originalImagePath 原图在阿里云OSS中的路径
     * @param previewImagePath  预览图在阿里云OSS中的路径
     * @param newFileName       生成的新文件名
     * @param fileType          文件MIME类型
     * @param fileSize          文件大小（字节）
     * @return 包含完整文件信息的UploadedFileInfo对象
     */
    private UploadedFileInfo buildImageFileInfo(String originalFileName, String originalImagePath,
                                                String previewImagePath, String newFileName,
                                                String fileType, long fileSize) {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(originalFileName);
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(fileSize);
        info.setFileType(fileType);
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(getAliyunOssConfig().getFileDomain(), originalImagePath).toString());
        info.setFileRelativePath(originalImagePath);
        info.setPreviewImage(Paths.get(getAliyunOssConfig().getFileDomain(), previewImagePath).toString());
        info.setPreviewImageRelativePath(previewImagePath);
        return info;
    }

    /**
     * 获取阿里云OSS配置
     * <p>
     * 从配置服务中获取当前激活的阿里云OSS存储配置信息。
     *
     * @return 阿里云OSS存储配置对象
     * @throws RuntimeException 当配置获取失败时抛出
     */
    private AliyunOSSStorageConfig getAliyunOssConfig() {
        try {
            String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
            if (json == null || json.isBlank()) {
                throw new RuntimeException("阿里云OSS配置未找到");
            }
            return JSON.parseObject(json, AliyunOSSStorageConfig.class);
        } catch (Exception e) {
            log.error("获取阿里云OSS配置失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取阿里云OSS配置失败: " + e.getMessage());
        }
    }
}
