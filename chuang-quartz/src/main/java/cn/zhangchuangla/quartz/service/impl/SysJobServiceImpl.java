package cn.zhangchuangla.quartz.service.impl;

import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.enums.MisfirePolicy;
import cn.zhangchuangla.quartz.enums.ScheduleType;
import cn.zhangchuangla.quartz.mapper.SysJobMapper;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobBatchRequest;
import cn.zhangchuangla.quartz.model.request.SysJobQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobVo;
import cn.zhangchuangla.quartz.service.SysJobService;
import cn.zhangchuangla.quartz.util.CronUtils;
import cn.zhangchuangla.quartz.util.JobInvokeUtil;
import cn.zhangchuangla.quartz.util.ScheduleUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务服务实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysJobServiceImpl extends ServiceImpl<SysJobMapper, SysJob> implements SysJobService {

    private final SysJobMapper sysJobMapper;
    private final Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理
     */
    @PostConstruct
    public void init() {
        initJobs();
    }

    @Override
    public Page<SysJob> selectJobList(SysJobQueryRequest request) {
        Page<SysJob> page = new Page<>(request.getPageNum(), request.getPageSize());

        return sysJobMapper.selectJobList(page, request);
    }

    @Override
    public SysJobVo selectJobById(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            return null;
        }

        SysJobVo jobVo = new SysJobVo();
        BeanUtils.copyProperties(job, jobVo);
        setJobDescriptions(jobVo);

        return jobVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addJob(SysJobAddRequest request) {
        // 验证参数
        validateJobRequest(request);

        // 检查任务名称是否存在
        if (checkJobNameExists(request.getJobName(), null)) {
            throw new ServiceException("任务名称已存在");
        }

        // 验证调用目标
        if (JobInvokeUtil.whiteList(request.getInvokeTarget())) {
            throw new ServiceException("调用目标不在白名单中");
        }

        SysJob job = new SysJob();
        BeanUtils.copyProperties(request, job);

        // 设置默认值
        setDefaultValues(job);

        // 保存到数据库
        boolean saved = save(job);
        if (saved) {
            try {
                // 创建定时任务
                ScheduleUtils.createScheduleJob(scheduler, job);
            } catch (SchedulerException e) {
                log.error("创建定时任务失败", e);
                throw new ServiceException("创建定时任务失败: " + e.getMessage());
            }
        }

        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateJob(SysJobUpdateRequest request) {
        // 验证参数
        validateJobRequest(request);

        // 检查任务是否存在
        SysJob existingJob = getById(request.getJobId());
        if (existingJob == null) {
            throw new ServiceException("任务不存在");
        }

        // 检查任务名称是否存在
        if (checkJobNameExists(request.getJobName(), request.getJobId())) {
            throw new ServiceException("任务名称已存在");
        }

        // 验证调用目标
        if (JobInvokeUtil.whiteList(request.getInvokeTarget())) {
            throw new ServiceException("调用目标不在白名单中");
        }

        SysJob job = new SysJob();
        BeanUtils.copyProperties(request, job);

        // 更新数据库
        boolean updated = updateById(job);
        if (updated) {
            try {
                // 更新定时任务
                updateSchedulerJob(job, existingJob.getJobGroup());
            } catch (SchedulerException e) {
                log.error("更新定时任务失败", e);
                throw new ServiceException("更新定时任务失败: " + e.getMessage());
            }
        }

        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteJobs(List<Long> jobIds) {
        for (Long jobId : jobIds) {
            SysJob job = getById(jobId);
            if (job != null) {
                try {
                    scheduler.deleteJob(ScheduleUtils.getJobKey(jobId, job.getJobGroup()));
                } catch (SchedulerException e) {
                    log.error("删除定时任务失败: {}", jobId, e);
                    throw new ServiceException("删除定时任务失败: " + e.getMessage());
                }
            }
        }

        return removeByIds(jobIds);
    }

    @Override
    public boolean startJob(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        try {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, job.getJobGroup()));

            // 更新数据库状态
            job.setStatus(QuartzConstants.JobStatusConstants.NORMAL);
            updateById(job);

            return true;
        } catch (SchedulerException e) {
            log.error("启动任务失败: {}", jobId, e);
            throw new ServiceException("启动任务失败: " + e.getMessage());
        }
    }

    @Override
    public boolean pauseJob(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        try {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, job.getJobGroup()));

            // 更新数据库状态
            job.setStatus(QuartzConstants.JobStatusConstants.PAUSE);
            updateById(job);

            return true;
        } catch (SchedulerException e) {
            log.error("暂停任务失败: {}", jobId, e);
            throw new ServiceException("暂停任务失败: " + e.getMessage());
        }
    }

    @Override
    public boolean resumeJob(Long jobId) {
        return startJob(jobId);
    }

    @Override
    public boolean runJob(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            throw new ServiceException("任务不存在");
        }

        try {
            scheduler.triggerJob(ScheduleUtils.getJobKey(jobId, job.getJobGroup()));
            return true;
        } catch (SchedulerException e) {
            log.error("执行任务失败: {}", jobId, e);
            throw new ServiceException("执行任务失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchOperateJobs(SysJobBatchRequest request) {
        String operation = request.getOperation();
        List<Long> jobIds = request.getJobIds();

        switch (operation) {
            case "start":
                jobIds.forEach(this::startJob);
                break;
            case "pause":
                jobIds.forEach(this::pauseJob);
                break;
            case "resume":
                jobIds.forEach(this::resumeJob);
                break;
            case "delete":
                return deleteJobs(jobIds);
            default:
                throw new ServiceException("不支持的操作类型: " + operation);
        }

        return true;
    }

    @Override
    public boolean checkJobNameExists(String jobName, Long jobId) {
        return sysJobMapper.checkJobNameExists(jobName, jobId) > 0;
    }

    @Override
    public List<SysJob> selectEnabledJobs() {
        return sysJobMapper.selectEnabledJobs();
    }

    @Override
    public List<SysJob> selectJobsByGroup(String jobGroup) {
        return sysJobMapper.selectJobsByGroup(jobGroup);
    }

    @Override
    public List<SysJob> checkJobDependencies(Long jobId) {
        return sysJobMapper.selectDependentJobs(jobId);
    }

    @Override
    public void initJobs() {
        try {
            scheduler.clear();
            List<SysJob> jobList = selectEnabledJobs();
            for (SysJob job : jobList) {
                ScheduleUtils.createScheduleJob(scheduler, job);
            }
            log.info("初始化定时任务完成，共加载 {} 个任务", jobList.size());
        } catch (SchedulerException e) {
            log.error("初始化定时任务失败", e);
        }
    }

    @Override
    public void refreshJobStatus(Long jobId) {
        SysJob job = getById(jobId);
        if (job == null) {
            return;
        }

        try {
            // 更新下次执行时间
            Trigger trigger = scheduler.getTrigger(ScheduleUtils.getTriggerKey(jobId, job.getJobGroup()));
            if (trigger != null) {
                job.setNextFireTime(trigger.getNextFireTime());
                job.setPreviousFireTime(trigger.getPreviousFireTime());
                updateById(job);
            }
        } catch (SchedulerException e) {
            log.error("刷新任务状态失败: {}", jobId, e);
        }
    }

    /**
     * 导出任务列表
     */
    @Override
    public List<SysJob> exportJobList(SysJobQueryRequest request) {
        return sysJobMapper.exportJobList(request);
    }

    /**
     * 验证任务请求参数
     */
    private void validateJobRequest(Object request) {
        if (request instanceof SysJobAddRequest addRequest) {
            validateScheduleType(addRequest.getScheduleType(), addRequest.getCronExpression(),
                    addRequest.getFixedRate(), addRequest.getFixedDelay());
        } else if (request instanceof SysJobUpdateRequest updateRequest) {
            validateScheduleType(updateRequest.getScheduleType(), updateRequest.getCronExpression(),
                    updateRequest.getFixedRate(), updateRequest.getFixedDelay());
        }
    }

    /**
     * 验证调度策略
     */
    private void validateScheduleType(Integer scheduleType, String cronExpression, Long fixedRate, Long fixedDelay) {
        ScheduleType type = ScheduleType.getByCode(scheduleType);
        if (type == null) {
            throw new ServiceException("不支持的调度策略");
        }

        switch (type) {
            case CRON:
                if (StringUtils.isEmpty(cronExpression)) {
                    throw new ServiceException("Cron表达式不能为空");
                }
                if (!CronUtils.isValid(cronExpression)) {
                    throw new ServiceException("Cron表达式格式错误");
                }
                break;
            case FIXED_RATE:
                if (fixedRate == null || fixedRate <= 0) {
                    throw new ServiceException("固定频率间隔必须大于0");
                }
                break;
            case FIXED_DELAY:
                if (fixedDelay == null || fixedDelay <= 0) {
                    throw new ServiceException("固定延迟间隔必须大于0");
                }
                break;
        }
    }

    /**
     * 设置默认值
     */
    private void setDefaultValues(SysJob job) {
        if (job.getStatus() == null) {
            job.setStatus(QuartzConstants.JobStatusConstants.PAUSE);
        }
        if (job.getConcurrent() == null) {
            job.setConcurrent(1);
        }
        if (job.getMisfirePolicy() == null) {
            job.setMisfirePolicy(QuartzConstants.MisfirePolicyConstants.DEFAULT);
        }
        if (job.getPriority() == null) {
            job.setPriority(5);
        }
        if (StringUtils.isEmpty(job.getJobGroup())) {
            job.setJobGroup(QuartzConstants.DEFAULT_GROUP);
        }
    }

    /**
     * 设置任务描述信息
     */
    private void setJobDescriptions(SysJobVo jobVo) {
        // 设置调度策略描述
        ScheduleType scheduleType = ScheduleType.getByCode(jobVo.getScheduleType());
        if (scheduleType != null) {
            jobVo.setScheduleTypeDesc(scheduleType.getDescription());
        }

        // 设置失火策略描述
        MisfirePolicy misfirePolicy = MisfirePolicy.getByCode(jobVo.getMisfirePolicy());
        if (misfirePolicy != null) {
            jobVo.setMisfirePolicyDesc(misfirePolicy.getDescription());
        }

        // 设置状态描述
        if (QuartzConstants.JobStatusConstants.NORMAL.equals(jobVo.getStatus())) {
            jobVo.setStatusDesc("正常");
        } else if (QuartzConstants.JobStatusConstants.PAUSE.equals(jobVo.getStatus())) {
            jobVo.setStatusDesc("暂停");
        }
    }

    /**
     * 更新调度器中的任务
     */
    private void updateSchedulerJob(SysJob job, String oldJobGroup) throws SchedulerException {
        Long jobId = job.getJobId();
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, oldJobGroup);

        // 判断是否存在
        if (scheduler.checkExists(jobKey)) {
            // 先移除
            scheduler.deleteJob(jobKey);
        }

        // 重新创建
        ScheduleUtils.createScheduleJob(scheduler, job);
    }
}
