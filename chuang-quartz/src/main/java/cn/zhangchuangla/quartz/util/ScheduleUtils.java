package cn.zhangchuangla.quartz.util;

import cn.zhangchuangla.quartz.constants.QuartzConstants;
import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.enums.MisfirePolicy;
import cn.zhangchuangla.quartz.enums.ScheduleType;
import cn.zhangchuangla.quartz.job.QuartzDisallowConcurrentExecution;
import cn.zhangchuangla.quartz.job.QuartzJobExecution;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务调度工具类
 *
 * @author Chuang
 */
public class ScheduleUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleUtils.class);

    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(SysJob sysJob) {
        boolean isConcurrent = QuartzConstants.JobStatusConstants.NORMAL.equals(sysJob.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerBuilder<Trigger> getTriggerBuilder(SysJob job) {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(job.getJobId(), job.getJobGroup()))
                .withDescription(job.getDescription());

        // 设置开始时间
        if (job.getStartTime() != null) {
            triggerBuilder.startAt(job.getStartTime());
        } else {
            triggerBuilder.startNow();
        }

        // 设置结束时间
        if (job.getEndTime() != null) {
            triggerBuilder.endAt(job.getEndTime());
        }

        return triggerBuilder;
    }

    /**
     * 构建任务调度
     */
    public static Trigger getTrigger(SysJob job) {
        TriggerBuilder<Trigger> triggerBuilder = getTriggerBuilder(job);
        ScheduleType scheduleType = ScheduleType.getByCode(job.getScheduleType());

        if (scheduleType == null) {
            throw new IllegalArgumentException("不支持的调度策略: " + job.getScheduleType());
        }

        return switch (scheduleType) {
            case CRON -> buildCronTrigger(triggerBuilder, job);
            case FIXED_RATE -> buildFixedRateTrigger(triggerBuilder, job);
            case FIXED_DELAY -> buildFixedDelayTrigger(triggerBuilder, job);
            case ONCE -> buildOnceTrigger(triggerBuilder, job);
        };
    }

    /**
     * 构建Cron触发器
     */
    private static CronTrigger buildCronTrigger(TriggerBuilder<Trigger> triggerBuilder, SysJob job) {
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
        cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);
        return triggerBuilder.withSchedule(cronScheduleBuilder).build();
    }

    /**
     * 构建固定频率触发器
     */
    private static SimpleTrigger buildFixedRateTrigger(TriggerBuilder<Trigger> triggerBuilder, SysJob job) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(job.getFixedRate())
                .repeatForever();
        scheduleBuilder = handleSimpleScheduleMisfirePolicy(job, scheduleBuilder);
        return triggerBuilder.withSchedule(scheduleBuilder).build();
    }

    /**
     * 构建固定延迟触发器
     */
    private static SimpleTrigger buildFixedDelayTrigger(TriggerBuilder<Trigger> triggerBuilder, SysJob job) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInMilliseconds(job.getFixedDelay())
                .repeatForever();
        scheduleBuilder = handleSimpleScheduleMisfirePolicy(job, scheduleBuilder);
        return triggerBuilder.withSchedule(scheduleBuilder).build();
    }

    /**
     * 构建一次性触发器
     */
    private static SimpleTrigger buildOnceTrigger(TriggerBuilder<Trigger> triggerBuilder, SysJob job) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        if (job.getInitialDelay() != null && job.getInitialDelay() > 0) {
            triggerBuilder.startAt(new java.util.Date(System.currentTimeMillis() + job.getInitialDelay()));
        }
        return triggerBuilder.withSchedule(scheduleBuilder).build();
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(SysJob job, CronScheduleBuilder cb) {
        MisfirePolicy misfirePolicy = MisfirePolicy.getByCode(job.getMisfirePolicy());
        if (misfirePolicy == null) {
            return cb;
        }

        return switch (misfirePolicy) {
            case DEFAULT -> cb;
            case IGNORE_MISFIRES -> cb.withMisfireHandlingInstructionIgnoreMisfires();
            case FIRE_AND_PROCEED -> cb.withMisfireHandlingInstructionFireAndProceed();
            case DO_NOTHING -> cb.withMisfireHandlingInstructionDoNothing();
            default -> cb;
        };
    }

    /**
     * 设置简单任务策略
     */
    public static SimpleScheduleBuilder handleSimpleScheduleMisfirePolicy(SysJob job, SimpleScheduleBuilder ssb) {
        MisfirePolicy misfirePolicy = MisfirePolicy.getByCode(job.getMisfirePolicy());
        if (misfirePolicy == null) {
            return ssb;
        }

        return switch (misfirePolicy) {
            case DEFAULT -> ssb;
            case IGNORE_MISFIRES -> ssb.withMisfireHandlingInstructionIgnoreMisfires();
            case FIRE_AND_PROCEED -> ssb.withMisfireHandlingInstructionFireNow();
            case DO_NOTHING -> ssb.withMisfireHandlingInstructionNextWithExistingCount();
            default -> ssb;
        };
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, SysJob job) throws SchedulerException {
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(QuartzConstants.TASK_PROPERTIES, job);

        // 构建新任务的触发器
        Trigger trigger = getTrigger(job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup))) {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }

        // 判断任务是否过期
        if (trigger.getStartTime() != null && trigger.getStartTime().before(new java.util.Date())) {
            // 如果任务已过期，根据失火策略处理
            logger.warn("任务 [{}] 开始时间已过期，开始时间: {}", job.getJobName(), trigger.getStartTime());
        }

        scheduler.scheduleJob(jobDetail, trigger);

        // 暂停任务
        if (job.getStatus().equals(QuartzConstants.JobStatusConstants.PAUSE)) {
            scheduler.pauseJob(getJobKey(jobId, jobGroup));
        }
    }

    /**
     * 构造任务键对象
     */
    public static JobKey getJobKey(Long jobId) {
        return JobKey.jobKey(QuartzConstants.TASK_PROPERTIES + "_" + jobId);
    }

    /**
     * 构造任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup) {
        return JobKey.jobKey(QuartzConstants.TASK_PROPERTIES + "_" + jobId, jobGroup);
    }

    /**
     * 构造任务触发器键对象
     */
    public static TriggerKey getTriggerKey(Long jobId) {
        return TriggerKey.triggerKey(QuartzConstants.TASK_PROPERTIES + "_" + jobId);
    }

    /**
     * 构造任务触发器键对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup) {
        return TriggerKey.triggerKey(QuartzConstants.TASK_PROPERTIES + "_" + jobId, jobGroup);
    }
}
