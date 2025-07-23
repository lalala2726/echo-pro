package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.storage.constant.StorageConstants;
import cn.zhangchuangla.storage.mapper.StorageConfigMapper;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.entity.config.AliyunOssStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.AmazonS3StorageConfig;
import cn.zhangchuangla.storage.model.entity.config.MinioStorageConfig;
import cn.zhangchuangla.storage.model.entity.config.TencentCosStorageConfig;
import cn.zhangchuangla.storage.model.request.config.*;
import cn.zhangchuangla.storage.model.vo.config.*;
import cn.zhangchuangla.storage.service.StorageConfigService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件配置服务实现类
 *
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StorageConfigServiceImpl extends ServiceImpl<StorageConfigMapper, StorageConfig>
        implements StorageConfigService {

    private final StorageConfigMapper storageConfigMapper;

    /**
     * 查询文件配置列表
     *
     * @param request 查询参数
     * @return 文件配置列表
     */
    @Override
    public Page<StorageConfig> listSysFileConfig(StorageConfigQueryRequest request) {
        Page<StorageConfig> sysFileConfigPage = new Page<>(request.getPageNum(), request.getPageSize());
        return storageConfigMapper.listStorageConfig(sysFileConfigPage, request);
    }


    /**
     * 判断存储key是否存在
     *
     * @param storageKey 存储key名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isStorageKeyExist(String storageKey) {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>()
                .eq(StorageConfig::getStorageKey, storageKey);
        return count(eq) > 0;
    }

    /**
     * 判断是否主配置
     *
     * @param id 文件配置id
     * @return true是主配置，false不是主配置
     */
    @Override
    public boolean isPrimary(Integer id) {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>()
                .eq(StorageConfig::getId, id)
                .eq(StorageConfig::getIsPrimary, StorageConstants.dataVerifyConstants.PRIMARY);
        return count(eq) > 0;
    }

    /**
     * 读取主配置
     *
     * @return 主配置
     */
    @Override
    public StorageConfig getPrimaryConfig() {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>()
                .eq(StorageConfig::getIsPrimary, StorageConstants.dataVerifyConstants.PRIMARY);
        return getOne(eq);
    }

    /**
     * 判断存储名称是否存在
     *
     * @param storageName 存储名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isNameExist(String storageName) {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>()
                .eq(StorageConfig::getStorageName, storageName);
        long count = count(eq);
        return count > 0;
    }

    @Override
    @Transactional
    public boolean updatePrimaryConfig(Long id) {
        // 取消当前主配置
        UpdateWrapper<StorageConfig> wrapper = new UpdateWrapper<>();
        wrapper.lambda()
                .eq(StorageConfig::getIsPrimary, StorageConstants.dataVerifyConstants.PRIMARY)
                .set(StorageConfig::getIsPrimary, !StorageConstants.dataVerifyConstants.PRIMARY);
        this.update(null, wrapper);

        // 设置新的主配置
        StorageConfig storageConfig = StorageConfig.builder()
                .id(id)
                .isPrimary(StorageConstants.dataVerifyConstants.PRIMARY)
                .build();
        return updateById(storageConfig);
    }

    /**
     * 删除文件配置，支持批量删除
     *
     * @param ids 文件配置id列表
     * @return 操作结果
     */
    @Override
    public boolean deleteFileConfig(List<Long> ids) {
        StorageConfig primaryConfig = getPrimaryConfig();
        ids.forEach(id -> {
            if (Objects.equals(primaryConfig.getId(), id)) {
                throw new ServiceException(ResultCode.OPERATION_ERROR, "主文件配置不能删除");
            }
        });
        return removeByIds(ids);
    }

    /**
     * 添加Minio存储配置
     *
     * @param request 添加Minio存储配置参数
     * @return 操作结果
     */
    @Override
    public boolean addMinioConfig(MinioConfigSaveRequest request) {
        MinioStorageConfig minioStorageConfig = BeanCotyUtils.copyProperties(request, MinioStorageConfig.class);
        return saveStorageConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.StorageType.MINIO,
                minioStorageConfig.toJson());
    }

    /**
     * 添加阿里云OSS配置
     *
     * @param request 阿里云OSS配置参数
     * @return 是否添加成功
     */
    @Override
    public boolean addAliyunOssConfig(AliyunOssConfigSaveRequest request) {
        AliyunOssStorageConfig aliyunOssStorageConfig = BeanCotyUtils.copyProperties(request, AliyunOssStorageConfig.class);
        return saveStorageConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.StorageType.ALIYUN_OSS,
                aliyunOssStorageConfig.toJson());
    }

    /**
     * 添加腾讯云COS配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean addTencentCosConfig(TencentCosConfigSaveRequest request) {
        TencentCosStorageConfig tencentCosStorageConfig = BeanCotyUtils.copyProperties(request, TencentCosStorageConfig.class);
        return saveStorageConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.StorageType.TENCENT_COS,
                tencentCosStorageConfig.toJson());
    }

    /**
     * 添加腾讯云COS存储配置
     *
     * @param request 添加AmazonS3存储配置参数
     * @return 添加结果
     */
    @Override
    public boolean addAmazonS3Config(AmazonS3ConfigSaveRequest request) {
        AmazonS3StorageConfig amazonS3StorageConfig = BeanCotyUtils.copyProperties(request, AmazonS3StorageConfig.class);
        return saveStorageConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.StorageType.AMAZON_S3,
                amazonS3StorageConfig.toJson());
    }

    /**
     * 根据ID获取文件存储配置
     *
     * @param id 文件存储配置ID
     * @return 文件存储配置
     */
    @Override
    public StorageConfigUnifiedVo getStorageConfigById(Long id) {
        Assert.isTrue(id > 0, "文件存储配置ID不能小于0");
        StorageConfig storageConfig = getById(id);
        if (storageConfig == null) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "文件存储配置不存在");
        }
        return toUnifiedVo(storageConfig);
    }

    /**
     * 修改存储配置
     *
     * @param request 修改存储配置参数
     * @return 修改结果
     */
    @Override
    public boolean updateStorageConConfig(StorageConfigUpdateRequest request) {
        StorageConfig storageConfig = getById(request.getId());
        if (storageConfig == null) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "文件存储配置不存在");
        }
        toStorageConfig(request, storageConfig);
        return updateById(storageConfig);
    }

    /**
     * 检查存储配置键值是否已存在
     *
     * @param id         存储配置ID
     * @param storageKey 存储配置键值
     * @return 存储配置键值是否已存在
     */
    @Override
    public boolean isStorageKeyExists(Long id, String storageKey) {
        LambdaQueryWrapper<StorageConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StorageConfig::getStorageKey, storageKey);
        if (id != null) {
            queryWrapper.ne(StorageConfig::getId, id);
        }
        return count(queryWrapper) > 0;
    }

    /**
     * 查询存储配置,无分页
     *
     * @param request 存储配置查询参数
     * @return 存储配置列表
     */
    @Override
    public List<StorageConfigUnifiedVo> listStorageConfig(StorageConfigQueryRequest request) {
        List<StorageConfig> storageConfigs = storageConfigMapper.listStorageConfig(request);
        ArrayList<StorageConfigUnifiedVo> storageConfigUnifiedVos = new ArrayList<>();
        storageConfigs.forEach(storageConfig -> {
                    StorageConfigUnifiedVo unifiedVo = toUnifiedVo(storageConfig);
                    storageConfigUnifiedVos.add(unifiedVo);
                }
        );
        return storageConfigUnifiedVos;
    }


    /**
     * 获取存储配置键值选项
     *
     * @return 存储配置键值选项
     */
    @Override
    public List<Option<String>> getStorageConfigKeyOption() {
        return list()
                .stream()
                .map(storageConfig -> new Option<>(storageConfig.getStorageKey(), storageConfig.getStorageName())).toList();
    }

    /**
     * 转换请求参数为存储配置实体对象
     *
     * @param request       请求参数
     * @param storageConfig 存储配置实体类
     */
    private void toStorageConfig(StorageConfigUpdateRequest request, StorageConfig storageConfig) {
        BeanUtils.copyProperties(request, storageConfig);
        switch (storageConfig.getStorageType()) {
            case StorageConstants.StorageType.ALIYUN_OSS:
                AliyunOssConfigSaveRequest aliyunOss = request.getAliyunOss();
                AliyunOssStorageConfig aliyunOssStorageConfig = BeanCotyUtils.copyProperties(aliyunOss, AliyunOssStorageConfig.class);
                storageConfig.setStorageValue(aliyunOssStorageConfig.toJson());
                break;
            case StorageConstants.StorageType.MINIO:
                MinioConfigSaveRequest minio = request.getMinio();
                MinioStorageConfig minioStorageConfig = BeanCotyUtils.copyProperties(minio, MinioStorageConfig.class);
                storageConfig.setStorageValue(minioStorageConfig.toJson());
                break;
            case StorageConstants.StorageType.AMAZON_S3:
                AmazonS3ConfigSaveRequest amazonS3 = request.getAmazonS3();
                AmazonS3StorageConfig amazonS3StorageConfig = BeanCotyUtils.copyProperties(amazonS3, AmazonS3StorageConfig.class);
                storageConfig.setStorageValue(amazonS3StorageConfig.toJson());
                storageConfig.setStorageType(StorageConstants.StorageType.AMAZON_S3);
                break;
            case StorageConstants.StorageType.TENCENT_COS:
                TencentCosConfigSaveRequest tencentCos = request.getTencentCos();
                TencentCosStorageConfig tencentCosStorageConfig = BeanCotyUtils.copyProperties(tencentCos, TencentCosStorageConfig.class);
                storageConfig.setStorageValue(tencentCosStorageConfig.toJson());
                break;
            default:
                throw new ServiceException(ResultCode.SERVER_CANNOT_SUPPORT);
        }
    }

    /**
     * 将文件存储配置转换为文件存储配置统一视图对象
     *
     * @param storageConfig 文件存储配置
     * @return 文件存储配置统一视图对象
     */
    private StorageConfigUnifiedVo toUnifiedVo(StorageConfig storageConfig) {
        StorageConfigUnifiedVo storageConfigUnifiedVo = BeanCotyUtils.copyProperties(storageConfig, StorageConfigUnifiedVo.class);
        switch (storageConfig.getStorageType()) {
            case StorageConstants.StorageType.ALIYUN_OSS:
                AliyunOssStorageConfig aliyunOssStorageConfig = JSON.parseObject(storageConfig.getStorageValue(), AliyunOssStorageConfig.class);
                AliyunOssStorageConfigVo aliyunOssStorageConfigVo = BeanCotyUtils.copyProperties(aliyunOssStorageConfig, AliyunOssStorageConfigVo.class);
                storageConfigUnifiedVo.setAliyunOssStorageConfigVo(aliyunOssStorageConfigVo);
                break;
            case StorageConstants.StorageType.MINIO:
                MinioStorageConfig minioStorageConfig = JSON.parseObject(storageConfig.getStorageValue(), MinioStorageConfig.class);
                MinioStorageConfigVo minioStorageConfigVo = BeanCotyUtils.copyProperties(minioStorageConfig, MinioStorageConfigVo.class);
                storageConfigUnifiedVo.setMinioStorageConfigVo(minioStorageConfigVo);
                break;
            case StorageConstants.StorageType.AMAZON_S3:
                AmazonS3StorageConfig amazonS3StorageConfig = JSON.parseObject(storageConfig.getStorageValue(), AmazonS3StorageConfig.class);
                AmazonS3StorageConfigVo amazonS3StorageConfigVo = BeanCotyUtils.copyProperties(amazonS3StorageConfig, AmazonS3StorageConfigVo.class);
                storageConfigUnifiedVo.setAmazonS3StorageConfigVo(amazonS3StorageConfigVo);
                break;
            case StorageConstants.StorageType.TENCENT_COS:
                TencentCosStorageConfig tencentCosStorageConfig = JSON.parseObject(storageConfig.getStorageValue(), TencentCosStorageConfig.class);
                TencentCosStorageConfigVo tencentCosStorageConfigVo = BeanCotyUtils.copyProperties(tencentCosStorageConfig, TencentCosStorageConfigVo.class);
                storageConfigUnifiedVo.setTencentCosStorageConfigVo(tencentCosStorageConfigVo);
                break;
            default:
                throw new ServiceException(ResultCode.SERVER_CANNOT_SUPPORT);
        }
        return storageConfigUnifiedVo;
    }


    /**
     * 保存文件配置
     *
     * @param storageName 存储名字
     * @param storageKey  存储key
     * @param storageType 存储类型
     * @param value       文件配置文件（JSON）
     * @return 保存结果
     */
    private boolean saveStorageConfig(String storageName, String storageKey, String storageType, String value) {
        if (isStorageKeyExist(storageKey)) {
            throw new ServiceException(String.format("存储key【%s】已存在", storageName));
        }
        if (isNameExist(storageName)) {
            throw new ServiceException(String.format("存储名称【%s】已存在", storageKey));
        }
        StorageConfig storageConfig = StorageConfig.builder()
                .storageName(storageName)
                .storageKey(storageKey)
                .storageValue(value)
                .storageType(storageType)
                .build();
        return save(storageConfig);
    }

}




