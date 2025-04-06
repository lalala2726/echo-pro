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
 * <p>
 * created on 2025/4/3 10:00
 */
@Slf4j
public class AliyunOssUtils {


    /**
     * 上传文件到阿里云OSS
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, AliyunOSSConfigEntity aliyunOSSConfig) {
        if (aliyunOSSConfig == null) throw new FileException(ResponseCode.FileUploadFailed, "阿里云OSS配置不能为空！");
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();
        String fileDomain = aliyunOSSConfig.getFileDomain();
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String bucketName = aliyunOSSConfig.getBucketName();
        // 使用FileUtils获取内容类型
        String contentType = FileUtils.generateFileContentType(fileName);

        // 默认不压缩
        boolean isCompress = false;

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        try {
            // 生成文件名
            String fileNameWithoutExt = FileUtils.generateFileName();
            String fileExtension = FileUtils.getFileExtension(fileName);

            // 生成日期目录
            String datePath = FileUtils.generateYearMonthDir();

            String uploadFileName = FileUtils.buildFinalPath(fileNameWithoutExt + fileExtension);

            // 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setHeader("Content-Disposition", "inline");
            metadata.setContentType(contentType);


            // 上传文件
            ossClient.putObject(bucketName, uploadFileName, new ByteArrayInputStream(data), metadata);

            // 返回文件URL
            String fileUrl = fileDomain + uploadFileName;

            // 构建并返回文件传输对象
            return FileTransferDto.builder()
                    .fileUrl(fileUrl)
                    .relativePath(uploadFileName)
                    .build();

        } catch (Exception e) {
            log.warn("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed, "文件上传失败！");
        } finally {
            ossClient.shutdown();
        }
    }
}
