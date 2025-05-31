package cn.zhangchuangla.quartz.job;

import org.quartz.DisallowConcurrentExecution;

/**
 * 定时任务处理（禁止并发执行）
 *
 * @author Chuang
 */
@DisallowConcurrentExecution
public class QuartzDisallowConcurrentExecution extends AbstractQuartzJob {
    @Override
    protected void doExecute(String invokeTarget) throws Exception {
        JobInvokeUtil.invokeMethod(invokeTarget);
    }
}
