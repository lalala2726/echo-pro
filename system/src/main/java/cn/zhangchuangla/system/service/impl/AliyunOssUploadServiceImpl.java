package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.system.model.dto.FileUploadByByteDto;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.service.AliyunOssUploadService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @author zhangchuang
 * Created on 2025/3/22 20:27
 */
@Slf4j
@Service
public class AliyunOssUploadServiceImpl implements AliyunOssUploadService {

    private final ConfigCacheService configCacheService;

    @Autowired
    public AliyunOssUploadServiceImpl(ConfigCacheService configCacheService) {
        this.configCacheService = configCacheService;
    }

    /**
     * 阿里云OSS上传文件
     *
     * @param fileUploadByByteDto 文件信息
     * @return 文件访问URL和文件存储相对路径
     */
    @Override
    public HashMap<String, String> aliyunOssUploadBytes(FileUploadByByteDto fileUploadByByteDto) {
        String contentType = fileUploadByByteDto.getContentType();
        byte[] data = fileUploadByByteDto.getData();
        String fileName = fileUploadByByteDto.getFileName();
        boolean isCompress = fileUploadByByteDto.isCompress();
        AliyunOSSConfigEntity aliyunOSSConfig = configCacheService.getAliyunOSSConfig();

        // 获取OSS配置
        String bucketName = aliyunOSSConfig.getBucketName();
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String fileDomain = aliyunOSSConfig.getFileDomain();
        String bucketPath = aliyunOSSConfig.getBucketPath();

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        HashMap<String, String> result = new HashMap<>();
        try {
            // 生成存储路径
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileNameWithExt = FileUtils.generateUUID();

            // 如果有扩展名，添加扩展名
            if (fileName.contains(".")) {
                fileNameWithExt += fileName.substring(fileName.lastIndexOf("."));
            }

            // 根据isCompress决定使用哪个文件夹
            String folderType = isCompress ? Constants.FILE_PREVIEW_FOLDER : Constants.FILE_ORIGINAL_FOLDER;

            // 拼接完整路径
            String normalizedBucketPath = normalizePath(bucketPath);
            String uploadPath = normalizedBucketPath.isEmpty()
                    ? datePath + "/" + folderType
                    : normalizedBucketPath + "/" + datePath + "/" + folderType;
            String uploadFileName = uploadPath + "/" + fileNameWithExt;

            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setHeader("Content-Disposition", "inline");
            metadata.setContentType(contentType);

            // 添加自定义元数据，标记是否为压缩版本
            metadata.addUserMetadata("isCompressed", String.valueOf(isCompress));

            // 上传文件
            ossClient.putObject(bucketName, uploadFileName, new ByteArrayInputStream(data), metadata);

            // 返回文件URL
            String fileUrl = fileDomain + uploadFileName;
            result.put(Constants.FILE_URL, fileUrl);
            result.put(Constants.RELATIVE_FILE_LOCATION, uploadFileName);
            result.put("isCompressed", String.valueOf(isCompress));
            return result;
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！");
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void deleteFileByFileId(FileManagement fileManagement) {

    }

    /**
     * 规范化路径格式
     * - 去除开头和结尾的斜杠
     * - 处理空值情况
     */
    private String normalizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        // 去除开头和结尾的斜杠
        String normalizedPath = path.trim();
        if (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }

        return normalizedPath;
    }
}
