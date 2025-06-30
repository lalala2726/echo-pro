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
     * 通用日期目录 "yyyy/MM/dd"
     */
    private String todayDir() {
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }

    /**
     * 根据目录确保存在
     */
    private File ensureDir(String datePath) throws IOException {
        File dir = new File(localFileStorageConfig.getUploadPath(), datePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("无法创建目录: " + dir.getAbsolutePath());
        }
        return dir;
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
            File destDir = ensureDir(datePath);
            File destFile = new File(destDir, newFileName);

            file.transferTo(destFile);

            return buildFileInfo(file, destFile, datePath, newFileName);
        } catch (IOException e) {
            log.error("文件上传传失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 1. 保存原图 -> 2. 基于磁盘文件流式压缩 -> 3. 保存压缩图 -> 返回压缩图信息
     */
    @Override
    public UploadedFileInfo uploadImage(MultipartFile file) throws IOException {
        initConfig();

        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String fileExtension = StorageUtils.getFileExtension(originalFilename);
        if (!ImageStreamUtils.isImage(fileExtension)) {
            throw new FileException(ResponseCode.FileUploadFailed, "请上传有效的图片文件！");
        }

        String datePath = todayDir();
        File destDir = ensureDir(datePath);

        // 1. 先保存原图
        String originalFileName = StorageUtils.generateFileName(originalFilename);
        File originalFile = new File(destDir, originalFileName);

        // 2. 生成压缩图文件名（加上compressed前缀或后缀标识）
        String compressedFileName = "compressed_" + originalFileName;
        File compressedFile = new File(destDir, compressedFileName);

        try {
            // 保存原图
            file.transferTo(originalFile);

            // 基于已保存的原图文件进行压缩
            try (InputStream in = new FileInputStream(originalFile);
                 OutputStream out = new FileOutputStream(compressedFile)) {
                ImageStreamUtils.compress(in, out, 1024, 1024, 0.9f, originalFilename);
            }

        } catch (IOException e) {
            log.error("图片保存或压缩失败", e);
            // 清理可能已创建的文件
            FileUtils.deleteQuietly(originalFile);
            FileUtils.deleteQuietly(compressedFile);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片处理失败：" + e.getMessage());
        }

        // 返回压缩图的信息，但保留原图在磁盘上
        return buildFileInfo(originalFilename, compressedFile, datePath, compressedFileName, file.getContentType());
    }


    /**
     * 构建文件信息
     *
     * @param src         文件源
     * @param savedFile   保存文件
     * @param datePath    文件保存路径
     * @param newFileName 新文件名
     * @return 文件信息
     * @throws IOException 构建文件信息异常
     */
    private UploadedFileInfo buildFileInfo(MultipartFile src, File savedFile, String datePath, String newFileName) throws IOException {
        UploadedFileInfo info = new UploadedFileInfo();
        info.setFileOriginalName(src.getOriginalFilename());
        info.setFileName(newFileName);
        info.setFileExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileSize(String.valueOf(savedFile.length()));
        info.setFileType(src.getContentType());
        info.setExtension(StorageUtils.getFileExtension(newFileName));
        info.setFileUrl(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, datePath, newFileName).toString());
        info.setFileRelativePath(Paths.get(datePath, newFileName).toString());
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
}

