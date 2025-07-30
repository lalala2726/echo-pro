package cn.zhangchuangla.quartz.constants;

/**
 * Quartz常量类
 *
 * @author Chuang
 */
public class QuartzConstants {

    /**
     * 任务调度参数key
     */
    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /**
     * 默认任务组名
     */
    public static final String DEFAULT_GROUP = "DEFAULT";

    /**
     * 任务白名单配置（可以配置包名，如：cn.zhangchuangla.quartz.task）
     */
    public static final String[] JOB_WHITELIST_STR = {"cn.zhangchuangla"};

    /**
     * 任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = {"java.net.URL", "javax.naming.InitialContext",
            "org.yaml.snakeyaml", "org.springframework", "org.apache", "cn.zhangchuangla.common.core.utils.file"};

    /**
     * 任务状态
     */
    public static class JobStatusConstants {
        /**
         * 正常
         */
        public static final Integer NORMAL = 0;

        /**
         * 暂停
         */
        public static final Integer PAUSE = 1;
    }

    /**
     * 调度策略
     */
    public static class ScheduleTypeConstants {
        /**
         * Cron表达式
         */
        public static final Integer CRON = 0;

        /**
         * 固定频率
         */
        public static final Integer FIXED_RATE = 1;

        /**
         * 固定延迟
         */
        public static final Integer FIXED_DELAY = 2;

        /**
         * 一次性执行
         */
        public static final Integer ONCE = 3;
    }

    /**
     * 失火策略
     */
    public static class MisfirePolicyConstants {
        /**
         * 默认策略
         */
        public static final Integer DEFAULT = 0;

        /**
         * 立即执行
         */
        public static final Integer IGNORE_MISFIRES = 1;

        /**
         * 执行一次
         */
        public static final Integer FIRE_AND_PROCEED = 2;

        /**
         * 放弃执行
         */
        public static final Integer DO_NOTHING = 3;
    }
}
