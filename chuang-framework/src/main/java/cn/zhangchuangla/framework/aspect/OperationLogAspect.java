package cn.zhangchuangla.framework.aspect;

import cn.zhangchuangla.common.core.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.framework.async.AsyncLogService;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.NamedThreadLocal;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 操作日志记录处理
 * 通过AOP切面实现对Controller层加了 @Log 注解的方法进行拦截
 * 可记录请求参数、返回结果以及异常信息，并将日志保存到数据库
 *
 * @author Chuang
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    /**
     * 默认排除的敏感字段，防止敏感信息被记录
     */
    private static final List<String> EXCLUDE_PROPERTIES = Arrays.asList("password", "oldPassword", "newPassword", "confirmPassword", "accessToken", "accessKey", "secretKey");
    /**
     * 线程变量，记录方法执行的开始时间，用于计算方法耗时
     */
    private static final ThreadLocal<Long> TIME_THREADLOCAL = new NamedThreadLocal<>("Cost Time");
    /**
     * 标记当前线程是否是清空日志的操作，用于防止递归调用
     */
    private static final ThreadLocal<Boolean> IS_CLEANING_LOG = new NamedThreadLocal<>("Is Cleaning Log");
    private final AsyncLogService asyncLogService;

    public OperationLogAspect(AsyncLogService asyncLogService) {
        this.asyncLogService = asyncLogService;
    }

    /**
     * 在方法执行前记录开始时间
     *
     * @param joinPoint              切点，表示被拦截的方法
     * @param controllerOperationLog @Log 注解对象，包含日志记录的配置信息
     */
    @Before(value = "@annotation(controllerOperationLog)")
    public void doBefore(JoinPoint joinPoint, OperationLog controllerOperationLog) {
        // 记录当前方法开始执行的时间
        TIME_THREADLOCAL.set(System.currentTimeMillis());

        // 检查是否是清空日志的操作
        String methodName = joinPoint.getSignature().getName();
        if (methodName.contains("cleanLoginLog") || methodName.contains("cleanOperationLog")) {
            IS_CLEANING_LOG.set(true);
            log.info("检测到清空日志操作: {}, 标记为清空日志线程", methodName);
        } else {
            IS_CLEANING_LOG.set(false);
        }
    }

    /**
     * 在方法执行完成后（成功）记录日志
     *
     * @param joinPoint              切点，表示被拦截的方法
     * @param controllerOperationLog @Log 注解对象，包含日志记录的配置信息
     * @param jsonResult             方法返回的结果
     */
    @AfterReturning(pointcut = "@annotation(controllerOperationLog)", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, OperationLog controllerOperationLog, Object jsonResult) {
        // 处理成功的日志
        handleLog(joinPoint, controllerOperationLog, null, jsonResult);
    }

    /**
     * 在方法执行出现异常时记录日志
     *
     * @param joinPoint              切点，表示被拦截的方法
     * @param controllerOperationLog @Log 注解对象，包含日志记录的配置信息
     * @param e                      抛出的异常信息
     */
    @AfterThrowing(pointcut = "@annotation(controllerOperationLog)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, OperationLog controllerOperationLog, Exception e) {
        // 处理异常的日志
        handleLog(joinPoint, controllerOperationLog, e, null);
    }

    /**
     * 处理日志的核心方法，记录请求参数、返回结果和异常信息
     *
     * @param joinPoint              切点，表示被拦截的方法
     * @param controllerOperationLog @Log 注解对象，包含日志记录的配置信息
     * @param exception              如果方法抛出异常，则包含异常信息
     * @param jsonResult             如果方法执行成功，则包含返回结果
     */
    private void handleLog(final JoinPoint joinPoint, OperationLog controllerOperationLog, final Exception exception, Object jsonResult) {
        try {
            // 获取当前登录用户
            SysUserDetails sysUserDetails = SecurityUtils.getLoginUser();

            // 构建操作日志对象
            SysOperationLog sysOperationLog = new SysOperationLog();
            sysOperationLog.setModule(controllerOperationLog.title());
            sysOperationLog.setOperationType(controllerOperationLog.businessType().name());
            sysOperationLog.setUserId(sysUserDetails != null ? sysUserDetails.getUserId() : null);
            sysOperationLog.setUserName(sysUserDetails != null ? sysUserDetails.getUsername() : SysRolesConstant.ANONYMOUS);

            // 获取 HTTP 请求对象
            HttpServletRequest request = SecurityUtils.getHttpServletRequest();
            sysOperationLog.setRequestUrl(request.getRequestURI());
            sysOperationLog.setMethodName(joinPoint.getSignature().getName());
            String ipAddr = IPUtils.getIpAddress(request);
            sysOperationLog.setOperationIp(ipAddr);
            sysOperationLog.setOperationRegion(IPUtils.getRegion(ipAddr));
            sysOperationLog.setRequestMethod(request.getMethod());

            // 计算方法执行耗时
            sysOperationLog.setCostTime(System.currentTimeMillis() - TIME_THREADLOCAL.get());

            // 记录请求参数
            if (controllerOperationLog.saveRequestData()) {
                sysOperationLog.setRequestParams(getRequestParams(joinPoint, controllerOperationLog.excludeParamNames()));
            }

            // 记录返回结果或异常信息
            if (exception != null) {
                // 记录异常信息
                sysOperationLog.setErrorMsg(exception.getMessage());
                sysOperationLog.setOperationStatus(1);
            } else {
                // 记录成功地返回结果
                sysOperationLog.setOperationStatus(0);
                if (controllerOperationLog.isSaveResponseData() && jsonResult != null) {
                    // 确保记录响应结果
                    try {
                        String jsonString = JSON.toJSONString(jsonResult);
                        sysOperationLog.setResponseResult(jsonString);
                        log.debug("记录响应结果: {}", jsonString);
                    } catch (Exception e) {
                        log.error("转换响应结果为JSON时出错: {}", e.getMessage(), e);
                        sysOperationLog.setResponseResult("Error converting result: " + e.getMessage());
                    }
                }
            }
            // 设置创建时间
            sysOperationLog.setCreateTime(new Date());

            // 使用Spring异步服务记录日志
            asyncLogService.recordOperationLog(sysOperationLog);
            log.debug("操作日志已提交异步处理: {}", sysOperationLog.getModule());

        } catch (Exception e) {
            log.error("记录操作日志时发生异常: ", e);
        } finally {
            // 清理线程变量，防止内存泄漏
            TIME_THREADLOCAL.remove();
            IS_CLEANING_LOG.remove();
        }
    }

    /**
     * 通过反射获取请求参数，并排除不需要记录的字段
     *
     * @param joinPoint         切点对象
     * @param excludeParamNames 需要排除的字段
     * @return 请求参数的 JSON 字符串
     */
    private String getRequestParams(JoinPoint joinPoint, String[] excludeParamNames) {
        try {
            // 获取方法签名
            Signature signature = joinPoint.getSignature();
            Object[] args = joinPoint.getArgs();

            // 如果没有参数，直接返回空 JSON
            if (args == null || args.length == 0) {
                return "{}";
            }

            // 构建需要排除的字段列表
            List<String> excludeList = new ArrayList<>(EXCLUDE_PROPERTIES);
            if (excludeParamNames != null) {
                excludeList.addAll(Arrays.asList(excludeParamNames));
            }

            // 处理方法参数
            if (signature instanceof MethodSignature methodSignature) {
                String[] parameterNames = methodSignature.getParameterNames();

                // 如果有参数名，则按名称解析
                if (parameterNames != null) {
                    // 将参数名和对应的值转换成 Map
                    Map<String, Object> paramMap = IntStream.range(0, parameterNames.length)
                            .filter(i -> i < args.length && args[i] != null && !(args[i] instanceof HttpServletRequest))
                            // 排除敏感字段
                            .filter(i -> !excludeList.contains(parameterNames[i]))
                            .boxed()
                            .collect(Collectors.toMap(i -> parameterNames[i], i -> args[i], (a, b) -> b));

                    // 转换成 JSON 格式返回
                    return JSON.toJSONString(paramMap);
                }
            }

            return "{}";
        } catch (Exception e) {
            log.error("解析请求参数异常", e);
            return "{}";
        }
    }
}
