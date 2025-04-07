package com.your.

package.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS对象存储工具类
 */
@Component
public class AliyunOssUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.url}")
    private String url;

    /**
     * 获取OSS客户端对象
     */
    private OSS initOSSClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 上传文件（MultipartFile类型）
     *
     * @param file    文件
     * @param fileDir 文件存储路径
     * @return 上传后的文件访问路径
     */
    public String uploadFile(MultipartFile file, String fileDir) {
        OSS ossClient = null;
        try {
            ossClient = initOSSClient();

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

            ossClient.putObject(bucketName, fileKey, file.getInputStream(), objectMetadata);

            // 返回文件访问路径
            return url + "/" + fileKey;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到阿里云失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
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
        OSS ossClient = null;
        try {
            ossClient = initOSSClient();

            // 生成随机文件名
            String originalFilename = file.getName();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 组合文件路径
            String fileKey = fileDir + "/" + fileName;

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileKey, file);
            ossClient.putObject(putObjectRequest);

            // 返回文件访问路径
            return url + "/" + fileKey;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到阿里云失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
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
        OSS ossClient = null;
        try {
            ossClient = initOSSClient();

            // 生成随机文件名
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            String randomFileName = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 组合文件路径
            String fileKey = fileDir + "/" + randomFileName;

            // 上传文件
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileSize);
            objectMetadata.setContentType(contentType);

            ossClient.putObject(bucketName, fileKey, inputStream, objectMetadata);

            // 返回文件访问路径
            return url + "/" + fileKey;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到阿里云失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
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
        OSS ossClient = null;
        try {
            ossClient = initOSSClient();

            // 从URL中提取文件Key
            String fileKey = fileUrl.replace(url + "/", "");

            // 删除文件
            ossClient.deleteObject(bucketName, fileKey);
        } catch (Exception e) {
            throw new RuntimeException("删除阿里云文件失败", e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
