package cn.zhangchuangla.quartz.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 示例定时任务
 *
 * @author Chuang
 */
@Component("sampleTask")
@Slf4j
public class SampleTask {

    /**
     * 无参数任务
     */
    public void noParams() {
        log.info("执行无参数任务");
    }

    /**
     * 有参数任务
     */
    public void withParams(String param1, Integer param2) {
        log.info("执行有参数任务，参数1: {}, 参数2: {}", param1, param2);
    }

    /**
     * 模拟长时间运行的任务
     */
    public void longRunningTask() {
        log.info("开始执行长时间运行任务");
        try {
            // 模拟5秒的处理时间
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("任务被中断", e);
        }
        log.info("长时间运行任务执行完成");
    }

    /**
     * 模拟可能失败的任务
     */
    public void mayFailTask(Boolean shouldFail) {
        log.info("执行可能失败的任务，shouldFail: {}", shouldFail);
        if (Boolean.TRUE.equals(shouldFail)) {
            throw new RuntimeException("模拟任务执行失败");
        }
        log.info("任务执行成功");
    }

    /**
     * 数据处理任务示例
     */
    public void dataProcessTask() {
        log.info("开始数据处理任务");
        // 模拟数据处理逻辑
        for (int i = 1; i <= 10; i++) {
            log.info("处理数据项 {}/10", i);
            try {
                // 模拟处理时间
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("数据处理被中断", e);
                return;
            }
        }
        log.info("数据处理任务完成");
    }

    /**
     * 系统清理任务示例
     */
    public void systemCleanupTask() {
        log.info("开始系统清理任务");
        // 模拟清理逻辑
        log.info("清理临时文件...");
        log.info("清理过期缓存...");
        log.info("清理日志文件...");
        log.info("系统清理任务完成");
    }
}
