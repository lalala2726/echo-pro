package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储工具类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
public class LocalStorageUtils extends AbstractStorageUtils {

    /**
     * 上传文件到本地存储
     *
     * @param fileTransferDto 文件传输对象
     * @param uploadPath      上传根路径
     * @param fileDomain      文件访问域名
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        // 生成存储路径
        String relativePath = generateFilePath(fileName);

        // 保存文件
        saveFile(data, uploadPath, relativePath);

        // 构建URL
        String fileUrl = buildCompleteUrl(relativePath, fileDomain);

        return createFileTransferResponse(fileUrl, relativePath, null, null);
    }

    /**
     * 上传图片到本地存储
     * 同时上传原图和压缩图，分别保存在不同目录
     *
     * @param fileTransferDto 文件传输对象
     * @param uploadPath      上传根路径
     * @param fileDomain      文件访问域名
     * @return 增强后的文件传输对象
     */
    public static FileTransferDto imageUpload(FileTransferDto fileTransferDto, String uploadPath, String fileDomain) {
        String fileName = fileTransferDto.getFileName();
        byte[] originalData = fileTransferDto.getBytes();

        try {
            // 生成存储路径
            String originalRelativePath = generateOriginalImagePath(fileName);
            String compressedRelativePath = generateCompressedImagePath(fileName);

            // 保存原图
            saveFile(originalData, uploadPath, originalRelativePath);
            String originalUrl = buildCompleteUrl(originalRelativePath, fileDomain);

            // 压缩并保存
            byte[] compressedData = compressImage(originalData);
            saveFile(compressedData, uploadPath, compressedRelativePath);
            String compressedUrl = buildCompleteUrl(compressedRelativePath, fileDomain);

            return createFileTransferResponse(
                    originalUrl, originalRelativePath,
                    compressedUrl, compressedRelativePath);

        } catch (Exception e) {
            log.error("图片处理及保存失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 保存文件到本地
     */
    private static void saveFile(byte[] data, String uploadPath, String relativePath) {
        String filePath = uploadPath + File.separator + relativePath;
        Path path = Paths.get(filePath);
        Path directory = path.getParent();
        try {
            // 创建目录
            if (directory != null) {
                Files.createDirectories(directory);
            }

            // 写入文件
            Files.write(path, data);
        } catch (IOException e) {
            log.error("文件写入失败", e);
            throw new FileException(ResponseCode.FILE_OPERATION_FAILED, "文件写入失败！");
        }
    }

    /**
     * 构建完整的URL
     */
    private static String buildCompleteUrl(String relativePath, String domain) {
        if (StringUtils.isEmpty(domain)) {
            return FileUtils.buildFinalPath(Constants.RESOURCE_PREFIX, relativePath);
        }
        return buildFullUrl(domain, relativePath);
    }
}
