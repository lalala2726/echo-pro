package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysOperationLogMapper;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.service.SysOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现类
 *
 * @author zhangchuang
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog>
        implements SysOperationLogService {
}




