package cn.zhangchuangla.storage.utils;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.FileException;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.storage.dto.FileTransferDto;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
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
public class TencentCOSUtils {

    /**
     * 上传文件到腾讯云COS
     *
     * @param fileTransferDto 文件传输对象
     * @return 文件传输对象
     */
    public static FileTransferDto uploadFile(FileTransferDto fileTransferDto, TencentCOSConfigEntity tencentCOSConfigEntity) {
        if (tencentCOSConfigEntity == null)
            throw new FileException(ResponseCode.FileUploadFailed, "腾讯云COS配置不能为空！");
        String fileName = fileTransferDto.getFileName();
        byte[] data = fileTransferDto.getBytes();

        String region = tencentCOSConfigEntity.getRegion();
        String secretId = tencentCOSConfigEntity.getSecretId();
        String secretKey = tencentCOSConfigEntity.getSecretKey();
        String bucketName = tencentCOSConfigEntity.getBucketName();
        String fileDomain = tencentCOSConfigEntity.getFileDomain();

        // 创建COS客户端
        COSClient cosClient = null;
        try {
            // 1. 初始化用户身份信息（secretId, secretKey）
            COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

            // 2. 设置bucket的区域
            ClientConfig clientConfig = new ClientConfig(new Region(region));

            // 3. 创建COS客户端
            cosClient = new COSClient(cred, clientConfig);

            // 4. 检查存储桶是否存在
            if (!cosClient.doesBucketExist(bucketName)) {
                log.warn("Bucket {} 不存在，系统将尝试创建", bucketName);
                cosClient.createBucket(bucketName);
            }

            // 5. 生成存储路径
            String datePath = FileUtils.generateYearMonthDir();
            String fileExtension = FileUtils.getFileExtension(fileName);
            String uuid = FileUtils.generateUUID();
            String objectName = FileUtils.buildFinalPath(datePath, uuid + fileExtension);

            // 6. 设置元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(data.length);
            metadata.setContentType(FileUtils.generateFileContentType(fileName));

            // 7. 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, objectName, new ByteArrayInputStream(data), metadata);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);

            // 8. 构建并返回文件URL
            String fileUrl = FileUtils.buildFinalPath(fileDomain, objectName);

            return FileTransferDto.builder()
                    .fileUrl(fileUrl)
                    .relativePath(objectName)
                    .build();

        } catch (CosServiceException serviceException) {
            // COS服务异常
            log.error("COS服务异常: {} - {}",
                    serviceException.getErrorCode(), serviceException.getErrorMessage(), serviceException);
            throw new FileException(ResponseCode.FileUploadFailed,
                    "COS服务异常: " + serviceException.getErrorMessage());
        } catch (CosClientException clientException) {
            // COS客户端异常
            log.error("COS客户端异常", clientException);
            throw new FileException(ResponseCode.FileUploadFailed,
                    "COS客户端异常: " + clientException.getMessage());
        } catch (Exception e) {
            // 其他异常
            log.error("文件上传失败", e);
            throw new FileException(ResponseCode.FileUploadFailed);
        } finally {
            // 关闭客户端
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
    }
}
