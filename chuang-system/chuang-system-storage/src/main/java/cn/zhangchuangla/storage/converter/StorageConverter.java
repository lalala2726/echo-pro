package cn.zhangchuangla.storage.converter;

import cn.zhangchuangla.common.model.dto.FileTransferDto;
import cn.zhangchuangla.common.model.entity.file.AliyunOSSConfigEntity;
import cn.zhangchuangla.common.model.entity.file.LocalFileConfigEntity;
import cn.zhangchuangla.common.model.entity.file.MinioConfigEntity;
import cn.zhangchuangla.common.model.entity.file.TencentCOSConfigEntity;
import cn.zhangchuangla.common.model.request.AliyunOSSConfigRequest;
import cn.zhangchuangla.common.model.request.LocalFileConfigRequest;
import cn.zhangchuangla.common.model.request.MinioConfigRequest;
import cn.zhangchuangla.common.model.request.TencentCOSConfigRequest;
import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.entity.SysFileManagement;
import cn.zhangchuangla.storage.model.request.config.StorageConfigAddRequest;
import cn.zhangchuangla.storage.model.request.config.StorageConfigUpdateRequest;
import cn.zhangchuangla.storage.model.vo.manage.StorageFileManagementListVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:52
 */
@Mapper(componentModel = "spring")
public interface StorageConverter {

    /**
     * 将文件配置添加请求类 转换为 SysFileConfig 实体类
     *
     * @param request 文件配置添加请求参数
     * @return SysFileConfig 实体类
     */
    StorageConfig toSysFileConfig(StorageConfigAddRequest request);

    /**
     * 将腾讯云对象存储配置请求类 转换为 TencentCOSConfigEntity 实体类
     *
     * @param request 腾讯云对象存储配置请求参数
     * @return TencentCOSConfigEntity 实体类
     */
    TencentCOSConfigEntity toTencentCOSConfigEntity(TencentCOSConfigRequest request);

    /**
     * 将本地文件存储配置请求类 转换为 LocalFileConfigEntity 实体类
     *
     * @param request 本地文件存储配置请求参数
     * @return LocalFileConfigEntity 实体类
     */
    LocalFileConfigEntity toLocalFileConfigEntity(LocalFileConfigRequest request);

    /**
     * 将阿里云对象存储配置请求类 转换为 AliyunOSSConfigEntity 实体类
     *
     * @param request 阿里云对象存储配置请求参数
     * @return AliyunOSSConfigEntity 实体类
     */
    AliyunOSSConfigEntity toAliyunOSSConfigEntity(AliyunOSSConfigRequest request);

    /**
     * 将 Minio 配置请求类 转换为 MinioConfigEntity 实体类
     *
     * @param request Minio 配置请求参数
     * @return MinioConfigEntity 实体类
     */
    MinioConfigEntity toMinioConfigEntity(MinioConfigRequest request);

    /**
     * 将文件配置更新请求类 转换为 SysFileConfig 实体类
     *
     * @param request 文件配置更新请求参数
     * @return SysFileConfig 实体类
     */
    StorageConfig toEntity(StorageConfigUpdateRequest request);

    /**
     * 将文件配置实体类 转换为文件传输数据传输对象
     *
     * @param fileManagement 文件配置实体类
     * @return 文件传输数据传输对象
     */
    FileTransferDto toFileTransferDto(SysFileManagement fileManagement);

    /**
     * 将文件配置实体类 转换为文件配置请求参数
     *
     * @param sysFileManagement 文件配置实体类
     * @return 文件配置请求参数
     */
    StorageFileManagementListVo toSysFileManagementListVo(SysFileManagement sysFileManagement);
}
