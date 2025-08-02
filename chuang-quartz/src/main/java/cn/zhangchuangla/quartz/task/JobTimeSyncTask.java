package cn.zhangchuangla.quartz.task;

import cn.zhangchuangla.quartz.service.SysJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务执行时间同步任务
 * <p>
 * 定期同步数据库中的任务执行时间与 Quartz 调度器中的时间信息
 * 确保在各种异常情况下，数据库中的时间信息都能保持准确
 * </p>
 *
 * <p>
 * 同步策略：
 * 1. 每5分钟执行一次同步，确保时间信息的实时性
 * 2. 只同步启用状态的任务，避免不必要的开销
 * 3. 异常情况下不影响正常的任务调度
 * </p>
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobTimeSyncTask {

    private final SysJobService sysJobService;

    /**
     * 定期同步任务执行时间
     * <p>
     * 每5分钟执行一次，将 Quartz 调度器中的时间信息同步到数据库
     * 这确保了即使在系统重启、异常恢复等情况下，时间信息也能保持准确
     * </p>
     *
     * <p>
     * 执行时机：
     * - 系统正常运行期间：每5分钟同步一次
     * - 避开系统高峰期：在分钟的第30秒执行，错开整点时间
     * - 容错处理：同步失败不影响正常的任务调度
     * </p>
     */
    @Scheduled(cron = "30 */5 * * * ?") // 每5分钟的第30秒执行
    public void syncJobExecutionTimes() {
        try {
            log.debug("开始执行定时任务时间同步");

            long startTime = System.currentTimeMillis();
            int updateCount = sysJobService.batchUpdateJobExecutionTimes();
            long endTime = System.currentTimeMillis();

            if (updateCount > 0) {
                log.info("定时任务时间同步完成: 更新了 {} 个任务，耗时 {} ms",
                        updateCount, (endTime - startTime));
            } else {
                log.debug("定时任务时间同步完成: 无需更新任务，耗时 {} ms",
                        (endTime - startTime));
            }

        } catch (Exception e) {
            log.error("定时任务时间同步失败", e);
        }
    }

    /**
     * 系统启动后延迟同步
     * <p>
     * 在系统启动后30秒执行一次同步，确保启动过程中的时间信息准确
     * 这是对 initJobs 方法中异步更新的补充，提供双重保障
     * </p>
     */
    @Scheduled(initialDelay = 30000, fixedDelay = Long.MAX_VALUE) // 启动后30秒执行一次
    public void initialSyncJobExecutionTimes() {
        try {
            log.info("执行系统启动后的任务时间同步");

            long startTime = System.currentTimeMillis();
            int updateCount = sysJobService.batchUpdateJobExecutionTimes();
            long endTime = System.currentTimeMillis();

            log.info("系统启动后任务时间同步完成: 更新了 {} 个任务，耗时 {} ms",
                    updateCount, (endTime - startTime));

        } catch (Exception e) {
            log.error("系统启动后任务时间同步失败", e);
        }
    }

    /**
     * 每日凌晨全量同步
     * <p>
     * 每天凌晨2点执行一次全量同步，作为兜底机制
     * 确保长期运行的系统中，时间信息的准确性
     * </p>
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void dailySyncJobExecutionTimes() {
        try {
            log.info("开始执行每日全量任务时间同步");

            long startTime = System.currentTimeMillis();
            int updateCount = sysJobService.batchUpdateJobExecutionTimes();
            long endTime = System.currentTimeMillis();

            log.info("每日全量任务时间同步完成: 更新了 {} 个任务，耗时 {} ms",
                    updateCount, (endTime - startTime));

        } catch (Exception e) {
            log.error("每日全量任务时间同步失败", e);
        }
    }
}
