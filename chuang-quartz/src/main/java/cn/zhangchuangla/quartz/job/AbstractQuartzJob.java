package cn.zhangchuangla.quartz.job;

import cn.zhangchuangla.common.core.utils.SpringUtils;
import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.entity.SysJobLog;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
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
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysJob sysJob) {
        THREAD_LOCAL.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, SysJob sysJob, Exception e) {
        Date startTime = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();

        final SysJobLog sysJobLog = new SysJobLog();
        sysJobLog.setJobId(sysJob.getJobId());
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setJobData(sysJob.getJobData());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setEndTime(new Date());
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

        // 写入数据库当中
        SpringUtils.getBean(SysJobLogService.class).addJobLog(sysJobLog);
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
