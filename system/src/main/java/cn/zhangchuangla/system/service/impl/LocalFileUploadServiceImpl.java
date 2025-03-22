package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.service.LocalFileUploadService;
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
@Service
public class LocalFileUploadServiceImpl implements LocalFileUploadService {

    private final ConfigCacheService configCacheService;

    @Autowired
    public LocalFileUploadServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }

    @Override
    public HashMap<String, String> localUploadBytes(byte[] data, String fileName) throws IOException {
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        String uploadPath = localFileConfig.getUploadPath();

        // 生成年月格式的目录
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
        String yearMonthDir = yearMonthFormat.format(new Date());

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

        // 设置目标路径，包含年月目录
        Path directory = Paths.get(uploadPath + File.separator + yearMonthDir);
        Path filePath = directory.resolve(uuidFileName + fileExtension);

        // 创建目录，如果不存在则创建
        Files.createDirectories(directory);

        // 写入文件
        Files.write(filePath, data);
        String relativeFileLocation = "/" + yearMonthDir + "/" + uuidFileName + fileExtension;
        //拼接文件URL
        String fileUrl = Constants.RESOURCE_PREFIX + relativeFileLocation;

        HashMap<String, String> result = new HashMap<>();
        result.put(Constants.FILE_URL, fileUrl);
        result.put(Constants.RELATIVE_FILE_LOCATION, relativeFileLocation);
        return result;
    }

    @Override
    public void deleteFileByFileId(FileManagement fileManagement) {

    }
}
