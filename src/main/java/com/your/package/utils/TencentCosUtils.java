package com.your.

package.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 腾讯云COS对象存储工具类
 */
@Component
public class TencentCosUtils {

    @Value("${tencent.cos.secretId}")
    private String secretId;

    @Value("${tencent.cos.secretKey}")
    private String secretKey;

    @Value("${tencent.cos.region}")
    private String region;

    @Value("${tencent.cos.bucketName}")
    private String bucketName;

    @Value("${tencent.cos.url}")
    private String url;

    /**
     * 获取COSClient对象
     */
    private COSClient initCOSClient() {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        return new COSClient(cred, clientConfig);
    }

    /**
     * 上传文件（MultipartFile类型）
     *
     * @param file    文件
     * @param fileDir 文件存储路径
     * @return 上传后的文件访问路径
     */
    public String uploadFile(MultipartFile file, String fileDir) {
        COSClient cosClient = null;
        try {
            cosClient = initCOSClient();

            // 生成随机文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 组合文件路径
            String fileKey = fileDir + "/" + fileName;

            // 上传文件
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file.getInputStream(), objectMetadata);
            cosClient.putObject(putObjectRequest);

            // 返回文件访问路径
            return url + "/" + fileKey;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到腾讯云失败", e);
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

    /**
     * 上传文件（File类型）
     *
     * @param file    文件
     * @param fileDir 文件存储路径
     * @return 上传后的文件访问路径
     */
    public String uploadFile(File file, String fileDir) {
        COSClient cosClient = null;
        try {
            cosClient = initCOSClient();

            // 生成随机文件名
            String originalFilename = file.getName();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 组合文件路径
            String fileKey = fileDir + "/" + fileName;

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file);
            cosClient.putObject(putObjectRequest);

            // 返回文件访问路径
            return url + "/" + fileKey;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到腾讯云失败", e);
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

    /**
     * 上传文件（InputStream类型）
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param fileDir     文件存储路径
     * @param contentType 文件类型
     * @param fileSize    文件大小
     * @return 上传后的文件访问路径
     */
    public String uploadFile(InputStream inputStream, String fileName, String fileDir, String contentType, long fileSize) {
        COSClient cosClient = null;
        try {
            cosClient = initCOSClient();

            // 生成随机文件名
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String randomFileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 组合文件路径
            String fileKey = fileDir + "/" + randomFileName;

            // 上传文件
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileSize);
            objectMetadata.setContentType(contentType);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, inputStream, objectMetadata);
            cosClient.putObject(putObjectRequest);

            // 返回文件访问路径
            return url + "/" + fileKey;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到腾讯云失败", e);
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }

    /**
     * 上传并返回URL（MultipartFile类型）
     *
     * @param file    文件
     * @param fileDir 文件存储路径
     * @return 上传后的文件访问路径
     */
    public String uploadAndReturnUrl(MultipartFile file, String fileDir) {
        return uploadFile(file, fileDir);
    }

    /**
     * 上传并返回URL（File类型）
     *
     * @param file    文件
     * @param fileDir 文件存储路径
     * @return 上传后的文件访问路径
     */
    public String uploadAndReturnUrl(File file, String fileDir) {
        return uploadFile(file, fileDir);
    }

    /**
     * 上传并返回URL（InputStream类型）
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param fileDir     文件存储路径
     * @param contentType 文件类型
     * @param fileSize    文件大小
     * @return 上传后的文件访问路径
     */
    public String uploadAndReturnUrl(InputStream inputStream, String fileName, String fileDir, String contentType, long fileSize) {
        return uploadFile(inputStream, fileName, fileDir, contentType, fileSize);
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        COSClient cosClient = null;
        try {
            cosClient = initCOSClient();

            // 从URL中提取文件Key
            String fileKey = fileUrl.replace(url + "/", "");

            // 删除文件
            cosClient.deleteObject(bucketName, fileKey);
        } catch (Exception e) {
            throw new RuntimeException("删除腾讯云文件失败", e);
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }
}
