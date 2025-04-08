package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;

/**
 * 腾讯云COS存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
public class TencentCOSUtils extends AbstractStorageUtils {


    /**
     * 上传文件到腾讯云COS
     * 如果检测到是图片类型，会自动调用图片上传方法
     *
     * @param fileTransferDto        文件传输对象
     * @param tencentCOSConfigEntity 腾讯云COS配置
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto,
                                             TencentCOSConfigEntity tencentCOSConfigEntity) {
        validateUploadParams(fileTransferDto, tencentCOSConfigEntity);

        // 填充文件基础信息
        fillFileTransferInfo(fileTransferDto, StorageConstants.TENCENT_COS, tencentCOSConfigEntity.getBucketName());

        // 如果是图片类型，则调用图片上传方法
        if (isImage(fileTransferDto)) {
            return imageUpload(fileTransferDto, tencentCOSConfigEntity);
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] data = fileTransferDto.getBytes();

        // 创建COS客户端
        COSClient cosClient = createCOSClient(tencentCOSConfigEntity);
        try {
            ensureBucketExists(cosClient, tencentCOSConfigEntity.getBucketName());

            // 生成存储路径
            String objectName = generateFilePath(fileName);

            // 上传文件
            uploadToCOS(cosClient, tencentCOSConfigEntity.getBucketName(), objectName, data, fileName);

            // 构建文件URL
            String fileUrl = buildFullUrl(tencentCOSConfigEntity.getFileDomain(), objectName);

            return createEnhancedFileTransferResponse(fileUrl, objectName, null, null, fileTransferDto);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 上传图片到腾讯云COS
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto        文件传输对象
     * @param tencentCOSConfigEntity 腾讯云COS配置
     * @return 增强后的文件传输对象
     */
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto,
                                              TencentCOSConfigEntity tencentCOSConfigEntity) {
        validateUploadParams(fileTransferDto, tencentCOSConfigEntity);

        // 填充文件基础信息
        fileTransferDto = fillFileTransferInfo(fileTransferDto, StorageConstants.TENCENT_COS, tencentCOSConfigEntity.getBucketName());

        // 验证是否为图片类型
        if (!isImage(fileTransferDto)) {
            throw new FileException(ResponseCode.FileUploadFailed, "非图片类型文件不能使用图片上传接口！");
        }

        String fileName = fileTransferDto.getOriginalName();
        byte[] originalData = fileTransferDto.getBytes();

        // 创建COS客户端
        COSClient cosClient = createCOSClient(tencentCOSConfigEntity);
        try {
            ensureBucketExists(cosClient, tencentCOSConfigEntity.getBucketName());

            // 生成存储路径
            String originalObjectName = generateOriginalImagePath(fileName);
            String compressedObjectName = generateCompressedImagePath(fileName);

            // 上传原图
            uploadToCOS(cosClient, tencentCOSConfigEntity.getBucketName(), originalObjectName, originalData, fileName);
            String originalFileUrl = buildFullUrl(tencentCOSConfigEntity.getFileDomain(), originalObjectName);

            // 压缩并上传缩略图
            byte[] compressedData = compressImage(originalData);
            uploadToCOS(cosClient, tencentCOSConfigEntity.getBucketName(), compressedObjectName, compressedData,
                    fileName);
            String compressedFileUrl = buildFullUrl(tencentCOSConfigEntity.getFileDomain(), compressedObjectName);

            return createEnhancedFileTransferResponse(
                    originalFileUrl, originalObjectName,
                    compressedFileUrl, compressedObjectName,
                    fileTransferDto);
        } catch (Exception e) {
            log.error("图片上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "图片上传失败！" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    /**
     * 创建COS客户端
     */
    private static COSClient createCOSClient(TencentCOSConfigEntity tencentCOSConfigEntity) {
        if (tencentCOSConfigEntity == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "腾讯云COS配置不能为空！");
        }

        COSCredentials cred = new BasicCOSCredentials(
                tencentCOSConfigEntity.getSecretId(),
                tencentCOSConfigEntity.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(tencentCOSConfigEntity.getRegion()));
        return new COSClient(cred, clientConfig);
    }

    /**
     * 确保Bucket存在
     */
    private static void ensureBucketExists(COSClient cosClient, String bucketName) {
        if (!cosClient.doesBucketExist(bucketName)) {
            log.warn("Bucket {} 不存在，系统将尝试创建", bucketName);
            cosClient.createBucket(bucketName);
        }
    }

    /**
     * 上传文件到COS
     */
    private static void uploadToCOS(COSClient cosClient, String bucketName, String objectName,
                                    byte[] data, String fileName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType(FileUtils.generateFileContentType(fileName));

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName, objectName, new ByteArrayInputStream(data), metadata);
        cosClient.putObject(putObjectRequest);
    }
}
