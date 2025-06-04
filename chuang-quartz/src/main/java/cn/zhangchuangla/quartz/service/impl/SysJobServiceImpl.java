package cn.zhangchuangla.quartz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.db.Page;
import cn.zhangchuangla.common.core.constants.ScheduleConstants;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.mapper.SysJobMapper;
import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobListQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.service.SysJobService;
import cn.zhangchuangla.quartz.util.CronUtils;
import cn.zhangchuangla.quartz.util.ScheduleUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob>
        implements SysJobService {

    private final SysJobMapper sysJobMapper;

    private final Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理
     */
    @PostConstruct
    public void init() throws Exception {
        // scheduler.clear(); // FIXME: 非常危险! 会清除所有持久化的任务。应移除或改为更细致的同步逻辑。
        List<SysJob> jobList = list();
        for (SysJob job : jobList) {
            // TODO: 此处可以增加逻辑，检查任务是否已在scheduler中，避免重复创建或处理状态不一致的情况
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Override
    public List<SysJob> listJobs(SysJobListQueryRequest request) {
        Page page = new Page(request.getPageNum(), request.getPageSize());
        return sysJobMapper.listJobs(page, request);
    }

    @Override
    public SysJob getJobById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addJob(SysJobAddRequest request) {
        // 验证CRON表达式
        if (!CronUtils.isValid(request.getCronExpression())) {
            throw new ServiceException("新增任务'" + request.getJobName() + "'失败，Cron表达式不正确");
        }

        // 验证调用目标白名单
        if (ScheduleUtils.whiteList(request.getInvokeTarget())) {
            throw new ServiceException("新增任务'" + request.getJobName() + "'失败，目标字符串不在白名单内");
        }

        SysJob sysJob = new SysJob();
        BeanUtil.copyProperties(request, sysJob);

        // 设置默认值
        if (sysJob.getJobGroup() == null) {
            sysJob.setJobGroup(ScheduleConstants.DEFAULT_GROUP);
        }
        if (sysJob.getMisfirePolicy() == null) {
            sysJob.setMisfirePolicy(ScheduleConstants.MISFIRE_DEFAULT);
        }
        if (sysJob.getConcurrent() == null) {
            sysJob.setConcurrent(QuartzConstants.JOB_CONCURRENT_DISALLOWED);
        }
        if (sysJob.getStatus() == null) {
            sysJob.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        }

        sysJob.setCreateTime(new Date());

        boolean result = save(sysJob);

        if (result) {
            try {
                ScheduleUtils.createScheduleJob(scheduler, sysJob);
            } catch (Exception e) {
                log.error("创建定时任务失败", e);
                throw new ServiceException("创建定时任务失败");
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(SysJobUpdateRequest request) {
        // 验证CRON表达式
        if (!CronUtils.isValid(request.getCronExpression())) {
            throw new ServiceException("修改任务'" + request.getJobName() + "'失败，Cron表达式不正确");
        }

        // 验证调用目标白名单
        if (ScheduleUtils.whiteList(request.getInvokeTarget())) {
            throw new ServiceException("修改任务'" + request.getJobName() + "'失败，目标字符串不在白名单内");
        }

        SysJob sysJob = new SysJob();
        BeanUtil.copyProperties(request, sysJob);
        sysJob.setUpdateTime(new Date());

        boolean result = updateById(sysJob);

        if (result) {
            try {
                updateSchedulerJob(sysJob, sysJob.getJobGroup());
            } catch (Exception e) {
                log.error("更新定时任务失败", e);
                throw new ServiceException("更新定时任务失败");
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJob(List<Long> ids) {
        for (Long jobId : ids) {
            SysJob job = getById(jobId);
            if (job != null) {
                try {
                    scheduler.deleteJob(ScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
                } catch (SchedulerException e) {
                    log.error("删除定时任务失败", e);
                    throw new ServiceException("删除定时任务失败");
                }
            }
        }
        return removeBatchByIds(ids);
    }

    @Override
    public boolean runJob(Long id) {
        SysJob job = getById(id);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        try {
            // 参数
            JobDataMap dataMap = new JobDataMap();
            dataMap.put(ScheduleConstants.TASK_PROPERTIES, job);
            JobKey jobKey = ScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup());
            scheduler.triggerJob(jobKey, dataMap);
            return true;
        } catch (SchedulerException e) {
            log.error("执行定时任务失败", e);
            throw new ServiceException("执行定时任务失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pauseJob(Long id) {
        SysJob job = getById(id);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        try {
            int rows = doPauseJob(job);
            return rows > 0;
        } catch (SchedulerException e) {
            log.error("暂停定时任务失败", e);
            throw new ServiceException("暂停定时任务失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resumeJob(Long id) {
        SysJob job = getById(id);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        try {
            int rows = doResumeJob(job);
            return rows > 0;
        } catch (SchedulerException e) {
            log.error("恢复定时任务失败", e);
            throw new ServiceException("恢复定时任务失败");
        }
    }

    /**
     * 更新任务
     *
     * @param job      任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(SysJob job, String jobGroup) throws Exception {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            // 先移除，然后做新增操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    public int doPauseJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        int rows = updateById(job) ? 1 : 0;
        if (rows > 0) {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    public int doResumeJob(SysJob job) throws SchedulerException {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = updateById(job) ? 1 : 0;
        if (rows > 0) {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return rows;
    }
}




