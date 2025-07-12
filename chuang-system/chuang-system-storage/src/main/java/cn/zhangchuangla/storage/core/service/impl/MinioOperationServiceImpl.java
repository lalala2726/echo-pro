package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.storage.async.StorageAsyncService;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.OperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.FileOperationDto;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.storage.utils.MinioOperationUtils;
import cn.zhangchuangla.storage.utils.StorageUtils;
import com.alibaba.fastjson2.JSON;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * MinIO文件操作服务实现类
 * 支持运行时动态切换存储类型，使用MinioOperationUtils进行MinIO操作
 *
 * @author Chuang
 * <p>
 * created on 2025/7/3 06:26
 */
@Slf4j
@Service(StorageConstants.springBeanName.MINIO_STORAGE_SERVICE)
@RequiredArgsConstructor
public class MinioOperationServiceImpl implements OperationService {

    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private final StorageAsyncService storageAsyncService;
    private final MinioOperationUtils minioOperationUtils;

    // 内部管理的MinIO客户端，支持动态创建和缓存
    private volatile MinioClient minioClient;
    private volatile MinioStorageConfig minioStorageConfig;

    /**
     * 获取当前存储配置
     * 每次操作前都会重新拉取最新配置确保配置的时效性
     *
     * @return MinIO存储配置
     */
    public MinioStorageConfig getConfig() {
        String json = getStorageConfigJson();
        MinioStorageConfig newConfig = JSON.parseObject(json, MinioStorageConfig.class);

        // 检查配置是否发生变化，如果变化则重置客户端
        if (configChanged(newConfig)) {
            log.info("检测到MinIO配置变更，将重新创建客户端");
            synchronized (this) {
                if (configChanged(newConfig)) {
                    // 重置客户端，下次使用时重新创建
                    this.minioClient = null;
                    this.minioStorageConfig = newConfig;
                }
            }
        } else {
            this.minioStorageConfig = newConfig;
        }

        return minioStorageConfig;
    }

    @NotNull
    private String getStorageConfigJson() {
        String storageType = storageConfigRetrievalService.getActiveStorageType();
        String json = storageConfigRetrievalService.getCurrentStorageConfigJson();

        // 验证当前激活的存储类型是否为MinIO
        if (!StorageConstants.StorageType.MINIO.equals(storageType)) {
            throw new FileException(String.format("当前调用的服务是:%s,而你激活的配置是:%s,调用的服务和激活的配置不符合!请你仔细检查配置!",
                    StorageConstants.StorageType.MINIO, storageType));
        }

        // 验证配置JSON是否存在
        if (json == null || json.isBlank()) {
            throw new FileException("MinIO文件存储配置未找到");
        }
        return json;
    }

    /**
     * 检查配置是否发生变化
     *
     * @param newConfig 新配置
     * @return 是否发生变化
     */
    private boolean configChanged(MinioStorageConfig newConfig) {
        if (minioStorageConfig == null) {
            return true;
        }

        return !Objects.equals(minioStorageConfig.getEndpoint(), newConfig.getEndpoint()) ||
                !Objects.equals(minioStorageConfig.getAccessKey(), newConfig.getAccessKey()) ||
                !Objects.equals(minioStorageConfig.getSecretKey(), newConfig.getSecretKey()) ||
                !Objects.equals(minioStorageConfig.getBucketName(), newConfig.getBucketName());
    }

    /**
     * 获取或创建MinIO客户端
     * 支持运行时动态创建客户端，实现存储类型的热切换
     * 使用双重检查锁定确保线程安全
     *
     * @return MinioClient实例
     */
    private MinioClient getMinioClient() {
        // 双重检查锁定模式
        if (minioClient == null) {
            synchronized (this) {
                if (minioClient == null) {
                    getConfig(); // 确保配置已加载
                    minioClient = minioOperationUtils.createMinioClient(minioStorageConfig);
                }
            }
        }
        return minioClient;
    }

    /**
     * 普通文件上传到MinIO
     * 核心逻辑：
     * 1. 获取配置信息
     * 2. 生成新的文件名和对象路径
     * 3. 确保存储桶存在
     * 4. 上传文件到MinIO
     * 5. 构建并返回文件信息
     *
     * @param file 要上传的文件
     * @return 上传后的文件信息
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        getConfig();
        try {
            String bucketName = minioStorageConfig.getBucketName();
            String dateDir = StorageUtils.createDateDir();
            String newFileName = StorageUtils.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));

            // 构建MinIO对象路径：日期目录/file/文件名
            String objectPath = Paths.get(StorageConstants.dirName.RESOURCE, dateDir, StorageConstants.dirName.FILE, newFileName).toString().replace("\\", "/");

            // 获取MinIO客户端并确保存储桶存在
            MinioClient client = getMinioClient();
            minioOperationUtils.ensureBucketExists(client, bucketName);

            // 上传文件到MinIO - 核心上传逻辑
            minioOperationUtils.uploadFile(client, bucketName, objectPath, file.getInputStream(), file.getSize(), file.getContentType());

            log.info("文件上传成功到MinIO: {}/{}", bucketName, objectPath);

            // 构建文件信息返回给调用方
            return buildFileInfo(file, objectPath, newFileName);

        } catch (Exception e) {
            log.error("MinIO文件上传失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 图片上传处理方法
     * 核心逻辑：
     * 1. 校验文件为有效图片格式
     * 2. 上传原图到MinIO指定路径
     * 3. 异步处理图片压缩并上传到预览路径
     * 4. 返回包含原图和预览图路径的文件信息
     *
     * @param file 上传的图片文件
     * @return 包含上传文件信息的UploadedFileInfo对象
     */
    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) {
        getConfig();
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());

        try {
            String bucketName = minioStorageConfig.getBucketName();
            String dateDir = StorageUtils.createDateDir();
            String newFileName = StorageUtils.generateFileName(originalFilename);

            // 构建MinIO中的原图和预览图路径
            String originalImagePath = Paths.get(StorageConstants.dirName.RESOURCE, dateDir, StorageConstants.dirName.IMAGE,
                    StorageConstants.dirName.ORIGINAL, newFileName).toString().replace("\\", "/");
            String previewImagePath = Paths.get(StorageConstants.dirName.RESOURCE, dateDir, StorageConstants.dirName.IMAGE,
                    StorageConstants.dirName.PREVIEW, newFileName).toString().replace("\\", "/");

            // 获取MinIO客户端并确保存储桶存在
            MinioClient client = getMinioClient();
            minioOperationUtils.ensureBucketExists(client, bucketName);

            // 1. 先上传原图到MinIO - 核心原图上传逻辑
            minioOperationUtils.uploadFile(client, bucketName, originalImagePath, file.getInputStream(), file.getSize(), file.getContentType());

            log.info("原图上传成功到MinIO: {}/{}", bucketName, originalImagePath);

            // 2. 异步处理图片压缩并上传预览图 - 使用StorageAsyncService的MinIO异步压缩
            storageAsyncService.compressImageMinio(
                    bucketName,
                    originalImagePath,
                    previewImagePath,
                    StorageConstants.imageCompression.MAX_WIDTH,
                    StorageConstants.imageCompression.MAX_HEIGHT,
                    StorageConstants.imageCompression.QUALITY,
                    originalFilename
            );

            log.info("图片压缩任务已提交到异步服务: {}", previewImagePath);

            // 3. 构建并返回文件信息（包含原图和预览图路径）
            return buildImageFileInfo(originalFilename, originalImagePath, previewImagePath,
                    newFileName, file.getContentType(), file.getSize());

        } catch (Exception e) {
            log.error("MinIO图片上传失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件方法
     * 核心逻辑：
     * 1. 参数校验
     * 2. 获取MinIO配置
     * 3. 根据删除模式执行物理删除或移入回收站
     *
     * @param fileOperationDto 文件操作DTO
     * @param forceDelete      true: 强制从MinIO删除；false: 移入回收站目录
     * @return 如果是移入回收站，返回包含新路径的DTO；如果是强制删除，返回null
     */
    @Override
    public FileOperationDto delete(FileOperationDto fileOperationDto, boolean forceDelete) {
        // 1. 参数校验
        if (ObjectUtils.isEmpty(fileOperationDto)) {
            throw new ParamException(ResponseCode.PARAM_NOT_NULL, "参数不能为空");
        }
        if (StringUtils.isEmpty(fileOperationDto.getOriginalRelativePath())) {
            throw new ParamException(ResponseCode.PARAM_NOT_NULL, "原始文件路径不能为空");
        }

        // 2. 获取配置和初始化
        getConfig();
        String bucketName = minioStorageConfig.getBucketName();

        // 3. 判断删除模式 - 核心删除逻辑分发
        if (forceDelete && minioStorageConfig.isRealDelete()) {
            // 物理删除模式
            return performPhysicalDelete(bucketName, fileOperationDto);
        } else {
            // 移入回收站模式
            return performMoveToTrash(bucketName, fileOperationDto);
        }
    }

    /**
     * 执行MinIO物理删除操作
     *
     * @param bucketName       存储桶名称
     * @param fileOperationDto 文件操作DTO
     * @return null（物理删除完成）
     */
    private FileOperationDto performPhysicalDelete(String bucketName, FileOperationDto fileOperationDto) {
        log.info("开始执行MinIO物理删除操作，原始文件路径: {}", fileOperationDto.getOriginalRelativePath());

        try {
            MinioClient client = getMinioClient();

            // 删除原始文件
            minioOperationUtils.deleteObject(client, bucketName, fileOperationDto.getOriginalRelativePath());
            log.info("MinIO原始文件删除成功: {}", fileOperationDto.getOriginalRelativePath());

            // 删除预览文件（如果存在）
            if (StringUtils.isNotBlank(fileOperationDto.getPreviewRelativePath())) {
                minioOperationUtils.deleteObject(client, bucketName, fileOperationDto.getPreviewRelativePath());
                log.info("MinIO预览文件删除成功: {}", fileOperationDto.getPreviewRelativePath());
            }

            log.info("MinIO物理删除操作完成");
            return null;

        } catch (Exception e) {
            log.error("MinIO物理删除失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件物理删除失败: " + e.getMessage());
        }
    }

    /**
     * 执行移入回收站操作
     * 在MinIO中通过复制文件到trash目录实现逻辑删除
     *
     * @param bucketName       存储桶名称
     * @param fileOperationDto 文件操作DTO
     * @return 包含回收站路径的DTO
     */
    private FileOperationDto performMoveToTrash(String bucketName, FileOperationDto fileOperationDto) {
        log.info("开始执行MinIO移入回收站操作，原始文件路径: {}", fileOperationDto.getOriginalRelativePath());

        try {
            MinioClient client = getMinioClient();

            // 检查原始文件是否存在
            String originalPath = fileOperationDto.getOriginalRelativePath();

            if (!minioOperationUtils.objectExists(client, bucketName, originalPath)) {
                log.warn("MinIO文件不存在，无法移入回收站: {}", originalPath);
                return null;
            }

            // 处理原始文件移动到回收站
            String pathWithoutResourcePrefix = StorageUtils.removeResourcePrefix(originalPath);
            String originalTrashPath = Paths.get(StorageConstants.dirName.TRASH, pathWithoutResourcePrefix).toString().replace("\\", "/");
            minioOperationUtils.moveObject(client, bucketName, originalPath, originalTrashPath);
            log.info("原始文件移动到回收站成功: {} -> {}", originalPath, originalTrashPath);

            // 处理预览文件移动到回收站
            String previewTrashPath = null;
            if (StringUtils.isNotBlank(fileOperationDto.getPreviewRelativePath())) {
                String previewPath = fileOperationDto.getPreviewRelativePath();
                if (minioOperationUtils.objectExists(client, bucketName, previewPath)) {
                    String previewPathWithoutResourcePrefix = StorageUtils.removeResourcePrefix(previewPath);
                    previewTrashPath = Paths.get(StorageConstants.dirName.TRASH, previewPathWithoutResourcePrefix).toString().replace("\\", "/");
                    minioOperationUtils.moveObject(client, bucketName, previewPath, previewTrashPath);
                    log.info("预览文件移动到回收站成功: {} -> {}", previewPath, previewTrashPath);
                }
            }

            log.info("文件已成功移入MinIO回收站. 原始文件新路径: {}, 预览文件新路径: {}", originalTrashPath, previewTrashPath);

            return FileOperationDto.builder()
                    .originalTrashPath(originalTrashPath)
                    .previewTrashPath(previewTrashPath)
                    .build();

        } catch (Exception e) {
            log.error("MinIO移入回收站失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件移入回收站失败: " + e.getMessage());
        }
    }

    /**
     * 恢复文件
     * 核心逻辑：
     * 1. 参数校验
     * 2. 获取MinIO配置
     * 3. 从回收站恢复原始文件和预览文件
     *
     * @param fileOperationDto 文件操作DTO
     * @return 是否恢复成功
     */
    @Override
    public boolean restore(FileOperationDto fileOperationDto) {
        // 1. 参数校验
        if (fileOperationDto == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件记录为空");
        }

        // 2. 获取配置和初始化
        getConfig();
        String bucketName = minioStorageConfig.getBucketName();

        // 3. 获取路径信息
        String originalTrashPath = fileOperationDto.getOriginalTrashPath();
        String previewTrashPath = fileOperationDto.getPreviewTrashPath();
        String originalRelativePath = fileOperationDto.getOriginalRelativePath();
        String previewImagePath = fileOperationDto.getPreviewRelativePath();

        // 4. 校验必要路径
        if (StringUtils.isBlank(originalTrashPath) || StringUtils.isBlank(originalRelativePath)) {
            log.error("文件恢复失败：回收站路径或原始路径为空，文件ID: {}", fileOperationDto.getFileId());
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "此文件无法恢复!可能文件在MinIO中已经被删除!");
        }

        try {
            // 5. 恢复原始文件
            restoreOriginalFile(bucketName, originalTrashPath, originalRelativePath);

            // 6. 恢复预览文件（如果存在）
            restorePreviewFile(bucketName, previewTrashPath, previewImagePath);

            log.info("文件恢复成功，文件ID: {}, 原始路径: {}", fileOperationDto.getFileId(), originalRelativePath);
            return true;

        } catch (Exception e) {
            log.error("文件恢复失败，文件ID: {}, 错误信息: {}", fileOperationDto.getFileId(), e.getMessage(), e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件恢复失败: " + e.getMessage());
        }
    }

    /**
     * 恢复原始文件
     */
    private void restoreOriginalFile(String bucketName, String originalTrashPath, String originalRelativePath) {
        try {
            MinioClient client = getMinioClient();

            if (!minioOperationUtils.objectExists(client, bucketName, originalTrashPath)) {
                log.error("回收站中的文件不存在：{}", originalTrashPath);
                throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件在回收站中不存在，无法恢复");
            }

            // 移动文件从回收站到原位置
            minioOperationUtils.moveObject(client, bucketName, originalTrashPath, originalRelativePath);
            log.info("主文件恢复成功：{} -> {}", originalTrashPath, originalRelativePath);

        } catch (Exception e) {
            log.error("恢复原始文件失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "恢复原始文件失败: " + e.getMessage());
        }
    }

    /**
     * 恢复预览文件
     */
    private void restorePreviewFile(String bucketName, String previewTrashPath, String previewImagePath) {
        if (StringUtils.isBlank(previewTrashPath) || StringUtils.isBlank(previewImagePath)) {
            log.debug("预览文件路径为空，跳过预览文件恢复");
            return;
        }

        try {
            MinioClient client = getMinioClient();

            if (minioOperationUtils.objectExists(client, bucketName, previewTrashPath)) {
                // 移动预览图从回收站到原位置
                minioOperationUtils.moveObject(client, bucketName, previewTrashPath, previewImagePath);
                log.info("预览图恢复成功：{} -> {}", previewTrashPath, previewImagePath);
            } else {
                log.warn("预览图在回收站中不存在，跳过预览图恢复：{}", previewTrashPath);
            }

        } catch (Exception e) {
            log.error("恢复预览文件失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "恢复预览文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除回收站文件
     *
     * @param fileOperationDto 文件传输对象
     */
    @Override
    public void deleteTrashFile(FileOperationDto fileOperationDto) {
        getConfig();
        if (fileOperationDto == null) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件记录为空");
        }
        if (StringUtils.isBlank(fileOperationDto.getOriginalTrashPath())) {
            throw new FileException(ResponseCode.FILE_OPERATION_ERROR, "文件记录不能为空!");
        }

        // 如果不是真实删除，则直接返回成功
        boolean realDelete = minioStorageConfig.isRealDelete();
        if (!realDelete) {
            log.info("文件不是真实删除，系统将不会执行实际的删除操作!");
            return;
        }

        try {
            String bucketName = minioStorageConfig.getBucketName();
            MinioClient client = getMinioClient();

            // 1. 删除原始文件
            String originalTrashPath = fileOperationDto.getOriginalTrashPath();
            if (minioOperationUtils.objectExists(client, bucketName, originalTrashPath)) {
                minioOperationUtils.deleteObject(client, bucketName, originalTrashPath);
                log.info("回收站原始文件删除成功：{}", originalTrashPath);
            }

            // 2. 如果预览图存在，则删除预览图
            if (StringUtils.isNotBlank(fileOperationDto.getPreviewTrashPath())) {
                String previewTrashPath = fileOperationDto.getPreviewTrashPath();
                if (minioOperationUtils.objectExists(client, bucketName, previewTrashPath)) {
                    minioOperationUtils.deleteObject(client, bucketName, previewTrashPath);
                    log.info("回收站预览图删除成功：{}", previewTrashPath);
                }
            }

        } catch (Exception e) {
            log.error("删除回收站文件失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 构建文件信息
     *
     * @param src         文件源
     * @param objectPath  MinIO对象路径
     * @param newFileName 新文件名
     * @return 文件信息
     */
    private UploadedFileInfo buildFileInfo(MultipartFile src, String objectPath, String newFileName) {
        String bucketName = getConfig().getBucketName();
        UploadedFileInfo info = new UploadedFileInfo();
        info.setBucketName(bucketName);
        info.setFileOriginalName(src.getOriginalFilename());
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(src.getSize());
        info.setFileType(src.getContentType());
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(minioStorageConfig.getFileDomain(), objectPath).toString().replace("\\", "/"));
        info.setFileRelativePath(objectPath);
        return info;
    }

    /**
     * 构建图片文件信息
     *
     * @param originalFileName  原始文件名
     * @param originalImagePath 原图MinIO路径
     * @param previewImagePath  预览图MinIO路径
     * @param newFileName       新文件名
     * @param fileType          文件类型
     * @param fileSize          文件大小
     * @return 文件信息
     */
    private UploadedFileInfo buildImageFileInfo(String originalFileName, String originalImagePath,
                                                String previewImagePath, String newFileName,
                                                String fileType, long fileSize) {
        String bucketName = getConfig().getBucketName();
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(originalFileName);
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setBucketName(bucketName);
        info.setFileSize(fileSize);
        info.setFileType(fileType);
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(minioStorageConfig.getFileDomain(), originalImagePath).toString().replace("\\", "/"));
        info.setFileRelativePath(originalImagePath);
        info.setPreviewImage(Paths.get(minioStorageConfig.getFileDomain(), previewImagePath).toString().replace("\\", "/"));
        info.setPreviewImageRelativePath(previewImagePath);
        return info;
    }

}
