package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import cn.zhangchuangla.storage.service.AliyunOssOperationService;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
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

    @Override
    public FileTransferDto save(FileTransferDto fileTransferDto) {
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        AliyunOSSConfigEntity ossConfig = sysFileConfigLoader.getAliyunOSSConfig();
        if (ossConfig == null) {
            throw new ProfileException("阿里云OSS配置文件为空！请检查配置文件是否存在？");
        }

        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        String bucketName = ossConfig.getBucketName();
        String fileDomain = ossConfig.getFileDomain();

        // 创建OSS客户端
        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 检查存储桶是否存在，不存在则创建
            if (!ossClient.doesBucketExist(bucketName)) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                // 设置存储桶权限为公共读，私有写
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }

            // 生成存储路径
            String datePath = FileUtils.generateYearMonthDir();
            // 获取文件扩展名
            String fileExtension = FileUtils.getFileExtension(fileName);
            // 生成唯一文件名
            String uuid = FileUtils.generateUUID();
            // 组合最终路径: 日期/uuid+扩展名
            String objectName = FileUtils.buildFinalPath(datePath, uuid + fileExtension);

            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            // 设置内容类型
            metadata.setContentType(FileUtils.generateFileContentType(fileName));

            // 上传文件
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(data), metadata);

            // 构建文件URL
            String fileUrl = FileUtils.buildFinalPath(fileDomain, objectName);

            // 返回文件信息
            return FileTransferDto.builder()
                    .fileUrl(fileUrl)
                    .relativePath(objectName)
                    .build();
        } catch (OSSException oe) {
            log.error("OSS服务异常: {}", oe.getErrorMessage(), oe);
            throw new FileException(ResponseCode.FileUploadFailed, "OSS服务异常: " + oe.getErrorMessage());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
