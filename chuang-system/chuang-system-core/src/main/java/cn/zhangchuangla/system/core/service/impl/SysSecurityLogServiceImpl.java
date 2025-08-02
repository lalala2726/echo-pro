package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.system.core.mapper.SysSecurityLogMapper;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import cn.zhangchuangla.system.core.service.SysSecurityLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<SysSecurityLog> getUserSecurityLog(Long userId) {
        return lambdaQuery().eq(SysSecurityLog::getUserId, userId)
                .ge(SysSecurityLog::getOperationTime, LocalDateTime.now().minusDays(30))
                .list();
    }
}




