package cn.zhangchuangla.common.config.loader;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.exception.ProfileException;
import cn.zhangchuangla.system.model.entity.SysFileConfig;
import cn.zhangchuangla.system.service.SysFileConfigService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/3 15:37
 */
@Component
@Slf4j
public class SysFileConfigLoader {

    private final SysFileConfigService sysFileConfigService;
    // 本地缓存
    private final Map<String, Object> sysFileConfigCache = new HashMap<>(2);

    @Autowired
    public SysFileConfigLoader(SysFileConfigService sysFileConfigService) {
        this.sysFileConfigService = sysFileConfigService;
    }

    @PostConstruct
    public void loadMasterConfig() {
        log.info("开始从数据库中加载主要配置");
        SysFileConfig config = sysFileConfigService.getMasterConfig();
        if (config == null) {
            log.error("没有找到主要配置");
            throw new ProfileException("没有找到文件主配置信息，请您配置");
        }
        loadConfigByStorageType(config);
    }


    public void loadConfigByStorageType(SysFileConfig sysFileConfig) {
        sysFileConfigCache.put(Constants.CURRENT_DEFAULT_UPLOAD_TYPE, sysFileConfig.getStorageType());
        switch (sysFileConfig.getStorageType()) {
            case Constants.LOCAL_FILE_UPLOAD:
                loadMinioConfig(sysFileConfig);
                break;
            case Constants.MINIO_FILE_UPLOAD:
                loadAliyunOSS(sysFileConfig);
                break;
            case Constants.ALIYUN_OSS_FILE_UPLOAD:
                loadLocalFileConfig(sysFileConfig);
                break;
            default:
        }
    }

    public String getCurrentDefaultUploadType() {
        return (String) sysFileConfigCache.get(Constants.CURRENT_DEFAULT_UPLOAD_TYPE);
    }

    /**
     * 获取minio配置
     */
    public MinioConfigEntity getMinioConfig() {
        return (MinioConfigEntity) sysFileConfigCache.get(Constants.MINIO_FILE_UPLOAD);
    }

    /**
     * 获取本地文件配置
     */
    public LocalFileConfigEntity getLocalFileConfig() {
        return (LocalFileConfigEntity) sysFileConfigCache.get(Constants.LOCAL_FILE_UPLOAD);
    }

    /**
     * 获取阿里云oss配置
     */
    public AliyunOSSConfigEntity getAliyunOSSConfig() {
        return (AliyunOSSConfigEntity) sysFileConfigCache.get(Constants.ALIYUN_OSS_FILE_UPLOAD);
    }


    /**
     * 加载minio配置
     */
    public void loadMinioConfig(SysFileConfig sysFileConfig) {
        MinioConfigEntity minioConfigEntity = (MinioConfigEntity) JSON.parse(sysFileConfig.getStorageValue());
        sysFileConfigCache.put(Constants.MINIO_FILE_UPLOAD, minioConfigEntity);
    }

    /**
     * 加载阿里云oss配置
     */
    public void loadAliyunOSS(SysFileConfig sysFileConfig) {
        AliyunOSSConfigEntity aliyunOSSConfig = (AliyunOSSConfigEntity) JSON.parse(sysFileConfig.getStorageValue());
        sysFileConfigCache.put(Constants.ALIYUN_OSS_FILE_UPLOAD, aliyunOSSConfig);
    }

    /**
     * 加载本地文件配置
     */
    public void loadLocalFileConfig(SysFileConfig sysFileConfig) {
        LocalFileConfigEntity LocalFileConfigEntity = (LocalFileConfigEntity) JSON.parse(sysFileConfig.getStorageValue());
        sysFileConfigCache.put(Constants.LOCAL_FILE_UPLOAD, LocalFileConfigEntity);
    }

    //todo 腾讯云配置
}
