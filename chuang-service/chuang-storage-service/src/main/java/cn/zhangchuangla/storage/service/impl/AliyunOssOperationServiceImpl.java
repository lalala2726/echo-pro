package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.storage.entity.FileTransferDto;
import cn.zhangchuangla.storage.service.AliyunOssOperationService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * 阿里云OSS 操作服务实现类
 *
 * @author Chuang
 * <p>
 * created on 2025/4/2 20:03
 */
@Service
@Slf4j
public class AliyunOssOperationServiceImpl implements AliyunOssOperationService {


    private final SysFileConfigLoader sysFileConfigLoader;

    @Autowired
    public AliyunOssOperationServiceImpl(SysFileConfigLoader sysFileConfigLoader) {
        this.sysFileConfigLoader = sysFileConfigLoader;
    }

    /**
     * 文件上传
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输对象
     */
    @Override
    public FileTransferDto save(FileTransferDto fileTransferDto) {
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        // 使用FileUtils获取内容类型
        String contentType = FileUtils.generateFileContentType(fileName);

        // 默认不压缩
        boolean isCompress = false;

        AliyunOSSConfigEntity aliyunOSSConfig = sysFileConfigLoader.getAliyunOSSConfig();

        if (aliyunOSSConfig == null) {
            throw new FileException(ResponseCode.FileUploadFailed, "阿里云OSS配置为空！请你检查配置文件");
        }

        // 获取OSS配置
        String bucketName = aliyunOSSConfig.getBucketName();
        String endPoint = aliyunOSSConfig.getEndpoint();
        String accessKeyId = aliyunOSSConfig.getAccessKeyId();
        String accessKeySecret = aliyunOSSConfig.getAccessKeySecret();
        String fileDomain = aliyunOSSConfig.getFileDomain();
        String bucketPath = aliyunOSSConfig.getBucketPath();

        // 创建OSS客户端
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);

        try {
            // 生成文件名
            String fileNameWithoutExt = FileUtils.generateFileName();
            String fileExtension = FileUtils.getFileExtension(fileName);

            // 根据isCompress决定使用哪个文件夹
            String folderType = Constants.FILE_ORIGINAL_FOLDER;

            // 生成日期目录
            String datePath = FileUtils.generateYearMonthDir();

            // 构建上传路径
            String uploadPath = FileUtils.buildFinalPath(
                    StringUtils.trim(bucketPath),
                    datePath,
                    folderType);

            String uploadFileName = FileUtils.buildFinalPath(uploadPath, fileNameWithoutExt + fileExtension);

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
