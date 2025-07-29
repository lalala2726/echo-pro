package cn.zhangchuangla.system.storage.mapper;

import cn.zhangchuangla.system.storage.model.entity.StorageConfig;
import cn.zhangchuangla.system.storage.model.request.config.StorageConfigQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface StorageConfigMapper extends BaseMapper<StorageConfig> {

    /**
     * 分页查询文件配置信息
     *
     * @param sysFileConfigPage 分页对象
     * @param request           查询参数
     * @return 返回分页结果
     */
    Page<StorageConfig> listStorageConfig(Page<StorageConfig> sysFileConfigPage, @Param("request") StorageConfigQueryRequest request);

    /**
     * 查询文件配置信息,无分页
     *
     * @param request 查询参数
     * @return 返回结果
     */
    List<StorageConfig> listStorageConfig(@Param("request") StorageConfigQueryRequest request);
}




