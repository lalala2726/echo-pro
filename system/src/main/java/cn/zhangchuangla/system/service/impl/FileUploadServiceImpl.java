package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.entity.file.FileInfo;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.result.FileUploadResult;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.ImageUtils;
import cn.zhangchuangla.system.model.dto.FileUploadByByteDto;
import cn.zhangchuangla.system.model.dto.SaveFileInfoDto;
import cn.zhangchuangla.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

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
     * 重写阿里云OSS文件上传方法
     *
     * @param file 要上传的文件，类型为MultipartFile，适用于Web表单文件上传
     * @return 返回上传成功的文件URL
     * @throws FileException 如果文件上传失败，抛出文件异常
     */
    @Override
    public String AliyunOssFileUpload(MultipartFile file) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);
            FileUploadByByteDto fileUploadByByteDto = FileUploadByByteDto.builder()
                    .fileName(fileInfo.getOriginalFilename())
                    .data(fileInfo.getData()).build();
            return aliyunOssUploadBytes
                    .aliyunOssUploadBytes(fileUploadByByteDto)
                    .get(Constants.FILE_URL);

        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }


    /**
     * 重写Minio文件上传方法
     *
     * @param file 要上传的文件，类型为MultipartFile，适用于Web表单文件上传
     * @return 返回上传成功的文件URL
     * @throws FileException 如果文件上传失败，抛出文件异常
     */
    @Override
    public String MinioFileUpload(MultipartFile file) {
        try {
            // 将MultipartFile转换为FileInfo，便于后续处理
            FileInfo fileInfo = FileInfo.fromMultipartFile(file);

            // 创建FileUploadByByteDto对象，用于文件上传
            FileUploadByByteDto fileUploadByByteDto = FileUploadByByteDto.builder()
                    .fileName(fileInfo.getOriginalFilename())
                    .data(fileInfo.getData()).build();

            // 调用minioFileUploadService的minioUploadBytes方法上传文件，并获取文件URL
            return minioFileUploadService
                    .minioUploadBytes(fileUploadByByteDto)
                    .get(Constants.FILE_URL);

        } catch (IOException e) {
            // 记录文件上传失败的错误信息
            log.error("文件上传失败: {}", e.getMessage(), e);
            // 当文件上传失败时，抛出自定义的文件异常
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }


    /**
     * 重写本地文件上传方法
     *
     * @param multipartFile 用户上传的文件，封装在MultipartFile对象中
     * @return 返回文件上传后的URL字符串
     * @throws FileException 如果文件上传失败，抛出文件异常
     */
    @Override
    public String localFileUpload(MultipartFile multipartFile) {
        try {
            // 将MultipartFile转换为FileInfo
            FileInfo fileInfo = FileInfo.fromMultipartFile(multipartFile);
            // 创建FileUploadByByteDto对象，用于文件上传
            FileUploadByByteDto fileUploadByByteDto = FileUploadByByteDto.builder()
                    .fileName(fileInfo.getOriginalFilename())
                    .data(fileInfo.getData()).build();

            // 调用本地文件上传服务，上传文件，并获取上传后的文件URL
            return localFileUploadService
                    .localUploadBytes(fileUploadByByteDto)
                    .get(Constants.FILE_URL);
        } catch (IOException e) {
            // 记录文件上传失败的错误信息
            log.error("文件上传失败: {}", e.getMessage(), e);
            // 抛出文件上传失败异常
            throw new FileException(ResponseCode.FileUploadFailed);
        }
    }


    /**
     * 上传文件并进行图像处理
     *
     * @param file        要上传的文件，不能为空
     * @param storageType 存储类型，指定文件存储的位置
     * @return FileUploadResult对象，包含文件上传的结果信息
     * @throws ServiceException 如果文件为空或文件处理过程中发生异常
     */
    @Override
    public FileUploadResult uploadWithImageProcess(MultipartFile file, String storageType) {
        // 检查上传的文件是否为空
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
            HashMap<String, String> originalResult = uploadByteArray(
                    fileInfo.getData(),
                    fileInfo.getOriginalFilename(),
                    fileInfo.getContentType(),
                    storageType,
                    false
            );
            // 设置原始文件URL
            result.setOriginalUrl(originalResult.get(Constants.FILE_URL));

            String previewRelativeFileLocation = null;
            // 如果是图片文件，进行压缩处理
            if (isImage) {
                // 压缩图片
                byte[] compressedBytes = ImageUtils.compressImage(
                        fileInfo.getData(), DEFAULT_MAX_WIDTH, DEFAULT_MAX_HEIGHT, DEFAULT_QUALITY);

                // 设置压缩文件大小
                result.setCompressedSize(compressedBytes.length);

                // 生成压缩文件名
                String compressedFileName = FileUtils.generateCompressedFileName(fileInfo.getOriginalFilename());

                // 直接上传压缩后的字节数组
                HashMap<String, String> previewResult = uploadByteArray(
                        compressedBytes,
                        compressedFileName,
                        fileInfo.getContentType(),
                        storageType,
                        true
                );
                previewRelativeFileLocation = previewResult.get(Constants.RELATIVE_FILE_LOCATION);
                result.setCompressedUrl(previewResult.get(Constants.FILE_URL));
            }

            // 保存文件信息到数据库中
            if (fileManagementService != null) {
                // 构建要保存的文件信息DTO
                SaveFileInfoDto saveFileInfoDto = SaveFileInfoDto.builder()
                        .fileUrl(originalResult.get(Constants.FILE_URL))
                        .compressedUrl(result.getCompressedUrl())
                        .fileInfo(fileInfo)
                        .storageType(storageType)
                        .originalRelativeFileLocation(originalResult.get(Constants.RELATIVE_FILE_LOCATION))
                        .previewRelativeFileLocation(previewRelativeFileLocation)
                        .build();
                // 保存文件记录
                fileManagementService.saveFileRecord(saveFileInfoDto);
            }

            // 返回文件上传结果
            return result;
        } catch (IOException e) {
            // 日志记录文件处理异常
            log.error("文件处理异常: {}", e.getMessage(), e);
            // 抛出服务异常，指示系统错误
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "文件处理失败: " + e.getMessage());
        }
    }


    /**
     * 上传字节数组
     *
     * @param data        字节数组
     * @param fileName    文件名
     * @param storageType 存储类型
     */
    private HashMap<String, String> uploadByteArray(byte[] data, String fileName, String contentType, String storageType, boolean isCompress) throws IOException {
        FileUploadByByteDto fileUploadByByteDto = FileUploadByByteDto.builder()
                .data(data)
                .fileName(fileName)
                .isCompress(isCompress)
                .contentType(contentType)
                .build();
        if (Constants.LOCAL_FILE_UPLOAD.equals(storageType)) {
            return localFileUploadService.localUploadBytes(fileUploadByByteDto);
        } else if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
            return minioFileUploadService.minioUploadBytes(fileUploadByByteDto);
        } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
            return aliyunOssUploadBytes.aliyunOssUploadBytes(fileUploadByByteDto);
        } else {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "不支持的存储类型: " + storageType);
        }
    }


}