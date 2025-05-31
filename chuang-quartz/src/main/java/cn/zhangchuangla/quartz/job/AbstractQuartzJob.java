package cn.zhangchuangla.quartz.job;

import cn.zhangchuangla.common.core.constants.ScheduleConstants;
import cn.zhangchuangla.common.core.utils.SpringUtils;
import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.entity.SysJobLog;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author Chuang
 */
@Slf4j
public abstract class AbstractQuartzJob implements Job {

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<Date> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) {
        SysJob sysJob = new SysJob();
        // 获取任务对象
        Object jobDataMap = context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        if (jobDataMap instanceof SysJob) {
            sysJob = (SysJob) jobDataMap;
        }

        try {
            before();
            doExecute(sysJob.getInvokeTarget());
            after(sysJob, null);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(sysJob, e);
        }
    }

    /**
     * 执行前
     *
     */
    protected void before() {
        THREAD_LOCAL.set(new Date());
    }

    /**
     * 执行后
     *
     * @param sysJob  系统计划任务
     */
    protected void after(SysJob sysJob, Exception e) {
        Date startTime = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();

        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setCreateTime(startTime);

        // 任务执行时间
        long runTime = new Date().getTime() - startTime.getTime();
        sysJobLog.setJobMessage(sysJob.getJobName() + " 总共耗时：" + runTime + "毫秒");

        if (e != null) {
            sysJobLog.setStatus(QuartzConstants.JOB_LOG_STATUS_FAIL);
            String errorMsg = e.getMessage();
            // 异常信息不能超过2000字符
            if (errorMsg != null && errorMsg.length() > QuartzConstants.JOB_LOG_EXCEPTION_INFO_MAX_LENGTH) {
                errorMsg = errorMsg.substring(0, QuartzConstants.JOB_LOG_EXCEPTION_INFO_MAX_LENGTH);
            }
            sysJobLog.setExceptionInfo(errorMsg);
        } else {
            sysJobLog.setStatus(QuartzConstants.JOB_LOG_STATUS_SUCCESS);
        }

        // 写入数据库当中
        SpringUtils.getBean(SysJobLogService.class).save(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param invokeTarget 调用目标字符串
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(String invokeTarget) throws Exception;
}
