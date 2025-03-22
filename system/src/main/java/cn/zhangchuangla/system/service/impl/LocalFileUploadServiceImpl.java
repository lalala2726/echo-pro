package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.system.model.dto.FileUploadByByteDto;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.service.LocalFileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
@Slf4j
@Service
public class LocalFileUploadServiceImpl implements LocalFileUploadService {

    private final ConfigCacheService configCacheService;

    @Autowired
    public LocalFileUploadServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }

    /**
     * 本地文件上传
     *
     * @param fileUploadByByteDto 文件信息
     * @return 文件访问路径和文件存储相对路径
     */
    @Override
    public HashMap<String, String> localUploadBytes(FileUploadByByteDto fileUploadByByteDto) {
        String fileName = fileUploadByByteDto.getFileName();
        byte[] data = fileUploadByByteDto.getData();
        boolean compress = fileUploadByByteDto.isCompress();

        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        String uploadPath = localFileConfig.getUploadPath();

        // 生成年月格式的目录
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
        String yearMonthDir = yearMonthFormat.format(new Date());

        // 确定存储目录 - 根据compress参数决定
        String folderType = compress ? Constants.FILE_PREVIEW_FOLDER : Constants.FILE_ORIGINAL_FOLDER;
        String targetDir = yearMonthDir + File.separator + folderType;

        // 生成唯一文件名
        String fileExtension = "";
        if (fileName.contains(".")) {
            fileExtension = fileName.substring(fileName.lastIndexOf("."));
        }

        String uuidFileName = String.format("%s%s",
                System.currentTimeMillis(),
                UUID.randomUUID().toString().substring(0, 8));

        // 只保留小写字母和数字
        Pattern pattern = Pattern.compile("[^a-z0-9]");
        uuidFileName = pattern.matcher(uuidFileName).replaceAll("");

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

        // 构建相对路径和URL
        String relativeFileLocation = ("/" + targetDir + "/" + uuidFileName + fileExtension)
                .replace('\\', '/');
        String fileUrl = Constants.RESOURCE_PREFIX + relativeFileLocation;

        // 返回结果
        HashMap<String, String> result = new HashMap<>();
        result.put(Constants.FILE_URL, fileUrl);
        result.put(Constants.RELATIVE_FILE_LOCATION, relativeFileLocation);

        // 添加额外信息表明是否为压缩版本
        result.put("isCompressed", String.valueOf(compress));

        return result;
    }

    /**
     * 根据文件ID删除文件
     *
     * @param fileManagement 文件管理实体
     */
    @Override
    public void deleteFileByFileId(FileManagement fileManagement) {
        if (fileManagement == null) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "文件管理实体不能为空！");
        }
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        try {
            if (fileManagement.getOriginalRelativeFileLocation() != null) {
                File originalFile = new File(localFileConfig.getUploadPath() + fileManagement.getOriginalRelativeFileLocation());
                FileUtils.delete(originalFile);
            }
            if (fileManagement.getPreviewRelativeFileLocation() != null) {
                String previewRelativeFileLocation = fileManagement.getPreviewRelativeFileLocation();
                File previewFile = new File(localFileConfig.getUploadPath() + previewRelativeFileLocation);
                FileUtils.delete(previewFile);
            }
        } catch (IOException e) {
            log.warn("文件删除失败", e);
        }

    }
}
