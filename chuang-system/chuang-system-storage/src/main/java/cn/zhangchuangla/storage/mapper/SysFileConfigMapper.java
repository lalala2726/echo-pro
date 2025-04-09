package cn.zhangchuangla.storage.mapper;

import cn.zhangchuangla.storage.model.entity.SysFileConfig;
import cn.zhangchuangla.storage.model.request.config.SysFileConfigListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysFileConfigMapper extends BaseMapper<SysFileConfig> {

    /**
     * 分页查询文件配置信息
     *
     * @param sysFileConfigPage 分页对象
     * @param request           查询参数
     * @return 返回分页结果
     */
    Page<SysFileConfig> listSysFileConfig(Page<SysFileConfig> sysFileConfigPage, @Param("request") SysFileConfigListRequest request);
}




