package cn.zhangchuangla.quartz.job;

import org.quartz.JobExecutionContext;

/**
 * 定时任务处理（允许并发执行）
 *
 * @author Chuang
 */
public class QuartzJobExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(String invokeTarget) throws Exception {
        JobInvokeUtil.invokeMethod(invokeTarget);
    }
} 