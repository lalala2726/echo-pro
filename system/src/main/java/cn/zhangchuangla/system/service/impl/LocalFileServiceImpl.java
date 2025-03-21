package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.system.service.LocalFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * @author zhangchuang
 * Created on 2025/2/17 15:41
 */
@Slf4j
@Service
public class LocalFileServiceImpl implements LocalFileService {

    private final AppConfig appConfig;
    private final ConfigCacheService configCacheService;

    public LocalFileServiceImpl(AppConfig appConfig, ConfigCacheService configCacheService) {
        this.appConfig = appConfig;
        this.configCacheService = configCacheService;
    }

    /**
     * 本地文件上传
     *
     * @param multipartFile 文件
     * @return 返回资源路径
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        LocalFileConfigEntity localFileConfig = configCacheService.getLocalFileConfig();
        String uploadPath = localFileConfig.getUploadPath();
        String originalFileName = multipartFile.getOriginalFilename();

        try {
            // 获取原始文件名并提取后缀
            if (originalFileName == null) {
                throw new FileException(ResponseCode.FileNameIsNull);
            }
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            // 生成年月格式的目录
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
            String yearMonthDir = yearMonthFormat.format(new Date());

            // 生成新的文件名
            String uuidFileName = String.format("%s%s",
                    System.currentTimeMillis(),
                    UUID.randomUUID().toString().substring(0, 8));

            // 只保留小写字母和数字
            Pattern pattern = Pattern.compile("[^a-z0-9]");
            uuidFileName = pattern.matcher(uuidFileName).replaceAll("");

            // 设置目标路径，包含年月目录
            Path path = Paths.get(uploadPath + File.separator + yearMonthDir + File.separator + uuidFileName + fileExtension);
            // 创建目录，如果不存在则创建
            Files.createDirectories(path.getParent());
            // 将文件保存到目标路径
            multipartFile.transferTo(path.toFile());

            return Constants.RESOURCE_PREFIX + "/" + yearMonthDir + "/" + uuidFileName + fileExtension;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }
}
