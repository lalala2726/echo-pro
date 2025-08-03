package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
    List<SysSecurityLog> getUserSecurityLog(Long userId);
}
