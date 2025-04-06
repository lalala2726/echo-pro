package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
public class LocalStorageUtils {

    @Resource(name = "appConfig")
    private AppConfig appConfig;

    /**
     * 上传文件到本地存储
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
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
                .relativePath(relativeFileLocation)
                .build();
    }
}
