package cn.zhangchuangla.storage.config.loader;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.StorageTypeConstants;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
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
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 系统文件配置加载器
 * 负责从数据库加载存储配置，并提供获取配置的接口
 */
@Component
@Slf4j
public class SysFileConfigLoader {

    private final SysFileConfigService sysFileConfigService;
    // 使用 HashMap 存储配置信息，值为 JSON 字符串
    private final Map<String, String> sysFileConfigCache = new HashMap<>(2);
    private final Map<String, Consumer<SysFileConfig>> configLoaders = new HashMap<>();
    private final AppConfig appConfig;

    @Autowired
    public SysFileConfigLoader(SysFileConfigService sysFileConfigService, AppConfig appConfig) {
        this.sysFileConfigService = sysFileConfigService;
        this.appConfig = appConfig;
    }

    /**
     * 初始化配置加载器，在系统启动时自动加载主要配置
     */
    @PostConstruct
    public void init() {
        log.info("开始从数据库中加载主要配置");
        SysFileConfig config = sysFileConfigService.getMasterConfig();
        if (config == null) {
            log.error("没有找到主要配置! 自动设置本地存储");
            autoSetLocalStorage();
            log.info("自动设置本地存储成功！");
            return;
        }
        initLoaders();
        loadConfigByStorageType(config);
    }

    /**
     * 初始化存储类型对应的加载方法
     */
    private void initLoaders() {
        configLoaders.put(StorageTypeConstants.LOCAL, this::loadLocalFileConfig);
        configLoaders.put(StorageTypeConstants.MINIO, this::loadMinioConfig);
        configLoaders.put(StorageTypeConstants.ALIYUN_OSS, this::loadAliyunOSSConfig);
    }

    /**
     * 根据存储类型加载配置
     */
    public void loadConfigByStorageType(SysFileConfig sysFileConfig) {
        sysFileConfigCache.put(Constants.CURRENT_DEFAULT_UPLOAD_TYPE, sysFileConfig.getStorageType());
        Optional.ofNullable(configLoaders.get(sysFileConfig.getStorageType()))
                .ifPresentOrElse(loader -> loader.accept(sysFileConfig),
                        () -> log.warn("未识别的存储类型: {}", sysFileConfig.getStorageType()));
    }

    /**
     * 自动设置本地存储
     */
    public void autoSetLocalStorage() {
        try {
            String uploadPath = appConfig.getUploadPath();
            if (uploadPath == null || uploadPath.isEmpty()) {
                throw new ProfileException("本地存储路径为空！");
            }
            sysFileConfigCache.put(StorageTypeConstants.LOCAL, JSON.toJSONString(new LocalFileConfigEntity(uploadPath)));
            sysFileConfigCache.put(Constants.CURRENT_DEFAULT_UPLOAD_TYPE, StorageTypeConstants.LOCAL);
        } catch (Exception e) {
            log.error("没有找到本地文件上传配置! 项目会正常启动！但是将无法上传文件！错误详情: {}", e.getMessage());
        }
    }

    /**
     * 获取当前默认上传类型
     */
    public String getCurrentDefaultUploadType() {
        return Optional.ofNullable(sysFileConfigCache.get(Constants.CURRENT_DEFAULT_UPLOAD_TYPE))
                .filter(config -> !config.isEmpty())
                .orElseThrow(() -> new ProfileException(ResponseCode.PROFILE_ERROR, "无法设置存储！请在系统中设置一个存储"));
    }

    /**
     * 解析 JSON 配置数据
     */
    private <T> T parseConfig(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            log.warn("解析配置失败: JSON 数据为空");
            return null;
        }
        return JSON.parseObject(json, clazz);
    }

    /**
     * 获取 MinIO 配置
     */
    public MinioConfigEntity getMinioConfig() {
        return parseConfig(sysFileConfigCache.get(StorageTypeConstants.MINIO), MinioConfigEntity.class);
    }

    /**
     * 获取本地文件存储配置
     */
    public LocalFileConfigEntity getLocalFileConfig() {
        return parseConfig(sysFileConfigCache.get(StorageTypeConstants.LOCAL), LocalFileConfigEntity.class);
    }

    /**
     * 获取阿里云 OSS 配置
     */
    public AliyunOSSConfigEntity getAliyunOSSConfig() {
        return parseConfig(sysFileConfigCache.get(StorageTypeConstants.ALIYUN_OSS), AliyunOSSConfigEntity.class);
    }

    /**
     * 加载 MinIO 配置
     */
    public void loadMinioConfig(SysFileConfig sysFileConfig) {
        sysFileConfigCache.put(StorageTypeConstants.MINIO, sysFileConfig.getStorageValue());
    }

    /**
     * 加载阿里云 OSS 配置
     */
    public void loadAliyunOSSConfig(SysFileConfig sysFileConfig) {
        sysFileConfigCache.put(StorageTypeConstants.ALIYUN_OSS, sysFileConfig.getStorageValue());
    }

    /**
     * 加载本地文件存储配置
     */
    public void loadLocalFileConfig(SysFileConfig sysFileConfig) {
        sysFileConfigCache.put(StorageTypeConstants.LOCAL, sysFileConfig.getStorageValue());
    }
}