package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.ProfileUtils;
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


/**
 * @author zhangchuang
 * Created on 2025/2/17 15:41
 */
@Slf4j
@Service
public class LocalFileServiceImpl implements LocalFileService {

    private final AppConfig appConfig;

    public LocalFileServiceImpl(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    /**
     * 本地文件上传
     *
     * @param multipartFile 文件
     * @return 返回资源路径
     */
    @Override
    public String uploadFile(MultipartFile multipartFile) {
        if (!ProfileUtils.checkLoadFileUploadProperties(appConfig)) {
            throw new FileException("本地上传路径未配置");
        }
        String uploadPath = appConfig.getUploadPath();
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

            // 生成新的文件名，包含目录结构
            String uuidFileName = String.format("%s/%s_%s_%s",
                    yearMonthDir,
                    System.currentTimeMillis(),
                    UUID.randomUUID().toString().substring(0, 8),
                    fileExtension);

            // 设置目标路径
            Path path = Paths.get(uploadPath + File.separator + uuidFileName);
            // 创建目录，如果不存在则创建
            Files.createDirectories(path.getParent());
            // 将文件保存到目标路径
            multipartFile.transferTo(path.toFile());

            return "/static/" + uuidFileName;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }
}
