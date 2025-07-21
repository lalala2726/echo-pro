package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.request.config.*;
import cn.zhangchuangla.storage.model.vo.config.StorageConfigUnifiedVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 文件配置接口
 *
 * @author Chuang
 */
public interface StorageConfigService extends IService<StorageConfig> {


    /**
     * 文件配置列表
     *
     * @param request 查询参数
     * @return 返回文件配置列表
     */
    Page<StorageConfig> listSysFileConfig(StorageConfigQueryRequest request);

    /**
     * 判断存储key是否存在
     *
     * @param storageKey 存储key
     * @return true 存在，false不存在
     */
    boolean isStorageKeyExist(String storageKey);


    /**
     * 判断是否是主配置
     *
     * @param id 文件配置id
     * @return true 是主配置，false不是主配置
     */
    boolean isPrimary(Integer id);

    /**
     * 读取主配置
     *
     * @return 返回主配置
     */
    StorageConfig getPrimaryConfig();


    /**
     * 判断存储名称是否存在
     *
     * @param storageName 存储名称
     * @return true 存在，false不存在
     */
    boolean isNameExist(String storageName);

    /**
     * 设置主配置
     *
     * @param id 文件配置id
     * @return 操作结果
     */
    boolean updatePrimaryConfig(Long id);

    /**
     * 删除文件配置，支持批量删除
     *
     * @param ids 文件配置id列表
     * @return 操作结果
     */
    boolean deleteFileConfig(List<Long> ids);

    /**
     * 添加Minio存储配置
     *
     * @param request 添加Minio存储配置参数
     * @return 操作结果
     */
    boolean addMinioConfig(MinioConfigSaveRequest request);

    /**
     * 添加阿里云OSS存储配置
     *
     * @param request 添加阿里云OSS存储配置参数
     * @return 操作结果
     */
    boolean addAliyunOssConfig(AliyunOssConfigSaveRequest request);

    /**
     * 添加腾讯云COS存储配置
     *
     * @param request 添加腾讯云COS存储配置参数
     * @return 操作结果
     */
    boolean addTencentCosConfig(TencentCosConfigSaveRequest request);

    /**
     * 添加AmazonS3存储配置
     *
     * @param request 添加AmazonS3存储配置参数
     * @return 操作结果
     */
    boolean addAmazonS3Config(AmazonS3ConfigSaveRequest request);

    /**
     * 获取文件存储配置
     *
     * @param id 文件存储配置ID
     * @return 文件存储配置
     */
    StorageConfigUnifiedVo getStorageConfigById(Long id);

    /**
     * 修改存储配置
     *
     * @param request 修改存储配置参数
     * @return 修改结果
     */
    boolean updateStorageConConfig(StorageConfigUpdateRequest request);

    /**
     * 检查存储配置键值是否已存在
     *
     * @param id         存储配置ID
     * @param storageKey 存储配置键值
     * @return 是否已存在
     */
    boolean isStorageKeyExists(Long id, String storageKey);
}
