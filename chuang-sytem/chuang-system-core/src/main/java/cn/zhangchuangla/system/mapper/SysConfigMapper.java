package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysConfig;
import cn.zhangchuangla.system.model.request.config.SysConfigListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 分页查询
     *
     * @param sysConfigPage 分页对象
     * @param request       查询参数
     * @return 返回分页数据
     */
    Page<SysConfig> listSysConfig(Page<SysConfig> sysConfigPage, @Param("request") SysConfigListRequest request);
}




