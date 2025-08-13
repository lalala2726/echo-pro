package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Chuang
 */
public interface SysSecurityLogService extends IService<SysSecurityLog> {

    /**
     * 获取用户安全日志
     *
     * @param userId 用户ID
     * @return 用户安全日志列表
     */
    Page<SysSecurityLog> getUserSecurityLog(Long userId, BasePageRequest request);
}
