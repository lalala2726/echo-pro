package cn.zhangchuangla.common.core.constants;

/**
 * 定时任务常量信息
 *
 * @author Chuang
 */
public class ScheduleConstants {

    public static final String TASK_CLASS_NAME = "TASK_CLASS_NAME";
    /**
     * 默认
     */
    public static final String MISFIRE_DEFAULT = "0";

    /**
     * 立即触发执行
     */
    public static final String MISFIRE_IGNORE_MISFIRES = "1";

    /**
     * 触发一次执行
     */
    public static final String MISFIRE_FIRE_AND_PROCEED = "2";

    /**
     * 不触发立即执行
     */
    public static final String MISFIRE_DO_NOTHING = "3";

    public static final String TASK_PROPERTIES = "TASK_PROPERTIES";

    /**
     * 默认组名
     */
    public static final String DEFAULT_GROUP = "DEFAULT";

    public enum Status {
        /**
         * 正常
         */
        NORMAL("0"),
        /**
         * 暂停
         */
        PAUSE("1");

        private String value;

        private Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
