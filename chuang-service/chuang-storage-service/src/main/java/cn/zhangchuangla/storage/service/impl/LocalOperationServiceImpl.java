package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.storage.entity.FileTransferDto;
import cn.zhangchuangla.storage.service.LocalOperationService;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class LocalOperationServiceImpl implements LocalOperationService {


    @Override
    public FileTransferDto save(FileTransferDto fileTransferDto) {
        // 使用appConfig获取本地文件路径

        //todo 暂时不获取本地文件路径
        String localPath = "";
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        String targetDir = FileUtils.generateYearMonthDir();
        // 获取文件扩展名
        String fileExtension = FileUtils.getFileExtension(fileName);
        String uuidFileName = FileUtils.generateFileName();

        // 设置目标路径，包含年月目录和文件夹类型
        Path directory = Paths.get(localPath + File.separator + targetDir);
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

        return FileTransferDto.builder()
                .fileUrl(fileUrl)
                .relativePath(relativeFileLocation)
                .build();
    }
}
