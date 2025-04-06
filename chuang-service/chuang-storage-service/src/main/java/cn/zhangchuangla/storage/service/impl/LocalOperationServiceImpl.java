package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.service.LocalOperationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:42
 */
@Service
@Slf4j
public class LocalOperationServiceImpl implements LocalOperationService {

    @Resource(name = "appConfig")
    private AppConfig appConfig;


    @Override
    public FileTransferDto save(FileTransferDto fileTransferDto) {
        String fileDomain = null;
        String uploadPath = null;
        uploadPath = appConfig.getUploadPath();
        try {
            fileDomain = appConfig.getFileDomain();
        } catch (NullPointerException e) {
            //文件域名不是必填项，可以不配置，默认为空字符串
            fileDomain = "";
        }
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        String targetDir = FileUtils.generateYearMonthDir();
        // 获取文件扩展名
        String fileExtension = FileUtils.getFileExtension(fileName);
        String uuidFileName = FileUtils.generateFileName();

        // 设置目标路径，包含年月目录和文件夹类型
        Path directory = Paths.get(uploadPath + File.separator + targetDir);
        Path filePath = directory.resolve(uuidFileName + fileExtension);

        try {
            // 创建目录，如果不存在则创建
            Files.createDirectories(directory);
            // 写入文件
            Files.write(filePath, data);
        } catch (IOException e) {
            log.error("文件写入失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件写入失败！");
        }

        String relativeFileLocation = FileUtils.generateFileRelativePath(targetDir, uuidFileName, fileExtension);
        String fileUrl = Constants.RESOURCE_PREFIX + relativeFileLocation;
        // 构建文件URL
        if (!StringUtils.isEmpty(fileDomain)) {
            fileUrl = FileUtils.buildFinalPath(fileDomain, fileUrl);
        }
        return FileTransferDto.builder()
                .fileUrl(fileUrl)
                .build();
    }
}
