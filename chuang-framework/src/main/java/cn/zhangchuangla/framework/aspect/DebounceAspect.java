package cn.zhangchuangla.framework.aspect;

import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.AccessType;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.TooManyRequestException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.framework.annotation.Debounce;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 防抖切面：在窗口期内阻止重复调用。
 *
 * @author Chuang
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DebounceAspect {

    private final StringRedisTemplate stringRedisTemplate;

    private final ExpressionParser spelParser = new SpelExpressionParser();

    @Around("@annotation(debounce)")
    public Object around(ProceedingJoinPoint joinPoint, Debounce debounce) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Debounce classLevel = method.getDeclaringClass().getAnnotation(Debounce.class);
        Debounce effective = debounce != null ? debounce : classLevel;
        if (effective == null || !effective.enable()) {
            return joinPoint.proceed();
        }

        long windowMillis = Math.max(1L, effective.windowMillis());
        AccessType type = effective.type();
        String message = effective.message();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String redisKey = buildDebounceKey(attributes, joinPoint, type, effective.key());

        try {
            Boolean ok = stringRedisTemplate.opsForValue()
                    .setIfAbsent(redisKey, "1", windowMillis, TimeUnit.MILLISECONDS);
            if (Boolean.FALSE.equals(ok)) {
                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String ip = IPUtils.getIpAddress(request);
                    log.warn("防抖拦截-重复请求: ip={}, {}.{}, window={}ms, type={}",
                            ip, className, methodName, windowMillis, type.name());
                } else {
                    log.warn("防抖拦截-重复请求: {}.{}, window={}ms, type={}",
                            className, methodName, windowMillis, type.name());
                }
                throw new TooManyRequestException(ResultCode.TOO_MANY_REQUESTS, message);
            }
        } catch (TooManyRequestException e) {
            throw e;
        } catch (Exception e) {
            // Redis 异常降级放行
            log.error("防抖功能异常，已放行: {}", e.getMessage(), e);
        }

        return joinPoint.proceed();
    }

    private String buildDebounceKey(ServletRequestAttributes attributes, ProceedingJoinPoint joinPoint,
                                    AccessType type, String customKeyExpr) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String baseKey = className + ":" + methodName;

        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        StringBuilder key = new StringBuilder(64);
        switch (type) {
            case USER -> {
                try {
                    SysUserDetails user = SecurityUtils.getLoginUser();
                    key.append(RedisConstants.ACCESS_LIMIT_PREFIX).append("debounce:user:").append(baseKey)
                            .append(":").append(user.getUserId());
                } catch (Exception e) {
                    String ip = request != null ? IPUtils.getIpAddress(request) : "non-web";
                    key.append(RedisConstants.ACCESS_LIMIT_PREFIX).append("debounce:ip:").append(baseKey)
                            .append(":").append(ip);
                    log.debug("获取用户失败，防抖降级为IP维度: {}", ip);
                }
            }
            case CUSTOM -> {
                String evaluated = null;
                if (customKeyExpr != null && !customKeyExpr.isBlank()) {
                    try {
                        StandardEvaluationContext context = new StandardEvaluationContext();
                        context.setVariable("args", joinPoint.getArgs());
                        context.setVariable("request", request);
                        try {
                            SysUserDetails user = SecurityUtils.getLoginUser();
                            context.setVariable("user", user);
                        } catch (Exception ignore) {
                        }
                        evaluated = String.valueOf(spelParser.parseExpression(customKeyExpr).getValue(context));
                    } catch (Exception e) {
                        log.warn("自定义防抖Key SpEL解析失败，降级为URI: {}", e.getMessage());
                    }
                }
                if (evaluated == null || evaluated.isBlank()) {
                    evaluated = request != null ? request.getRequestURI() : "non-web";
                }
                key.append(RedisConstants.ACCESS_LIMIT_PREFIX).append("debounce:custom:").append(baseKey)
                        .append(":").append(evaluated);
            }
            default -> {
                String ip = request != null ? IPUtils.getIpAddress(request) : "non-web";
                key.append(RedisConstants.ACCESS_LIMIT_PREFIX).append("debounce:ip:").append(baseKey)
                        .append(":").append(ip);
            }
        }
        return key.toString();
    }
}



