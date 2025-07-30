package cn.zhangchuangla.quartz.util;

import cn.zhangchuangla.quartz.entity.SysJob;
import org.junit.jupiter.api.Test;
import org.quartz.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 调度工具类测试
 *
 * @author Chuang
 */
public class ScheduleUtilsTest {

    @Test
    public void testGetJobKey() {
        Long jobId = 1L;
        String jobGroup = "TEST_GROUP";

        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);

        assertNotNull(jobKey);
        assertEquals("TASK_PROPERTIES_1", jobKey.getName());
        assertEquals("TEST_GROUP", jobKey.getGroup());
    }

    @Test
    public void testGetTriggerKey() {
        Long jobId = 1L;
        String jobGroup = "TEST_GROUP";

        TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobId, jobGroup);

        assertNotNull(triggerKey);
        assertEquals("TASK_PROPERTIES_1", triggerKey.getName());
        assertEquals("TEST_GROUP", triggerKey.getGroup());
    }

    @Test
    public void testGetTriggerBuilder() {
        SysJob job = new SysJob();
        job.setJobId(1L);
        job.setJobGroup("TEST_GROUP");
        job.setDescription("Test job description");

        TriggerBuilder<Trigger> triggerBuilder = ScheduleUtils.getTriggerBuilder(job);

        assertNotNull(triggerBuilder);

        // Build the trigger to verify it works
        Trigger trigger = triggerBuilder.build();
        assertNotNull(trigger);
        assertEquals("TASK_PROPERTIES_1", trigger.getKey().getName());
        assertEquals("TEST_GROUP", trigger.getKey().getGroup());
        assertEquals("Test job description", trigger.getDescription());
    }

    @Test
    public void testGetTriggerWithCronSchedule() {
        SysJob job = new SysJob();
        job.setJobId(1L);
        job.setJobGroup("TEST_GROUP");
        job.setDescription("Test cron job");
        job.setScheduleType(0); // Cron expression
        job.setCronExpression("0 */5 * * * ?");
        job.setMisfirePolicy(0); // Default policy

        Trigger trigger = ScheduleUtils.getTrigger(job);

        assertNotNull(trigger);
        assertTrue(trigger instanceof CronTrigger);
        assertEquals("TASK_PROPERTIES_1", trigger.getKey().getName());
        assertEquals("TEST_GROUP", trigger.getKey().getGroup());
    }

    @Test
    public void testGetTriggerWithFixedRate() {
        SysJob job = new SysJob();
        job.setJobId(2L);
        job.setJobGroup("TEST_GROUP");
        job.setDescription("Test fixed rate job");
        job.setScheduleType(1); // Fixed rate
        job.setFixedRate(60000L); // 1 minute
        job.setMisfirePolicy(0); // Default policy

        Trigger trigger = ScheduleUtils.getTrigger(job);

        assertNotNull(trigger);
        assertTrue(trigger instanceof SimpleTrigger);
        assertEquals("TASK_PROPERTIES_2", trigger.getKey().getName());
        assertEquals("TEST_GROUP", trigger.getKey().getGroup());
    }

    @Test
    public void testCronUtilsValidation() {
        // Test valid cron expression
        assertTrue(CronUtils.isValid("0 */5 * * * ?"));

        // Test invalid cron expression
        assertFalse(CronUtils.isValid("invalid cron"));

        // Test getting next execution time
        assertNotNull(CronUtils.getNextExecution("0 */5 * * * ?"));
    }
}
