package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysLoginLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author zhangchuang
 */
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    Page<SysLoginLog> listLoginLog(Page<SysLoginLog> sysLoginLogPage);

    void cleanLoginLog();
}




