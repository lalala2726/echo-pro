package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.OSSConfig;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.system.service.AliyunOssFileService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static cn.dev33.satoken.SaManager.log;

@Service
public class AliyunOssFileServiceImpl implements AliyunOssFileService {

    private final OSSConfig ossConfig;

    public AliyunOssFileServiceImpl(OSSConfig ossConfig) {
        this.ossConfig = ossConfig;
    }


    /**
     * 阿里云OSS文件上传
     */
    @Override
    public String upload(MultipartFile file) {
        // 验证文件是否有效
        if (!validateFile(file)) {
            log.error("文件上传失败：文件为空或无效");
            throw new FileException(ResponseCode.FileNameIsNull);
        }

        // 获取 OSS 配置
        String bucketName = ossConfig.getBucketName();
        String endPoint = ossConfig.getEndPoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        String fileDomain = ossConfig.getFileDomain();

        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        // 生成存储路径
        String folder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = generateUUID() + getFileExtension(file.getOriginalFilename());
        String uploadFileName = folder + "/" + fileName;

        // 配置文件元数据（确保浏览器可直接预览）
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setHeader("Content-Disposition", "inline"); // 预览模式
        metadata.setContentType(determineContentType(file));

        // 执行上传
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectResult result = ossClient.putObject(bucketName, uploadFileName, inputStream, metadata);
            return result != null ? fileDomain + uploadFileName : null;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return null;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 校验文件有效性
     */
    private boolean validateFile(MultipartFile file) {
        return file != null && !file.isEmpty() && file.getOriginalFilename() != null;
    }

    /**
     * 获取文件扩展名（带点）
     */
    private String getFileExtension(String fileName) {
        return (fileName != null && fileName.contains(".")) ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    /**
     * 根据文件类型返回合适的 Content-Type
     */
    private String determineContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.equals("application/octet-stream")) {
            String ext = getFileExtension(file.getOriginalFilename()).toLowerCase();
            return switch (ext) {
                case ".jpg", ".jpeg" -> "image/jpeg";
                case ".png" -> "image/png";
                case ".gif" -> "image/gif";
                case ".pdf" -> "application/pdf";
                case ".mp4" -> "video/mp4";
                default -> "application/octet-stream";
            };
        }
        return contentType;
    }


    /**
     * 获取随机字符串
     */
    private String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32);
    }
}
