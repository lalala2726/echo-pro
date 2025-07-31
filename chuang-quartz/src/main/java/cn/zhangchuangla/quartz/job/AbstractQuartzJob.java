package cn.zhangchuangla.quartz.job;

import cn.zhangchuangla.common.core.utils.SpringUtils;
import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.entity.SysJobLog;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import cn.zhangchuangla.quartz.service.SysJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author Chuang
 */
public abstract class AbstractQuartzJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<Date> THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) {
        SysJob sysJob = (SysJob) context.getMergedJobDataMap().get(QuartzConstants.TASK_PROPERTIES);
        try {
            before(context, sysJob);
            doExecute(context, sysJob);
            after(context, sysJob, null);
        } catch (Exception e) {
            logger.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     * <p>
     * 在任务执行前进行准备工作：
     * 1. 记录任务开始执行时间
     * 2. 更新数据库中的上次执行时间和下次执行时间
     * </p>
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        Date startTime = new Date();
        THREAD_LOCAL.set(startTime);

        try {
            // 获取触发器信息
            Trigger trigger = context.getTrigger();
            Date previousFireTime = trigger.getPreviousFireTime();
            Date nextFireTime = trigger.getNextFireTime();

            // 更新数据库中的执行时间
            // 注意：这里的 previousFireTime 实际上是当前执行时间，因为触发器已经触发
            // 我们使用当前时间作为上次执行时间
            SysJobService jobService = SpringUtils.getBean(SysJobService.class);
            jobService.updateJobExecutionTime(sysJob.getJobId(), startTime, nextFireTime);

            logger.debug("任务执行前时间更新完成: jobId={}, previousFireTime={}, nextFireTime={}",
                    sysJob.getJobId(), startTime, nextFireTime);
        } catch (Exception e) {
            logger.warn("更新任务执行时间失败: jobId={}", sysJob.getJobId(), e);
        }
    }

    /**
     * 执行后
     * <p>
     * 在任务执行完成后进行收尾工作：
     * 1. 记录任务执行日志
     * 2. 更新下次执行时间（确保时间同步）
     * 3. 清理线程本地变量
     * </p>
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @param e       执行异常（如果有）
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        Date startTime = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        Date endTime = new Date();

        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobId(sysJob.getJobId());
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setJobData(sysJob.getJobData());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setEndTime(endTime);
        long runMs = sysJobLog.getEndTime().getTime() - sysJobLog.getStartTime().getTime();
        sysJobLog.setExecuteTime(runMs);

        // 设置服务器信息
        try {
            InetAddress addr = InetAddress.getLocalHost();
            sysJobLog.setServerIp(addr.getHostAddress());
            sysJobLog.setServerName(addr.getHostName());
        } catch (Exception ex) {
            logger.warn("获取服务器信息失败", ex);
        }

        if (e != null) {
            sysJobLog.setStatus(1);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 2000) {
                errorMsg = errorMsg.substring(0, 2000);
            }
            sysJobLog.setExceptionInfo(errorMsg);
            sysJobLog.setJobMessage("执行失败");
        } else {
            sysJobLog.setStatus(0);
            sysJobLog.setJobMessage("执行成功");
        }

        // 写入执行日志到数据库
        SpringUtils.getBean(SysJobLogService.class).addJobLog(sysJobLog);

        // 任务执行完成后，更新下次执行时间
        // 这确保了即使在高并发情况下，数据库中的时间信息也是准确的
        try {
            Trigger trigger = context.getTrigger();
            Date nextFireTime = trigger.getNextFireTime();

            // 只有当下次执行时间存在时才更新（避免一次性任务的空指针）
            if (nextFireTime != null) {
                SysJobService jobService = SpringUtils.getBean(SysJobService.class);
                jobService.updateJobExecutionTime(sysJob.getJobId(), startTime, nextFireTime);

                logger.debug("任务执行后时间更新完成: jobId={}, nextFireTime={}",
                        sysJob.getJobId(), nextFireTime);
            } else {
                logger.debug("任务无下次执行时间，可能是一次性任务: jobId={}", sysJob.getJobId());
            }
        } catch (Exception ex) {
            logger.warn("任务执行后更新下次执行时间失败: jobId={}", sysJob.getJobId(), ex);
        }
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception;
}
