package cn.zhangchuangla.storage.mapper;

import cn.zhangchuangla.storage.model.entity.StorageConfig;
import cn.zhangchuangla.storage.model.request.config.StorageConfigQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author Chuang
 */
public interface SysFileConfigMapper extends BaseMapper<StorageConfig> {

    /**
     * 分页查询文件配置信息
     *
     * @param sysFileConfigPage 分页对象
     * @param request           查询参数
     * @return 返回分页结果
     */
    Page<StorageConfig> listSysFileConfig(Page<StorageConfig> sysFileConfigPage, @Param("request") StorageConfigQueryRequest request);
}




