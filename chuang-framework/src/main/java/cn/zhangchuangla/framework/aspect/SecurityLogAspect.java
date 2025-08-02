package cn.zhangchuangla.framework.aspect;

import cn.zhangchuangla.common.core.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.framework.annotation.SecurityLog;
import cn.zhangchuangla.framework.async.AsyncLogService;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 安全日志记录处理
 * <p>
 * 通过AOP切面实现对Controller层加了 @SecurityLog 注解的方法进行拦截，
 * 专门用于记录用户的敏感安全操作，如权限变更、密码修改、重要数据操作等。
 * 与普通操作日志不同，安全日志更关注安全相关的操作记录，便于安全审计和问题追踪。
 * </p>
 *
 * <p>功能特性：</p>
 * <ul>
 *   <li>自动记录安全操作的基本信息（用户、IP、时间等）</li>
 *   <li>支持异步日志记录，不影响业务性能</li>
 *   <li>记录操作成功和失败的情况</li>
 * </ul>
 *
 * @author Chuang
 */
@Aspect
@Component
@Slf4j
public class SecurityLogAspect {

    /**
     * 线程变量，记录方法执行的开始时间，用于计算方法耗时
     */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("Security Log Cost Time");

    /**
     * 异步日志服务，用于异步处理安全日志记录
     */
    private final AsyncLogService asyncLogService;

    public SecurityLogAspect(AsyncLogService asyncLogService) {
        this.asyncLogService = asyncLogService;
    }

    /**
     * 在方法执行前记录开始时间
     *
     * @param joinPoint   切点，表示被拦截的方法
     * @param securityLog @SecurityLog 注解对象，包含安全日志记录的配置信息
     */
    @Before(value = "@annotation(securityLog)")
    public void doBefore(JoinPoint joinPoint, SecurityLog securityLog) {
        // 记录当前方法开始执行的时间
        TIME_THREADLOCAL.set(System.currentTimeMillis());
        log.debug("开始记录安全日志，方法: {}, 模块: {}",
                joinPoint.getSignature().getName(), securityLog.title());
    }

    /**
     * 在方法执行完成后（成功）记录安全日志
     *
     * @param joinPoint   切点，表示被拦截的方法
     * @param securityLog @SecurityLog 注解对象，包含安全日志记录的配置信息
     * @param jsonResult  方法返回的结果
     */
    @AfterReturning(pointcut = "@annotation(securityLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, SecurityLog securityLog, Object jsonResult) {
        // 处理成功的安全日志
        handleSecurityLog(joinPoint, securityLog, null, jsonResult);
    }

    /**
     * 在方法执行出现异常后记录安全日志
     *
     * @param joinPoint   切点，表示被拦截的方法
     * @param securityLog @SecurityLog 注解对象，包含安全日志记录的配置信息
     * @param exception   方法执行时抛出的异常
     */
    @AfterThrowing(pointcut = "@annotation(securityLog)", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, SecurityLog securityLog, Exception exception) {
        // 处理异常的安全日志
        handleSecurityLog(joinPoint, securityLog, exception, null);
    }

    /**
     * 处理安全日志的核心方法，记录安全操作的详细信息
     *
     * @param joinPoint   切点，表示被拦截的方法
     * @param securityLog @SecurityLog 注解对象，包含安全日志记录的配置信息
     * @param exception   如果方法抛出异常，则包含异常信息
     * @param jsonResult  如果方法执行成功，则包含返回结果
     */
    private void handleSecurityLog(final JoinPoint joinPoint, SecurityLog securityLog,
                                   final Exception exception, Object jsonResult) {
        try {
            // 获取当前登录用户
            SysUserDetails sysUserDetails = SecurityUtils.getLoginUser();

            // 构建安全日志对象
            SysSecurityLog sysSecurityLog = new SysSecurityLog();
            sysSecurityLog.setTitle(securityLog.title());
            sysSecurityLog.setOperationType(securityLog.businessType().name());
            sysSecurityLog.setUsername(sysUserDetails != null ? sysUserDetails.getUsername() : SysRolesConstant.ANONYMOUS);

            // 获取 HTTP 请求对象
            HttpServletRequest request = SecurityUtils.getHttpServletRequest();
            String ipAddr = IPUtils.getIpAddress(request);
            sysSecurityLog.setOperationIp(ipAddr);
            sysSecurityLog.setOperationRegion(IPUtils.getRegion(ipAddr));

            // 设置操作时间
            sysSecurityLog.setOperationTime(new Date());

            // 记录异常信息（仅用于日志输出，不存储到数据库）
            if (exception != null) {
                log.warn("安全操作执行异常，模块: {}, 用户: {}, 异常: {}",
                        securityLog.title(),
                        sysUserDetails != null ? sysUserDetails.getUsername() : "匿名用户",
                        exception.getMessage());
            }

            // 使用Spring异步服务记录安全日志
            asyncLogService.recordSecurityLog(sysSecurityLog);
            log.debug("安全日志已提交异步处理: {}", sysSecurityLog.getTitle());

        } catch (Exception e) {
            log.error("记录安全日志时发生异常: ", e);
        } finally {
            // 清理线程变量，防止内存泄漏
            TIME_THREADLOCAL.remove();
        }
    }

}
