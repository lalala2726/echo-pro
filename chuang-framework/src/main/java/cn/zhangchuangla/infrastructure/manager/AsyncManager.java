package cn.zhangchuangla.infrastructure.manager;

import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 *
 * @author Chuang
 */
@Slf4j
public class AsyncManager {

    private static final AsyncManager ME = new AsyncManager();
    /**
     * 异步操作任务调度线程池
     */
    private final ScheduledExecutorService executor;

    /**
     * 单例模式，私有构造函数
     */
    private AsyncManager() {
        this.executor = new ScheduledThreadPoolExecutor(5, r -> {
            Thread t = new Thread(r);
            t.setName("async-manager-thread-");
            t.setDaemon(true); // 设置为守护线程，不阻止JVM退出
            return t;
        });
    }

    /**
     * 获取实例
     */
    public static AsyncManager me() {
        return ME;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task) {
        int OPERATE_DELAY_TIME = 10;
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            log.info("正在关闭异步任务线程池...");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    log.warn("异步任务线程池在60秒内未能正常关闭，已强制关闭");
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                log.error("关闭异步任务线程池时被中断", e);
            }
            log.info("异步任务线程池已关闭");
        }
    }
}
