package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.service.SysLoginLogService;
import cn.zhangchuangla.system.service.SysOperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Spring异步服务
 * 使用@Async注解替换原有的AsyncManager和AsyncFactory
 *
 * @author Chuang
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {

    private final SysOperationLogService sysOperationLogService;
    private final SysLoginLogService sysLoginLogService;

    /**
     * 异步记录操作日志
     *
     * @param operLog 操作日志信息
     */
    @Async("logProcessExecutor")
    public void recordOperationLog(SysOperationLog operLog) {
        try {
            // 如果IP地址为空或者地区为空，尝试根据IP获取地区信息
            if (operLog.getOperationIp() != null && operLog.getOperationRegion() == null) {
                String region = IPUtils.getRegion(operLog.getOperationIp());
                operLog.setOperationRegion(region);
            }

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

    /**
     * 异步记录登录日志
     *
     * @param username  用户名
     * @param ipAddr    IP地址
     * @param userAgent 用户代理
     * @param isSuccess 是否登录成功
     */
    @Async("logProcessExecutor")
    public void recordLoginLog(String username, String ipAddr, String userAgent, boolean isSuccess) {
        final int loginSuccess = 0;
        final int loginFail = 1;
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
                    .status(isSuccess ? loginSuccess : loginFail)
                    .loginTime(new Date())
                    .createBy(Constants.SYSTEM_CREATE)
                    .build();

            // 保存登录日志
            sysLoginLogService.save(sysLoginLog);
        } catch (Exception e) {
            log.error("异步保存登录日志失败: {}", e.getMessage(), e);
        }
    }


}
