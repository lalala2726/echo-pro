package cn.zhangchuangla.framework.manager.factory;

import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.SpringUtils;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;

import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author Chuang
 */
@Slf4j
public class AsyncFactory {

    /**
     * 记录操作日志
     *
     * @param operLog 操作日志信息
     * @return 任务Task
     */
    public static TimerTask recordOperationLog(final SysOperationLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    // 如果IP地址为空或者地区为空，尝试根据IP获取地区信息
                    if (operLog.getOperationIp() != null && operLog.getOperationRegion() == null) {
                        String region = IPUtils.getRegion(operLog.getOperationIp());
                        operLog.setOperationRegion(region);
                    }

                    // 通过SpringUtils获取SysOperationLogService的Bean实例
                    SysOperationLogService sysOperationLogService = SpringUtils.getBean(SysOperationLogService.class);
                    // 保存日志
                    boolean result = sysOperationLogService.save(operLog);
                    if (result) {
                        log.debug("异步保存操作日志成功，ID: {}, 操作: {}", operLog.getId(), operLog.getModule());
                    } else {
                        log.warn("异步保存操作日志失败，操作: {}", operLog.getModule());
                    }
                } catch (Exception e) {
                    log.error("异步保存操作日志失败: {}", e.getMessage(), e);
                }
            }
        };
    }

    /**
     * 清空操作日志（特殊处理，不记录日志）
     *
     * @return 任务Task
     */
    public static TimerTask cleanOperationLog() {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    // 通过SpringUtils获取SysOperationLogService的Bean实例
                    SysOperationLogService sysOperationLogService = SpringUtils.getBean(SysOperationLogService.class);
                    // 清空日志
                    boolean result = sysOperationLogService.cleanOperationLog();
                    if (result) {
                        log.info("异步清空操作日志成功");
                    } else {
                        log.warn("异步清空操作日志失败");
                    }
                } catch (Exception e) {
                    log.error("异步清空操作日志失败: {}", e.getMessage(), e);
                }
            }
        };
    }
}
