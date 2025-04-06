package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS存储工具类
 *
 * @author Chuang
 *         <p>
 *         created on 2025/4/3 10:00
 */
@Slf4j
public class AliyunOssUtils extends AbstractStorageUtils {

    /**
     * 上传文件到阿里云OSS
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        if (aliyunOSSConfig == null)
            throw new FileException(ResponseCode.FileUploadFailed, "阿里云OSS配置不能为空！");

        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        // 从配置获取参数
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String bucketName = aliyunOSSConfig.getBucketName();
        String fileDomain = aliyunOSSConfig.getFileDomain();

        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        try {
            // 生成存储路径
            String objectName = generateFilePath(fileName);

            // 上传文件
            uploadToOSS(ossClient, bucketName, objectName, data, fileName);

            // 构建文件URL
            String fileUrl = buildFullUrl(fileDomain, objectName);

            return createFileTransferResponse(fileUrl, objectName, null, null);
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！");
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传图片到阿里云OSS
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @return 增强后的文件传输对象
     */
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        if (aliyunOSSConfig == null)
            throw new FileException(ResponseCode.FileUploadFailed, "阿里云OSS配置不能为空！");

        String fileName = fileTransferDto.getFileName();
        byte[] originalData = fileTransferDto.getBytes();

        // 从配置获取参数
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String bucketName = aliyunOSSConfig.getBucketName();
        String fileDomain = aliyunOSSConfig.getFileDomain();

        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        try {
            // 生成存储路径
            String originalObjectName = generateOriginalImagePath(fileName);
            String compressedObjectName = generateCompressedImagePath(fileName);

            // 上传原图
            uploadToOSS(ossClient, bucketName, originalObjectName, originalData, fileName);
            String originalFileUrl = buildFullUrl(fileDomain, originalObjectName);

            // 压缩图片并上传
            byte[] compressedData = compressImage(originalData);
            uploadToOSS(ossClient, bucketName, compressedObjectName, compressedData, fileName);
            String compressedFileUrl = buildFullUrl(fileDomain, compressedObjectName);

            return createFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName);
        } catch (Exception e) {
            log.warn("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！");
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 上传文件到OSS
     */
    private static void uploadToOSS(OSS ossClient, String bucketName, String objectName,
                                    byte[] data, String fileName) {
        // 设置元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setHeader("Content-Disposition", "inline");
        metadata.setContentType(FileUtils.generateFileContentType(fileName));

        // 上传文件
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data), metadata);
    }
}
