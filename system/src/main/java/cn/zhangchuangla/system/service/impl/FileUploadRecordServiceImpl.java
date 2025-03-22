package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.FileUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.common.utils.URLUtils;
import cn.zhangchuangla.system.mapper.FileUploadRecordMapper;
import cn.zhangchuangla.system.model.entity.FileUploadRecord;
import cn.zhangchuangla.system.model.request.file.FileUploadRecordRequest;
import cn.zhangchuangla.system.service.FileUploadRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * @author zhangchuang
 */
@Service
@Slf4j
public class FileUploadRecordServiceImpl extends ServiceImpl<FileUploadRecordMapper, FileUploadRecord>
        implements FileUploadRecordService {

    private final ConfigCacheService configCacheService;
    private final FileUploadRecordMapper fileUploadRecordMapper;


    public FileUploadRecordServiceImpl(ConfigCacheService configCacheService, FileUploadRecordMapper fileUploadRecordMapper) {
        this.configCacheService = configCacheService;
        this.fileUploadRecordMapper = fileUploadRecordMapper;
    }


    /**
     * 保存文件信息到数据库
     * 此方法不处理文件上传，只负责记录已上传文件的信息
     *
     * @param fileUrl     文件访问URL
     * @param file        上传的文件对象
     * @param storageType 存储类型(LOCAL/MINIO/ALIYUN_OSS)
     */
    @Override
    public void saveFileInfo(String fileUrl, String compressedUrl, MultipartFile file, String storageType) {
        if (file == null) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "文件不能为空！");
        }

        if (StringUtils.isBlank(fileUrl)) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "文件URL不能为空！");
        }

        if (StringUtils.isBlank(storageType)) {
            throw new ServiceException(ResponseCode.PARAM_NOT_NULL, "存储类型不能为空！");
        }

        try {
            // 获取文件基本信息
            String originalFileName = file.getOriginalFilename();
            String contentType = file.getContentType();
            long fileSize = file.getSize();

            // 从URL中提取文件扩展名
            String fileExtension = URLUtils.extractExtensionFromUrl(fileUrl);
            if (StringUtils.isBlank(fileExtension) && originalFileName != null) {
                // 如果从URL无法提取到扩展名，则从原始文件名中提取
                fileExtension = FileUtils.getFileExtension(originalFileName);
            }

            // 从URL中提取文件名
            String fileName = URLUtils.extractFilenameFromUrl(fileUrl);

            // 从URL中提取路径
            String filePath = URLUtils.extractPathFromUrl(fileUrl);

            // 计算文件MD5值
            byte[] bytes = file.getBytes();
            String md5 = FileUtils.calculateMD5(bytes);

            // 获取当前用户信息
            Long userId = SecurityUtils.getUserId();
            String userName = SecurityUtils.getUsername();

            // 获取存储桶名称（仅对MinIO和OSS有效）
            String bucketName = null;
            if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getMinioConfig().getBucketName();
            } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getAliyunOSSConfig().getBucketName();
            }

            // 创建文件记录
            FileUploadRecord record = new FileUploadRecord();
            record.setFileName(fileName);
            record.setOriginalFileName(originalFileName);
            record.setFilePath(filePath);
            record.setFileUrl(fileUrl);
            record.setFileSize(fileSize);
            record.setFileType(contentType);
            record.setPreviewImage(compressedUrl);
            record.setFileExtension(fileExtension);
            record.setStorageType(storageType);
            record.setBucketName(bucketName);
            record.setMd5(md5);
            record.setUploaderId(userId);
            record.setUploaderName(userName);
            record.setCreateBy(Constants.SYSTEM_CREATE);
            record.setUploadTime(new Date());

            // 保存文件记录到数据库
            boolean save = save(record);

            // 记录上传日志
            log.info("文件信息保存成功 - 文件名: {}, 大小: {}, MD5: {}, 存储类型: {}, 访问URL: {}",
                    originalFileName, fileSize, md5, storageType, fileUrl);

        } catch (IOException e) {
            log.error("读取文件内容失败: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "读取文件内容失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("保存文件信息失败: {}", e.getMessage(), e);
            throw new ServiceException(ResponseCode.SYSTEM_ERROR, "保存文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 文件列表
     *
     * @param request 文件列表请求参数
     * @return 文件列表分页结果
     */
    @Override
    public Page<FileUploadRecord> fileList(FileUploadRecordRequest request) {
        Page<FileUploadRecord> page = page(new Page<>(request.getPageNum(), request.getPageSize()));
        return fileUploadRecordMapper.fileList(page, request);
    }


}




