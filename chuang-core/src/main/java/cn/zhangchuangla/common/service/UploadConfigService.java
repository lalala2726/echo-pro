package cn.zhangchuangla.common.service;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.entity.UploadProperties;
import cn.zhangchuangla.common.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ProfileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UploadConfigService {

    private final UploadProperties uploadProperties;

    private final RedisCache redisCache;

    /**
     * 项目启动时加载 Redis 配置
     */
    //todo 重构文件加载文件配置等信息
    public void loadUploadConfig() {
        String config = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_FILE_UPLOAD_SERVICE_SELECT);
        // 如果 Redis 中没有配置，则使用默认使用本地文件进行上传
        uploadProperties.setFileUploadType(config != null ? config : Constants.LOCAL_FILE_UPLOAD);

        // 根据不同的上传类型加载对应的配置
        if (config != null) {
            switch (config) {
                case Constants.ALIYUN_OSS_FILE_UPLOAD:
                    loadOssConfig();
                    break;
                case Constants.MINIO_FILE_UPLOAD:
                    loadMinioConfig();
                    break;
                case Constants.LOCAL_FILE_UPLOAD:
                default:
                    loadLocalConfig();
                    break;
            }
        }
    }

    /**
     * 加载 OSS 配置
     */
    private void loadOssConfig() {
        AliyunOSSConfigEntity ossConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_CONFIG + "upload:aliyun:config");
        if (ossConfig == null) {
            throw new ProfileException(ResponseCode.PROFILE_ERROR, "OSS配置文件错误，请求重新配置");
        }
        uploadProperties.setAliyunOSSConfigEntity(ossConfig);
    }

    /**
     * 加载 MinIO 配置
     */
    private void loadMinioConfig() {
        MinioConfigEntity minioConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_CONFIG + "upload:minio:config");
        if (minioConfig == null) {
            throw new ProfileException(ResponseCode.PROFILE_ERROR, "MinIO配置文件错误，请求重新配置");
        }
        uploadProperties.setMinioConfigEntity(minioConfig);
    }

    /**
     * 加载本地上传配置
     */
    private void loadLocalConfig() {
        LocalFileConfigEntity localFileConfig = redisCache.getCacheObject(RedisKeyConstant.SYSTEM_CONFIG + "upload:local:config");
        if (localFileConfig == null) {
            throw new ProfileException(ResponseCode.PROFILE_ERROR, "本地上传配置文件错误，请求重新配置");
        }
        uploadProperties.setLocalFileConfigEntity(localFileConfig);
    }
}
