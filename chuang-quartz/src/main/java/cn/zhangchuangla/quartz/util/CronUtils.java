package cn.zhangchuangla.quartz.util;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * Cron表达式工具类
 *
 * @author Chuang
 */
public class CronUtils {

    private static final Logger logger = LoggerFactory.getLogger(CronUtils.class);

    /**
     * 验证Cron表达式是否有效
     *
     * @param cronExpression Cron表达式
     * @return 是否有效
     */
    public static boolean isValid(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 获取下次执行时间
     *
     * @param cronExpression Cron表达式
     * @return 下次执行时间
     */
    public static Date getNextExecution(String cronExpression) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date());
        } catch (ParseException e) {
            logger.error("解析Cron表达式失败: {}", cronExpression, e);
            return null;
        }
    }

    /**
     * 获取下次执行时间
     *
     * @param cronExpression Cron表达式
     * @param date           基准时间
     * @return 下次执行时间
     */
    public static Date getNextExecution(String cronExpression, Date date) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(date);
        } catch (ParseException e) {
            logger.error("解析Cron表达式失败: {}", cronExpression, e);
            return null;
        }
    }

    /**
     * 获取Cron表达式描述
     *
     * @param cronExpression Cron表达式
     * @return 描述信息
     */
    public static String getDescription(String cronExpression) {
        if (!isValid(cronExpression)) {
            return "无效的Cron表达式";
        }

        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date nextTime = cron.getNextValidTimeAfter(new Date());
            if (nextTime != null) {
                return "下次执行时间: " + nextTime;
            }
            return "无法计算下次执行时间";
        } catch (ParseException e) {
            logger.error("解析Cron表达式失败: {}", cronExpression, e);
            return "解析失败";
        }
    }

    /**
     * 检查Cron表达式是否会在指定时间范围内执行
     *
     * @param cronExpression Cron表达式
     * @param startTime      开始时间
     * @param endTime        结束时间
     * @return 是否会执行
     */
    public static boolean willExecuteInRange(String cronExpression, Date startTime, Date endTime) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            Date nextTime = cron.getNextValidTimeAfter(startTime);
            return nextTime != null && nextTime.before(endTime);
        } catch (ParseException e) {
            logger.error("解析Cron表达式失败: {}", cronExpression, e);
            return false;
        }
    }

}
