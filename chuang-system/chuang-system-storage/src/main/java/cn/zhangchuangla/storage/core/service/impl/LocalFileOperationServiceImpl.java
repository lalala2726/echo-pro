package cn.zhangchuangla.storage.core.service.impl;

import cn.idev.excel.util.StringUtils;
import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.FileException;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.core.service.FileOperationService;
import cn.zhangchuangla.storage.core.service.StorageConfigRetrievalService;
import cn.zhangchuangla.storage.model.dto.UploadedFileInfo;
import cn.zhangchuangla.storage.model.entity.config.LocalFileStorageConfig;
import cn.zhangchuangla.storage.utils.StorageUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * 本地文件存储服务实现类
 *
 * @author Chuang
 */
@Slf4j
@Service(StorageConstants.LOCAL_STORAGE_SERVICE)
public class LocalFileOperationServiceImpl implements FileOperationService {

    private LocalFileStorageConfig localFileStorageConfig;
    private final StorageConfigRetrievalService storageConfigRetrievalService;

    public LocalFileOperationServiceImpl(StorageConfigRetrievalService storageConfigRetrievalService) {
        this.storageConfigRetrievalService = storageConfigRetrievalService;
    }

    /**
     * 初始化配置, 确保在每次调用服务方法之前调用
     */
    private void initConfig() {
        String activeStorageType = storageConfigRetrievalService.getActiveStorageType();
        if (!StorageConstants.LOCAL.equals(activeStorageType)) {
            throw new FileException("存储配置异常!请你文件配置刷新配置再试!");
        }
        String json = storageConfigRetrievalService.getCurrentStorageConfigJson();
        if (StringUtils.isBlank(json)) {
            throw new FileException("本地文件存储配置未找到");
        }
        this.localFileStorageConfig = JSON.parseObject(json, LocalFileStorageConfig.class);

    }

    /**
     * 上传文件
     *
     * @param file 文件传输数据传输对象
     * @return 上传成功后的文件信息
     */
    @Override
    public UploadedFileInfo upload(MultipartFile file) {
        initConfig();
        try {
            UploadedFileInfo uploadedFileInfo = new UploadedFileInfo();
            // 生成年月日目录结构
            String datePath = new java.text.SimpleDateFormat("yyyy/MM/dd").format(new java.util.Date());
            File destDir = new File(localFileStorageConfig.getUploadPath(), datePath);

            uploadedFileInfo.setFileExtension(file.getOriginalFilename());
            uploadedFileInfo.setFileSize(String.valueOf(file.getSize()));
            uploadedFileInfo.setFileType(file.getContentType());
            uploadedFileInfo.setFileOriginalName(file.getOriginalFilename());

            // 生成文件名并保存文件
            String newFileName = StorageUtils.generateFileName(file.getOriginalFilename());
            File destFile = new File(destDir, newFileName);
            file.transferTo(destFile);

            uploadedFileInfo.setFileName(newFileName);
            uploadedFileInfo.setFileUrl(Paths.get(localFileStorageConfig.getFileDomain(), Constants.RESOURCE_PREFIX, datePath, newFileName).toString());
            uploadedFileInfo.setFileRelativePath(Paths.get(datePath,newFileName).toString());


            return uploadedFileInfo;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件上传失败：" + e.getMessage());
        }
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
            File trashDir = new File(localFileStorageConfig.getUploadPath(), StorageConstants.TRASH_DIR);
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
