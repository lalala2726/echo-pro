package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.redis.ConfigCacheService;
import cn.zhangchuangla.common.entity.file.FileInfo;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.URLUtils;
import cn.zhangchuangla.system.mapper.FileManagementMapper;
import cn.zhangchuangla.system.model.entity.FileManagement;
import cn.zhangchuangla.system.model.request.file.FileManagementListRequest;
import cn.zhangchuangla.system.service.FileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zhangchuang
 */
@Service
@Slf4j
public class FileManagementServiceImpl extends ServiceImpl<FileManagementMapper, FileManagement>
        implements FileManagementService {

    private final ConfigCacheService configCacheService;
    private final FileManagementMapper fileManagementMapper;

    public FileManagementServiceImpl(ConfigCacheService configCacheService, FileManagementMapper fileManagementMapper) {
        this.configCacheService = configCacheService;
        this.fileManagementMapper = fileManagementMapper;
    }


    /**
     * 文件列表
     *
     * @param request 文件列表请求参数
     * @return 文件列表分页结果
     */
    @Override
    public Page<FileManagement> fileList(FileManagementListRequest request) {
        Page<FileManagement> page = page(new Page<>(request.getPageNum(), request.getPageSize()));
        return fileManagementMapper.fileList(page, request);
    }

    /**
     * 删除文件
     *
     * @param ids 文件ID集合
     */
    @Override
    public void deleteFile(List<Long> ids) {
        ids.forEach(id -> {
            FileManagement file = getById(id);
        });
    }



    @Override
    public void saveFileRecord(String fileUrl, String compressedUrl, FileInfo fileInfo, String storageType) {
        try {
            // 获取当前用户信息
            Long userId = SecurityUtils.getUserId();
            String userName = SecurityUtils.getUsername();

            // 从URL中提取路径
            String filePath = URLUtils.extractPathFromUrl(fileUrl);

            // 获取存储桶名称（仅对MinIO和OSS有效）
            String bucketName = null;
            if (Constants.MINIO_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getMinioConfig().getBucketName();
            } else if (Constants.ALIYUN_OSS_FILE_UPLOAD.equals(storageType)) {
                bucketName = configCacheService.getAliyunOSSConfig().getBucketName();
            }

            // 创建文件记录
            FileManagement record = new FileManagement();
            record.setFileName(fileInfo.getOriginalFilename());
            record.setOriginalFileName(fileInfo.getOriginalFilename());
            record.setFilePath(filePath);
            record.setFileUrl(fileUrl);
            record.setFileSize(fileInfo.getSize());
            record.setFileType(fileInfo.getContentType());
            record.setFileExtension(fileInfo.getFileExtension());
            record.setStorageType(storageType);
            record.setBucketName(bucketName);
            record.setUploaderId(userId);
            record.setMd5(fileInfo.getMd5());
            record.setUploaderName(userName);
            record.setCreateBy(Constants.SYSTEM_CREATE);
            record.setUploadTime(new Date());
            record.setPreviewImage(compressedUrl);

            save(record);
        } catch (Exception e) {
            log.error("保存文件记录失败: {}", e.getMessage(), e);
            // 不抛出异常，避免影响上传流程
        }
    }


}




