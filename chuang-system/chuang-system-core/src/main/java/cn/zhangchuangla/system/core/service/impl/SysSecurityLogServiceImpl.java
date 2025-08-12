package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import cn.zhangchuangla.system.core.mapper.SysSecurityLogMapper;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import cn.zhangchuangla.system.core.service.SysSecurityLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author Chuang
 */
@Service
public class SysSecurityLogServiceImpl extends ServiceImpl<SysSecurityLogMapper, SysSecurityLog>
        implements SysSecurityLogService {

    /**
     * 获取用户安全日志
     *
     * @param userId 用户ID
     * @return 用户安全日志
     */
    @Override
    public Page<SysSecurityLog> getUserSecurityLog(Long userId, BasePageRequest request) {
        Page<SysSecurityLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        return lambdaQuery()
                .eq(SysSecurityLog::getUserId, userId)
                .ge(SysSecurityLog::getOperationTime, LocalDateTime.now().minusDays(30))
                .page(page);
    }
}




