package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.request.log.SysOperationLogListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author zhangchuang
 */
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    Page<SysOperationLog> listOperationLog(Page<SysOperationLog> sysOperationLogPage, SysOperationLogListRequest request);

    void cleanLoginLog();
}




