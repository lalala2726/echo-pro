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
     * 如果检测到是图片类型，会自动调用图片上传方法
     *
     * @param fileTransferDto 文件传输对象
     * @param aliyunOSSConfig 阿里云OSS配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        validateUploadParams(fileTransferDto, aliyunOSSConfig);

        // 如果是图片类型，则调用图片上传方法
        if (isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, aliyunOSSConfig);
        }

        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        // 创建OSS客户端
        OSS ossClient = createOSSClient(aliyunOSSConfig);

        try {
            // 生成存储路径
            String objectName = generateFilePath(fileName);

            // 上传文件
            uploadToOSS(ossClient, aliyunOSSConfig.getBucketName(), objectName, data, fileName);

            // 构建文件URL
            String fileUrl = buildFullUrl(aliyunOSSConfig.getFileDomain(), objectName);

            return createFileTransferResponse(fileUrl, objectName, null, null);
        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
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
        validateUploadParams(fileTransferDto, aliyunOSSConfig);

        // 验证是否为图片类型
        if (!isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getFileName();
        byte[] originalData = fileTransferDto.getBytes();

        // 创建OSS客户端
        OSS ossClient = createOSSClient(aliyunOSSConfig);

        try {
            // 生成存储路径
            String originalObjectName = generateOriginalImagePath(fileName);
            String compressedObjectName = generateCompressedImagePath(fileName);

            // 上传原图
            uploadToOSS(ossClient, aliyunOSSConfig.getBucketName(), originalObjectName, originalData, fileName);
            String originalFileUrl = buildFullUrl(aliyunOSSConfig.getFileDomain(), originalObjectName);

            // 压缩图片并上传
            byte[] compressedData = compressImage(originalData);
            uploadToOSS(ossClient, aliyunOSSConfig.getBucketName(), compressedObjectName, compressedData, fileName);
            String compressedFileUrl = buildFullUrl(aliyunOSSConfig.getFileDomain(), compressedObjectName);

            return createFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName);
        } catch (Exception e) {
            log.warn("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "图片上传失败！" + e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 创建OSS客户端
     */
    private static OSS createOSSClient(AliyunOSSConfigEntity aliyunOSSConfig) {
        if (aliyunOSSConfig == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "阿里云OSS配置不能为空！");
        }

        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();

        return new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
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
