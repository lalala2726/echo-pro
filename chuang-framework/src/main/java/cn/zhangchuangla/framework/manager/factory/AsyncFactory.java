package cn.zhangchuangla.framework.manager.factory;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.SpringUtils;
import cn.zhangchuangla.common.utils.UserAgentUtils;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.service.SysLoginLogService;
import cn.zhangchuangla.system.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
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
     * 记录登录日志
     *
     * @param username  用户名
     * @param ipAddr    IP地址
     * @param userAgent 用户代理
     * @param isSuccess 是否登录成功
     * @return 任务Task
     */
    public static TimerTask recordLoginLog(final String username, final String ipAddr, final String userAgent, final boolean isSuccess) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    String region = IPUtils.getRegion(ipAddr);
                    String osName = UserAgentUtils.getOsName(userAgent);
                    String browserName = UserAgentUtils.getBrowserName(userAgent);

                    log.info("用户名: {}, 登录: {}, 登录时间: {}, 系统名称: {}, IP地址: {}, 浏览器名称: {}, 区域: {}",
                            username,
                            isSuccess ? "登录成功" : "登录失败",
                            new Date(),
                            osName,
                            ipAddr,
                            browserName,
                            region
                    );

                    // 构建登录日志对象
                    SysLoginLog sysLoginLog = SysLoginLog.builder()
                            .os(osName)
                            .ip(ipAddr)
                            .region(region)
                            .username(username)
                            .browser(browserName)
                            .status(isSuccess ? Constants.LOGIN_SUCCESS : Constants.LOGIN_FAIL) // 使用常量
                            .loginTime(new Date())
                            .createBy(Constants.SYSTEM_CREATE)
                            .build();

                    // 获取登录日志服务并保存
                    SysLoginLogService sysLoginLogService = SpringUtils.getBean(SysLoginLogService.class);
                    boolean result = sysLoginLogService.save(sysLoginLog);

                    if (result) {
                        log.debug("异步保存登录日志成功，用户: {}, 状态: {}", username, isSuccess ? "成功" : "失败");
                    } else {
                        log.warn("异步保存登录日志失败，用户: {}", username);
                    }
                } catch (Exception e) {
                    log.error("异步保存登录日志失败: {}", e.getMessage(), e);
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
