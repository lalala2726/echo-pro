package cn.zhangchuangla.storage.core.service.impl;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import cn.zhangchuangla.storage.utils.ImageStreamUtils;
import cn.zhangchuangla.storage.utils.StorageUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 本地文件存储服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.springBeanName.LOCAL_STORAGE_SERVICE)
public class LocalFileOperationServiceImpl implements FileOperationService {


    private final StorageConfigRetrievalService storageConfigRetrievalService;
    private LocalFileStorageConfig localFileStorageConfig;

    public LocalFileOperationServiceImpl(StorageConfigRetrievalService storageConfigRetrievalService) {
        this.storageConfigRetrievalService = storageConfigRetrievalService;
    }

    /**
     * 每次操作前拉取最新配置
     */
    private void initConfig() {
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        if (!StorageConstants.StorageType.LOCAL.equals(activeStorageType)) {
            throw new FileException("存储配置异常!请刷新配置后再试!");
        }
        String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
        if (json == null || json.isBlank()) {
            throw new FileException("本地文件存储配置未找到");
        }
        this.localFileStorageConfig = JSON.parseObject(json, LocalFileStorageConfig.class);
    }


    /**
     * 保存原始文件到本地。
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        initConfig();
        try {
            String datePath = todayDir();
            String newFileName = StorageUtils.generateFileName(Objects.requireNonNull(file.getOriginalFilename()));
            String targetDirectory = Paths.get(datePath, StorageConstants.dirName.FILE).toString();
            File destDir = ensureDir(targetDirectory);
            File destFile = new File(destDir, newFileName);

            file.transferTo(destFile);

            return buildFileInfo(file, destFile, targetDirectory, newFileName);
        } catch (IOException e) {
            log.error("文件上传传失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 图片上传处理方法
     * <p>
     * 处理流程：
     * 1. 校验文件为有效图片格式
     * 2. 创建日期目录结构（包含原图和压缩图目录）
     * 3. 先保存原始图片到指定路径
     * 4. 对原始图片进行压缩处理并保存
     * 5. 返回压缩后的图片信息
     *
     * @param file 上传的MultipartFile对象
     * @return 包含上传文件信息的UploadedFileInfo对象
     * @throws IOException 文件操作异常时抛出
     */
    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) throws IOException {
        //最大宽
        int maxWidth = 1024;
        //最大高
        int maxHeight = 1024;
        //压缩质量
        float quality = 0.9f;

        initConfig();

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String fileExtension = StorageUtils.getFileExtension(originalFilename);
        if (!ImageStreamUtils.isImage(fileExtension)) {
            throw new FileException(ResponseCode.FileUploadFailed, "请上传有效的图片文件！");
        }

        String datePath = todayDir();
        String originalImageDir = Paths.get(datePath, StorageConstants.dirName.IMAGE, StorageConstants.dirName.ORIGINAL)
                .toString();
        String previewImageDir = Paths.get(datePath, StorageConstants.dirName.IMAGE, StorageConstants.dirName.PREVIEW)
                .toString();
        File originalDir = ensureDir(originalImageDir);
        File previewDir = ensureDir(previewImageDir);

        // 1. 生成并保存原图文件
        String originalFileName = StorageUtils.generateFileName(originalFilename);
        File originalFile = new File(originalDir, originalFileName);

        // 2. 准备压缩图目标文件
        File compressedFile = new File(previewDir, originalFileName);

        try {
            // 保存原图
            file.transferTo(originalFile);

            // 使用流式处理压缩图片
            try (InputStream in = new FileInputStream(originalFile);
                 OutputStream out = new FileOutputStream(compressedFile)) {
                ImageStreamUtils.compress(in, out, maxWidth, maxHeight,quality, originalFilename);
            }

        } catch (IOException e) {
            log.error("图片保存或压缩失败", e);
            // 清理可能已创建的文件，避免残留
            FileUtils.deleteQuietly(originalFile);
            FileUtils.deleteQuietly(compressedFile);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片处理失败：" + e.getMessage());
        }

        // 返回压缩图的信息，同时保留原图在磁盘上供后续使用
        return buildFileInfo(originalFilename, compressedFile, previewImageDir, originalFileName, file.getContentType());
    }


    /**
     * 删除文件
     *
     * @param relativePath 文件相对路径
     * @param realDelete   是否真实删除
     * @return 操作是否成功
     */
    @Override
    public boolean delete(String relativePath, boolean realDelete) {
        initConfig();
        File file = new File(localFileStorageConfig.getUploadPath(), relativePath);
        if (!file.exists()) {
            log.warn("文件不存在，删除失败: {}", relativePath);
            return false;
        }

        if (realDelete) {
            // 真实删除
            return FileUtils.deleteQuietly(file);
        } else {
            // 移动到回收站
            File trashDir = new File(localFileStorageConfig.getUploadPath(), "trash");
            try {
                FileUtils.moveFileToDirectory(file, trashDir, true);
                return true;
            } catch (IOException e) {
                log.error("文件移入回收站失败", e);
                return false;
            }
        }
    }

    /**
     * 恢复文件
     *
     * @param originalRelativePath 原始文件相对路径
     * @param trashRelativePath    回收站文件相对路径
     * @return 操作是否成功
     */
    @Override
    public boolean restore(String originalRelativePath, String trashRelativePath) {
        initConfig();
        File trashFile = new File(localFileStorageConfig.getUploadPath(), trashRelativePath);
        if (!trashFile.exists()) {
            log.warn("回收站文件不存在，恢复失败: {}", trashRelativePath);
            return false;
        }

        File destDir = new File(localFileStorageConfig.getUploadPath(), originalRelativePath).getParentFile();
        try {
            FileUtils.moveFileToDirectory(trashFile, destDir, true);
            return true;
        } catch (IOException e) {
            log.error("文件恢复失败", e);
            return false;
        }
    }

    /**
     * 清空回收站
     *
     * @param relativePath 文件相对路径
     * @return 操作是否成功
     */
    @Override
    public boolean deleteTrash(String relativePath) {
        initConfig();
        File file = new File(localFileStorageConfig.getUploadPath(), relativePath);
        if (!file.exists()) {
            log.warn("回收站文件不存在，删除失败: {}", relativePath);
            return false;
        }
        return FileUtils.deleteQuietly(file);
    }

    /**
     * 构建文件信息
     *
     * @param src         文件源
     * @param savedFile   保存文件
     * @param targetPath  文件保存路径
     * @param newFileName 新文件名
     * @return 文件信息
     * @throws IOException 构建文件信息异常
     */
    private UploadedFileInfo buildFileInfo(MultipartFile src, File savedFile, String targetPath, String newFileName) throws IOException {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(src.getOriginalFilename());
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(String.valueOf(savedFile.length()));
        info.setFileType(src.getContentType());
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, targetPath, newFileName).toString());
        info.setFileRelativePath(Paths.get(targetPath, newFileName).toString());
        return info;
    }

    /**
     * 构建文件信息
     *
     * @param originalFileName 文件源
     * @param savedFile        保存文件
     * @param datePath         文件保存路径
     * @param newFileName      新文件名
     * @return 文件信息
     */
    private UploadedFileInfo buildFileInfo(String originalFileName, File savedFile, String datePath, String newFileName, String fileType) {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(originalFileName);
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(String.valueOf(savedFile.length()));
        info.setFileType(fileType);
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, datePath, newFileName).toString());
        info.setFileRelativePath(Paths.get(datePath, newFileName).toString());
        return info;
    }


    /**
     * 日期目录可以在这边统一修改{@link StorageConstants}
     */
    private String todayDir() {
        return new SimpleDateFormat(StorageConstants.FILE_UPLOAD_PATH_FORMAT).format(new Date());
    }

    /**
     * 根据目录确保存在,并返回文件夹
     */
    private File ensureDir(String path) throws IOException {
        File dir = new File(localFileStorageConfig.getUploadPath(), path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("无法创建目录: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }

}

