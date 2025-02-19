package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.config.AliyunOSSConfig;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.system.service.AliyunOssFileService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@Slf4j
public class AliyunOssFileServiceImpl implements AliyunOssFileService {

    private final AliyunOSSConfig aliyunOssConfig;

    public AliyunOssFileServiceImpl(AliyunOSSConfig aliyunOssConfig) {
        this.aliyunOssConfig = aliyunOssConfig;
    }


    /**
     * 阿里云OSS文件上传
     */
    @Override
    public String upload(MultipartFile file) {
        // 验证文件是否有效
        if (!FileUtils.validateFile(file)) {
            log.error("文件上传失败：文件为空或无效");
            throw new FileException(ResponseCode.FileNameIsNull);
        }

        // 获取 OSS 配置
        String bucketName = aliyunOssConfig.getBucketName();
        String endPoint = aliyunOssConfig.getEndPoint();
        String accessKeyId = aliyunOssConfig.getAccessKeyId();
        String accessKeySecret = aliyunOssConfig.getAccessKeySecret();
        String fileDomain = aliyunOssConfig.getFileDomain();

        // 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        // 生成存储路径
        String folder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fileName = FileUtils.generateUUID() + FileUtils.getFileExtension(file.getOriginalFilename());
        String uploadFileName = folder + "/" + fileName;

        // 配置文件元数据（确保浏览器可直接预览）
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setHeader("Content-Disposition", "inline"); // 预览模式
        metadata.setContentType(FileUtils.determineContentType(file));

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


}
