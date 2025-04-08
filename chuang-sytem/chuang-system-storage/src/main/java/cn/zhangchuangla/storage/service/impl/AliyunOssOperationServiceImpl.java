package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.storage.config.loader.SysFileConfigLoader;
import cn.zhangchuangla.storage.service.AliyunOssOperationService;
import cn.zhangchuangla.storage.utils.AliyunOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public FileTransferDto fileUpload(FileTransferDto fileTransferDto) {
        AliyunOSSConfigEntity aliyunOSSConfig = sysFileConfigLoader.getAliyunOSSConfig();
        return AliyunOssUtils.uploadFile(fileTransferDto, aliyunOSSConfig);
    }


    /**
     * 上传图片
     *
     * @param fileTransferDto 文件传输对象
     * @return FileTransferDto
     */
    @Override
    public FileTransferDto imageUpload(FileTransferDto fileTransferDto) {
        return AliyunOssUtils.imageUpload(fileTransferDto, sysFileConfigLoader.getAliyunOSSConfig());
    }

    @Override
    public boolean removeFile(FileTransferDto fileTransferDto, boolean isDelete) {
        return false;
    }
}
