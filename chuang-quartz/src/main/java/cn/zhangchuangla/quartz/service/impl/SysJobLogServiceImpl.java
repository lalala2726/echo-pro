package cn.zhangchuangla.quartz.service.impl;

import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.entity.SysJobLog;
import cn.zhangchuangla.quartz.mapper.SysJobLogMapper;
import cn.zhangchuangla.quartz.model.request.SysJobLogListQueryRequest;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chuang
 */
@Service
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog>
        implements SysJobLogService {

    @Override
    public List<SysJob> listJobLogs(SysJobLogListQueryRequest request) {
        return List.of();
    }

    @Override
    public SysJob getJobLogById(Long id) {
        return null;
    }

    @Override
    public boolean deleteJobLog(List<Long> ids) {
        return false;
    }

    @Override
    public boolean cleanJobLog() {
        return false;
    }
}




