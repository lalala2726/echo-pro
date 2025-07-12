package cn.zhangchuangla.storage.service;

import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.request.AliyunOSSConfigRequest;
import cn.zhangchuangla.storage.model.request.AmazonS3ConfigRequest;
import cn.zhangchuangla.storage.model.request.MinioConfigRequest;
import cn.zhangchuangla.storage.model.request.TencentCOSConfigRequest;
import cn.zhangchuangla.storage.model.request.config.StorageConfigAddRequest;
import cn.zhangchuangla.storage.model.request.config.StorageConfigQueryRequest;
import cn.zhangchuangla.storage.model.request.config.StorageConfigUpdateRequest;
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
     * 根据id查询文件配置
     *
     * @param id 文件配置id
     * @return 文件配置对象
     */
    StorageConfig getSysFileConfigById(Integer id);


    /**
     * 根据id删除文件配置
     *
     * @param id 文件配置id
     * @return 操作结果
     */
    boolean deleteSysFileConfigById(Integer id);


    /**
     * 新增文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addStorageConfig(StorageConfigAddRequest request);


    /**
     * 新增腾讯云COS配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addStorageConfig(TencentCOSConfigRequest request);


    /**
     * 新增阿里云OSS配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addStorageConfig(AliyunOSSConfigRequest request);


    /**
     * 新增亚马逊S3配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addStorageConfig(AmazonS3ConfigRequest request);


    /**
     * 新增Minio配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean addStorageConfig(MinioConfigRequest request);


    /**
     * 修改文件配置
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updateFileConfigById(StorageConfigUpdateRequest request);

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
    boolean isMaster(Integer id);

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

}
