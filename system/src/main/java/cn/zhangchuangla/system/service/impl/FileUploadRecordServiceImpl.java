package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.FileUploadRecordMapper;
import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import cn.zhangchuangla.system.service.FileUploadRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * @author zhangchuang
 */
@Service
@Slf4j
public class FileUploadRecordServiceImpl extends ServiceImpl<FileUploadRecordMapper, FileUploadRecord>
        implements FileUploadRecordService {

    private final ConfigCacheService configCacheService;

    public FileUploadRecordServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }


    /**
     * 保存文件信息到数据库
     * 此方法不处理文件上传，只负责记录已上传文件的信息
     *
     * @param fileUrl     文件访问URL
     * @param file        上传的文件对象
     * @param storageType 存储类型(LOCAL/MINIO/ALIYUN_OSS)
     * @return 是否保存成功
     */
    @Override
    public boolean saveFileInfo(String fileUrl, MultipartFile file, String storageType) {
        if (file == null) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "文件不能为空！");
        }

        if (StringUtils.isBlank(fileUrl)) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "文件URL不能为空！");
        }

        if (StringUtils.isBlank(storageType)) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "存储类型不能为空！");
        }

        try {
            // 获取文件基本信息
            String originalFileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            // 从URL中提取文件扩展名
            String fileExtension = extractExtensionFromUrl(fileUrl);
            if (StringUtils.isBlank(fileExtension) && originalFileName != null) {
                // 如果从URL无法提取到扩展名，则从原始文件名中提取
                fileExtension = FileUtils.getFileExtension(originalFileName);
            }

            // 从URL中提取文件名
            String fileName = extractFilenameFromUrl(fileUrl);

            // 从URL中提取路径
            String filePath = extractPathFromUrl(fileUrl);

            // 计算文件MD5值
            byte[] bytes = file.getBytes();
            String md5 = FileUtils.calculateMD5(bytes);

            // 获取当前用户信息
            Long userId = SecurityUtils.getUserId();
            String userName = SecurityUtils.getUsername();

            // 获取存储桶名称（仅对MinIO和OSS有效）
            String bucketName = null;
            if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getMinioConfig().getBucketName();
            } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getAliyunOSSConfig().getBucketName();
            }

            // 创建文件记录
            FileUploadRecord record = new FileUploadRecord();
            record.setFileName(fileName);
            record.setOriginalFileName(originalFileName);
            record.setFilePath(filePath);
            record.setFileUrl(fileUrl);
            record.setFileSize(fileSize);
            record.setFileType(contentType);
            record.setFileExtension(fileExtension);
            record.setStorageType(storageType);
            record.setBucketName(bucketName);
            record.setMd5(md5);
            record.setUploaderId(userId);
            record.setUploaderName(userName);
            record.setCreateBy(Constants.SYSTEM_CREATE);
            record.setUploadTime(new Date());

            // 保存文件记录到数据库
            boolean save = save(record);

            // 记录上传日志
            log.info("文件信息保存成功 - 文件名: {}, 大小: {}, MD5: {}, 存储类型: {}, 访问URL: {}",
                    originalFileName, fileSize, md5, storageType, fileUrl);

            return save;
        } catch (IOException e) {
            log.error("读取文件内容失败: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "读取文件内容失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("保存文件信息失败: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "保存文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 从URL中提取文件扩展名
     *
     * @param url 文件URL
     * @return 文件扩展名（带点，如 .jpg）
     */
    private String extractExtensionFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }

        // 移除URL参数
        String cleanUrl = url;
        int queryIndex = cleanUrl.indexOf('?');
        if (queryIndex > 0) {
            cleanUrl = cleanUrl.substring(0, queryIndex);
        }

        // 获取最后一个点之后的内容作为扩展名
        int lastDotIndex = cleanUrl.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return cleanUrl.substring(lastDotIndex);
        }

        return "";
    }

    /**
     * 从URL中提取文件名
     *
     * @param url 文件URL
     * @return 文件名
     */
    private String extractFilenameFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }

        // 移除URL参数
        String cleanUrl = url;
        int queryIndex = cleanUrl.indexOf('?');
        if (queryIndex > 0) {
            cleanUrl = cleanUrl.substring(0, queryIndex);
        }

        // 获取最后一个斜杠之后的内容作为文件名
        int lastSlashIndex = cleanUrl.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < cleanUrl.length() - 1) {
            return cleanUrl.substring(lastSlashIndex + 1);
        }

        return cleanUrl;
    }

    /**
     * 从URL中提取文件路径
     *
     * @param url 文件URL
     * @return 文件路径
     */
    private String extractPathFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return "";
        }

        try {
            URL parsedUrl = new URL(url);
            String path = parsedUrl.getPath();

            // 如果路径为空，则返回空字符串
            if (StringUtils.isBlank(path)) {
                return "";
            }

            // 删除开头的斜杠
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            return path;
        } catch (MalformedURLException e) {
            // 如果URL格式不正确，尝试简单提取
            int protocolIndex = url.indexOf("://");
            if (protocolIndex > 0) {
                String remaining = url.substring(protocolIndex + 3);
                int pathStartIndex = remaining.indexOf('/');
                if (pathStartIndex >= 0) {
                    return remaining.substring(pathStartIndex + 1);
                }
            }

            return "";
        }
    }
}




