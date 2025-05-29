package cn.zhangchuangla.quartz.service.impl;

import cn.zhangchuangla.quartz.mapper.SysJobLogMapper;
import cn.zhangchuangla.quartz.model.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobLogListQueryRequest;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog>
        implements SysJobLogService {

    private final SysJobLogMapper sysJobLogMapper;

    @Override
    public List<SysJobLog> listJobLogs(SysJobLogListQueryRequest request) {
        Page<SysJobLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysJobLogMapper.listJobLogs(page, request);
    }

    @Override
    public SysJobLog getJobLogById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobLog(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    public void cleanJobLog() {
        sysJobLogMapper.cleanJobLog();
    }
}




