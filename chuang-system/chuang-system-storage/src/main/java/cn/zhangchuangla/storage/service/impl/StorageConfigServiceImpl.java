package cn.zhangchuangla.storage.service.impl;

import cn.zhangchuangla.common.constant.StorageConstants;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.model.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.model.request.AliyunOSSConfigRequest;
import cn.zhangchuangla.common.model.request.LocalFileConfigRequest;
import cn.zhangchuangla.common.model.request.MinioConfigRequest;
import cn.zhangchuangla.common.model.request.TencentCOSConfigRequest;
import cn.zhangchuangla.storage.converter.StorageConverter;
import cn.zhangchuangla.storage.mapper.SysFileConfigMapper;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.request.config.StorageConfigAddRequest;
import cn.zhangchuangla.storage.model.request.config.StorageConfigListRequest;
import cn.zhangchuangla.storage.model.request.config.StorageConfigUpdateRequest;
import cn.zhangchuangla.storage.service.StorageConfigService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件配置服务实现类
 *
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StorageConfigServiceImpl extends ServiceImpl<SysFileConfigMapper, StorageConfig>
        implements StorageConfigService {

    private final SysFileConfigMapper sysFileConfigMapper;
    private final StorageConverter storageConverter;

    /**
     * 查询文件配置列表
     *
     * @param request 查询参数
     * @return 文件配置列表
     */
    @Override
    public Page<StorageConfig> listSysFileConfig(StorageConfigListRequest request) {
        Page<StorageConfig> sysFileConfigPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysFileConfigMapper.listSysFileConfig(sysFileConfigPage, request);
    }

    /**
     * 根据id获取文件配置信息
     *
     * @param id 文件配置id
     * @return 文件配置信息
     */
    @Override
    public StorageConfig getSysFileConfigById(Integer id) {
        return getById(id);
    }

    /**
     * 根据id删除文件配置信息
     *
     * @param id 文件配置id
     * @return 操作结果
     */
    @Override
    public boolean deleteSysFileConfigById(Integer id) {
        if (isMaster(id)) {
            throw new ServiceException("主配置不允许删除");
        }
        return removeById(id);
    }

    /**
     * 添加文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveFileConfig(StorageConfigAddRequest request) {
        if (isStorageKeyExist(request.getStorageKey())) {
            throw new ServiceException(String.format("存储key【%s】已存在", request.getStorageKey()));
        }
        StorageConfig storageConfig = storageConverter.toSysFileConfig(request);
        return save(storageConfig);
    }

    /**
     * 添加腾讯云COS配置文件
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveFileConfig(TencentCOSConfigRequest request) {
        TencentCOSConfigEntity tencentCOSConfigEntity = storageConverter.toTencentCOSConfigEntity(request);
        String value = JSON.toJSONString(tencentCOSConfigEntity);
        return saveFileConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.TENCENT_COS, value);
    }

    /**
     * 添加本地文件配置文件
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveFileConfig(LocalFileConfigRequest request) {
        LocalFileConfigEntity localFileConfig = storageConverter.toLocalFileConfigEntity(request);
        String value = JSON.toJSONString(localFileConfig);
        return saveFileConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.LOCAL, value);
    }

    /**
     * 添加阿里云OSS配置文件
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveFileConfig(AliyunOSSConfigRequest request) {
        AliyunOSSConfigEntity aliyunOSSConfigEntity = storageConverter.toAliyunOSSConfigEntity(request);
        String value = JSON.toJSONString(aliyunOSSConfigEntity);
        return saveFileConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.ALIYUN_OSS, value);
    }

    /**
     * 添加Minio配置文件
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean saveFileConfig(MinioConfigRequest request) {
        MinioConfigEntity minioConfigEntity = storageConverter.toMinioConfigEntity(request);
        String value = JSON.toJSONString(minioConfigEntity);
        return saveFileConfig(request.getStorageName(), request.getStorageKey(), StorageConstants.MINIO, value);
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
    private boolean saveFileConfig(String storageName, String storageKey, String storageType, String value) {
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

    /**
     * 更新文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateFileConfigById(StorageConfigUpdateRequest request) {
        if (isStorageKeyExist(request.getStorageKey())) {
            throw new ServiceException(String.format("存储key【%s】已存在", request.getStorageKey()));
        }
        StorageConfig storageConfig = storageConverter.toEntity(request);
        return updateById(storageConfig);
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
    public boolean isMaster(Integer id) {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>()
                .eq(StorageConfig::getId, id)
                .eq(StorageConfig::getIsMaster, StorageConstants.IS_FILE_UPLOAD_MASTER);
        return count(eq) > 0;
    }

    /**
     * 读取主配置
     *
     * @return 主配置
     */
    @Override
    public StorageConfig getMasterConfig() {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>()
                .eq(StorageConfig::getIsMaster, StorageConstants.IS_FILE_UPLOAD_MASTER);
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

    /**
     * 设置主配置
     *
     * @param id 文件配置id
     * @return 操作结果
     */
    @Override
    public boolean setMasterConfig(Long id) {
        // 取消当前主配置
        StorageConfig currentMasterConfig = getMasterConfig();
        if (currentMasterConfig != null) {
            currentMasterConfig.setIsMaster(StorageConstants.IS_NOT_FILE_UPLOAD_MASTER);
            updateById(currentMasterConfig);
        }
        // 设置新的主配置
        StorageConfig newMasterConfig = getById(id);
        if (newMasterConfig == null) {
            throw new ServiceException("文件配置不存在");
        }
        newMasterConfig.setIsMaster(StorageConstants.IS_FILE_UPLOAD_MASTER);
        return updateById(newMasterConfig);
    }

    /**
     * 删除文件配置，支持批量删除
     *
     * @param ids 文件配置id列表
     * @return 操作结果
     */
    @Override
    public boolean deleteFileConfig(List<Long> ids) {
        LambdaQueryWrapper<StorageConfig> eq = new LambdaQueryWrapper<StorageConfig>().eq(StorageConfig::getId, ids);
        List<StorageConfig> list = list(eq);
        list.forEach(sysFileConfig -> {
            if (StorageConstants.IS_FILE_UPLOAD_MASTER.equals(sysFileConfig.getIsMaster())) {
                throw new ServiceException(String.format("文件配置【%s】为当前主配置，不能删除", sysFileConfig.getStorageName()));
            }
        });
        return removeByIds(ids);
    }

}




