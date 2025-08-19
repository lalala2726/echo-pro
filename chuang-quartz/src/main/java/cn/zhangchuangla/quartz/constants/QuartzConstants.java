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
     * 任务白名单配置（严格限制到具体的任务包）
     * 安全建议：只允许明确指定的任务包，避免使用过于宽泛的包名
     */
    public static final String[] JOB_WHITELIST_STR = {
        "cn.zhangchuangla.quartz.task",      // 定时任务包
        "cn.zhangchuangla.system.job",       // 系统任务包
        "cn.zhangchuangla.system.task"       // 系统定时任务包
    };

    /**
     * 任务违规的字符和类名
     * 防止执行危险操作和系统调用
     */
    public static final String[] JOB_ERROR_STR = {
        // 网络和文件系统访问
        "java.net.URL", "java.net.URLConnection", "java.net.Socket",
        "java.io.FileInputStream", "java.io.FileOutputStream", 
        "java.nio.file.Files", "java.nio.file.Paths",
        
        // JNDI和命名服务
        "javax.naming.InitialContext", "javax.naming.Context",
        
        // 序列化和反序列化
        "java.io.ObjectInputStream", "java.io.ObjectOutputStream",
        
        // YAML和XML解析（可能的XXE攻击）
        "org.yaml.snakeyaml", "javax.xml.parsers",
        
        // 系统调用和进程执行
        "java.lang.Runtime", "java.lang.ProcessBuilder",
        "java.lang.System.exit", "java.lang.System.setProperty",
        
        // 反射相关危险操作
        "java.lang.reflect.Method.invoke", "java.lang.Class.forName",
        
        // Spring容器访问（防止Bean操作）
        "org.springframework.context", "org.springframework.beans",
        
        // 脚本引擎
        "javax.script.ScriptEngine", "javax.script.ScriptEngineManager",
        
        // 危险的Apache组件
        "org.apache.commons.collections.functors",
        
        // 系统文件操作工具
        "cn.zhangchuangla.common.core.utils.file"
    };

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
