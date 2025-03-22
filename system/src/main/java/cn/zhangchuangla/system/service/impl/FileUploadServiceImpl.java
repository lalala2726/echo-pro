package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.entity.file.FileInfo;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.result.FileUploadResult;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.ImageUtils;
import cn.zhangchuangla.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传服务实现类
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {


    // 图片压缩配置
    private static final int DEFAULT_MAX_WIDTH = 800;
    private static final int DEFAULT_MAX_HEIGHT = 800;
    private static final float DEFAULT_QUALITY = 0.75f;
    private final FileManagementService fileManagementService;
    private final AliyunOssUploadService aliyunOssUploadBytes;
    private final LocalFileUploadService localFileUploadService;
    private final MinioFileUploadService minioFileUploadService;

    @Autowired
    public FileUploadServiceImpl(FileManagementService fileManagementService, AliyunOssUploadService aliyunOssUploadBytes, LocalFileUploadService localFileUploadService, MinioFileUploadService minioFileUploadService) {
        this.fileManagementService = fileManagementService;
        this.aliyunOssUploadBytes = aliyunOssUploadBytes;
        this.localFileUploadService = localFileUploadService;
        this.minioFileUploadService = minioFileUploadService;
    }



    /**
     * 文件上传到阿里云OSS
     *
     * @param file MultipartFile
     * @return 文件访问路径
     */
    @Override
    public String AliyunOssFileUpload(MultipartFile file) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);
            return aliyunOssUploadBytes.aliyunOssUploadBytes(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }

    /**
     * 文件上传到Minio
     *
     * @param file 文件
     * @return 文件访问路径
     */
    @Override
    public String MinioFileUpload(MultipartFile file) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);
            return minioFileUploadService.minioUploadBytes(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }

    /**
     * 本地文件上传
     *
     * @param multipartFile 文件
     * @return 文件访问路径
     */
    @Override
    public String localFileUpload(MultipartFile multipartFile) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(multipartFile);
            return localFileUploadService.localUploadBytes(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename()
            );
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }

    /**
     * 智能上传，如果是图片等资源将会返回两个两个URL，分别是原始URL和压缩URL
     *
     * @param file        上传的文件
     * @param storageType 存储类型 (LOCAL/MINIO/ALIYUN_OSS)
     * @return 返回文件上传结果
     */
    @Override
    public FileUploadResult uploadWithImageProcess(MultipartFile file, String storageType) {
        if (file == null || file.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "上传文件不能为空");
        }

        try {
            // 将MultipartFile转换为FileInfo，一次性读取所有信息
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);

            // 构建结果对象
            FileUploadResult result = FileUploadResult.builder()
                    .fileName(fileInfo.getOriginalFilename())
                    .fileType(fileInfo.getContentType())
                    .originalSize(fileInfo.getSize())
                    .build();

            // 检查是否为图片
            boolean isImage = ImageUtils.isImage(fileInfo.getContentType());
            result.setImage(isImage);

            // 上传原始文件
            String originalUrl = uploadByteArray(
                    fileInfo.getContent(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType(),
                    storageType
            );
            result.setOriginalUrl(originalUrl);

            // 如果是图片文件，进行压缩处理
            if (isImage) {
                // 压缩图片
                byte[] compressedBytes = ImageUtils.compressImage(
                        fileInfo.getContent(), DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT, DEFAULT_QUALITY);

                // 设置压缩文件大小
                result.setCompressedSize(compressedBytes.length);

                // 生成压缩文件名
                String compressedFileName = FileUtils.generateCompressedFileName(fileInfo.getOriginalFilename());

                // 直接上传压缩后的字节数组
                String compressedUrl = uploadByteArray(
                        compressedBytes,
                        compressedFileName,
                        fileInfo.getContentType(),
                        storageType
                );

                result.setCompressedUrl(compressedUrl);

            }

            // 保存原始文件记录
            if (fileManagementService != null) {
                fileManagementService.saveFileRecord(
                        originalUrl,
                        result.getCompressedUrl(),
                        fileInfo,
                        storageType
                );
            }

            return result;
        } catch (IOException e) {
            log.error("文件处理异常: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "文件处理失败: " + e.getMessage());
        }
    }


    /**
     * 上传字节数组
     *
     * @param data        字节数组
     * @param fileName    文件名
     * @param contentType 文件类型
     * @param storageType 存储类型
     */
    private String uploadByteArray(byte[] data, String fileName, String contentType, String storageType) throws IOException {
        if (Constants.LOCAL_FILE_UPLOAD.equals(storageType)) {
            return localFileUploadService.localUploadBytes(data, fileName);
        } else if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
            return minioFileUploadService.minioUploadBytes(data, fileName, contentType);
        } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
            return aliyunOssUploadBytes.aliyunOssUploadBytes(data, fileName, contentType);
        } else {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "不支持的存储类型: " + storageType);
        }
    }


}