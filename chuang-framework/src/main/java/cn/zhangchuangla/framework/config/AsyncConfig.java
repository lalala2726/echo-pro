package cn.zhangchuangla.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Spring异步任务配置
 * 替换原有的AsyncManager，使用Spring标准的@Async机制
 *
 * @author Chuang
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    // ==================== 通用任务执行器配置常量 ====================

    /**
     * 默认任务执行器核心线程数 - 保持少量常驻线程处理基本任务
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 5;

    /**
     * 默认任务执行器最大线程数 - 处理高峰期任务
     */
    private static final int DEFAULT_MAX_POOL_SIZE = 20;

    /**
     * 默认任务执行器队列容量 - 缓存待处理任务
     */
    private static final int DEFAULT_QUEUE_CAPACITY = 200;

    /**
     * 默认线程空闲时间(秒) - 空闲线程存活时间
     */
    private static final int DEFAULT_KEEP_ALIVE_SECONDS = 60;

    /**
     * 默认关闭等待时间(秒) - 应用关闭时等待任务完成的最大时间
     */
    private static final int DEFAULT_AWAIT_TERMINATION_SECONDS = 60;

    // ==================== 图片处理执行器配置常量 ====================

    /**
     * 图片处理最小核心线程数 - 确保至少有基本处理能力
     */
    private static final int IMAGE_MIN_CORE_POOL_SIZE = 2;

    /**
     * 图片处理队列容量 - CPU密集型任务不宜积压过多
     */
    private static final int IMAGE_QUEUE_CAPACITY = 50;

    // ==================== 日志处理执行器配置常量 ====================

    /**
     * 日志处理核心线程数 - IO密集型任务保持适中线程数
     */
    private static final int LOG_CORE_POOL_SIZE = 3;

    /**
     * 日志处理最大线程数 - 日志处理峰值线程数
     */
    private static final int LOG_MAX_POOL_SIZE = 10;

    /**
     * 日志处理队列容量 - 日志任务可以有较大缓存队列
     */
    private static final int LOG_QUEUE_CAPACITY = 500;

    /**
     * 默认异步任务执行器
     * 用于@Async注解标记的方法
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 核心线程数
        executor.setCorePoolSize(DEFAULT_CORE_POOL_SIZE);
        // 最大线程数
        executor.setMaxPoolSize(DEFAULT_MAX_POOL_SIZE);
        // 队列容量
        executor.setQueueCapacity(DEFAULT_QUEUE_CAPACITY);
        // 线程空闲时间
        executor.setKeepAliveSeconds(DEFAULT_KEEP_ALIVE_SECONDS);
        // 线程名前缀
        executor.setThreadNamePrefix("async-task-");

        // 拒绝策略：由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时间
        executor.setAwaitTerminationSeconds(DEFAULT_AWAIT_TERMINATION_SECONDS);

        executor.initialize();
        log.info("Spring异步任务线程池初始化完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());

        return executor;
    }

    /**
     * 图片处理专用异步执行器
     * 用于图片压缩等CPU密集型任务
     */
    @Bean("imageProcessExecutor")
    public Executor imageProcessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 根据CPU核心数设置线程数
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(Math.max(IMAGE_MIN_CORE_POOL_SIZE, processors / 2));
        executor.setMaxPoolSize(processors);
        executor.setQueueCapacity(IMAGE_QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(DEFAULT_KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix("image-process-");

        // 拒绝策略：由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(DEFAULT_AWAIT_TERMINATION_SECONDS);

        executor.initialize();
        log.info("图片处理异步线程池初始化完成 - 核心线程数: {}, 最大线程数: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());

        return executor;
    }

    /**
     * 日志处理专用异步执行器
     * 用于操作日志、登录日志等IO密集型任务
     */
    @Bean("logProcessExecutor")
    public Executor logProcessExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(LOG_CORE_POOL_SIZE);
        executor.setMaxPoolSize(LOG_MAX_POOL_SIZE);
        executor.setQueueCapacity(LOG_QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(DEFAULT_KEEP_ALIVE_SECONDS);
        executor.setThreadNamePrefix("log-process-");

        // 拒绝策略：由调用线程执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(DEFAULT_AWAIT_TERMINATION_SECONDS);

        executor.initialize();
        log.info("日志处理异步线程池初始化完成 - 核心线程数: {}, 最大线程数: {}",
                executor.getCorePoolSize(), executor.getMaxPoolSize());

        return executor;
    }
}
