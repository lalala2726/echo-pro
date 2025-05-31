package cn.zhangchuangla.quartz.constants;

/**
 * Quartz模块相关常量
 *
 * @author Chuang
 * <p>
 * created on 2025/5/29 18:26
 */
public class QuartzConstants {

    /**
     * 定时任务日志执行状态：成功
     */
    public static final String JOB_LOG_STATUS_SUCCESS = "0";

    /**
     * 定时任务日志执行状态：失败
     */
    public static final String JOB_LOG_STATUS_FAIL = "1";

    /**
     * 定时任务日志异常信息最大长度
     */
    public static final int JOB_LOG_EXCEPTION_INFO_MAX_LENGTH = 2000;

    /**
     * 定时任务是否并发执行：允许 ("0")
     */
    public static final String JOB_CONCURRENT_ALLOWED = "0";

    /**
     * 定时任务是否并发执行：禁止 ("1")
     */
    public static final String JOB_CONCURRENT_DISALLOWED = "1";

    /**
     * 定时任务调用目标白名单包前缀
     */
    public static final String[] INVOKE_TARGET_WHITELIST_PACKAGES = {"cn.zhangchuangla"};

}
