package cn.zhangchuangla.quartz.service.impl;

import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.quartz.entity.SysJobLog;
import cn.zhangchuangla.quartz.mapper.SysJobLogMapper;
import cn.zhangchuangla.quartz.model.request.SysJobLogQueryRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobLogVo;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 定时任务日志服务实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysJobLogServiceImpl extends ServiceImpl<SysJobLogMapper, SysJobLog> implements SysJobLogService {

    private final SysJobLogMapper sysJobLogMapper;

    @Override
    public Page<SysJobLog> selectJobLogList(SysJobLogQueryRequest request) {
        Page<SysJobLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysJobLogMapper.selectJobLogList(page, request);
    }

    @Override
    public SysJobLogVo selectJobLogById(Long jobLogId) {
        SysJobLog jobLog = getById(jobLogId);
        if (jobLog == null) {
            return null;
        }

        SysJobLogVo logVo = new SysJobLogVo();
        BeanUtils.copyProperties(jobLog, logVo);
        setLogDescriptions(logVo);

        return logVo;
    }

    @Override
    public List<SysJobLogVo> selectLogsByJobId(Long jobId) {
        List<SysJobLog> logs = sysJobLogMapper.selectLogsByJobId(jobId);
        List<SysJobLogVo> logVos = BeanCotyUtils.copyListProperties(logs, SysJobLogVo.class);

        // 设置状态描述
        logVos.forEach(this::setLogDescriptions);

        return logVos;
    }

    @Override
    public boolean addJobLog(SysJobLog jobLog) {
        return save(jobLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobLogs(List<Long> logIds) {
        return removeByIds(logIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanLogsBefore(Date beforeDate) {
        return sysJobLogMapper.cleanLogsBefore(beforeDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanAllLogs() {
        return sysJobLogMapper.delete(null);
    }

    @Override
    public List<SysJobLogVo> getJobStatistics(Long jobId, Date startDate, Date endDate) {
        List<SysJobLog> logs = sysJobLogMapper.selectJobStatistics(jobId, startDate, endDate);
        List<SysJobLogVo> logVos = BeanCotyUtils.copyListProperties(logs, SysJobLogVo.class);

        // 设置状态描述
        logVos.forEach(this::setLogDescriptions);

        return logVos;
    }

    @Override
    public List<SysJobLogVo> getRecentLogs(Long jobId, Integer limit) {
        List<SysJobLog> logs = sysJobLogMapper.selectRecentLogs(jobId, limit);
        List<SysJobLogVo> logVos = BeanCotyUtils.copyListProperties(logs, SysJobLogVo.class);

        // 设置状态描述
        logVos.forEach(this::setLogDescriptions);

        return logVos;
    }

    @Override
    public Long recordJobStart(SysJobLog jobLog) {
        jobLog.setStartTime(new Date());
        // 执行中
        jobLog.setStatus(0);
        save(jobLog);
        return jobLog.getJobLogId();
    }

    @Override
    public void recordJobEnd(Long jobLogId, Integer status, String message, String exceptionInfo) {
        SysJobLog jobLog = getById(jobLogId);
        if (jobLog != null) {
            Date endTime = new Date();
            jobLog.setEndTime(endTime);
            jobLog.setStatus(status);
            jobLog.setJobMessage(message);
            jobLog.setExceptionInfo(exceptionInfo);

            // 计算执行时间
            if (jobLog.getStartTime() != null) {
                long executeTime = endTime.getTime() - jobLog.getStartTime().getTime();
                jobLog.setExecuteTime(executeTime);
            }

            updateById(jobLog);
        }
    }

    /**
     * 设置日志描述信息
     */
    private void setLogDescriptions(SysJobLogVo logVo) {
        // 设置状态描述
        if (Integer.valueOf(0).equals(logVo.getStatus())) {
            logVo.setStatusDesc("成功");
        } else if (Integer.valueOf(1).equals(logVo.getStatus())) {
            logVo.setStatusDesc("失败");
        } else {
            logVo.setStatusDesc("未知");
        }
    }
}
