package cn.zhangchuangla.framework.aspect;

import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.AccessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.TooManyRequestException;
import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.framework.annotation.AccessLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 访问限制切面
 * 通过AOP方式实现注解式接口限流功能
 * 与拦截器方式互为补充，可以同时使用或单独使用
 * 拦截器适合对Controller层接口限流，AOP适合对任意方法限流
 *
 * @author Chuang
 * <p>
 * created on 2025/4/7 21:30
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AccessLimitAspect {

    /**
     * 限流Lua脚本
     */
    private static final String LIMIT_LUA_SCRIPT = """
            local key = KEYS[1]
            local maxCount = tonumber(ARGV[1])
            local expireTime = tonumber(ARGV[2])
            
            local current = tonumber(redis.call('get', key) or "0")
            if current >= maxCount then
                return 0
            end
            
            if current == 0 then
                redis.call('setex', key, expireTime, 1)
            else
                redis.call('incr', key)
            end
            return 1
            """;
    /**
     * Redis Lua脚本对象
     */
    private static final DefaultRedisScript<Long> LIMIT_SCRIPT = new DefaultRedisScript<>(LIMIT_LUA_SCRIPT, Long.class);
    /**
     * Redis操作模板
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 环绕通知处理访问限制
     *
     * @param joinPoint   连接点
     * @param accessLimit 访问限制注解
     * @return 处理结果
     * @throws Throwable 执行原方法可能抛出的异常
     */
    @Around("@annotation(accessLimit)")
    public Object around(ProceedingJoinPoint joinPoint, AccessLimit accessLimit) throws Throwable {
        // 获取注解中的限流参数
        int maxCount = accessLimit.maxCount();
        int limitPeriod = accessLimit.second();
        AccessType limitType = accessLimit.limitType();
        String message = accessLimit.message();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 构建Redis键
        String redisKey = buildLimitKey(attributes, joinPoint, limitType);

        try {
            // 执行Lua脚本进行限流判断
            Long result = stringRedisTemplate.execute(
                    LIMIT_SCRIPT,
                    Collections.singletonList(redisKey),
                    String.valueOf(maxCount),
                    String.valueOf(limitPeriod));

            // 如果执行结果为0，表示超过访问限制
            if (result == 0) {
                // 获取类名和方法名，用于日志记录
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();
                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();

                // 记录限流日志
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String ip = IPUtils.getIpAddr(request);
                    log.warn("AOP接口访问频率超限 - IP: {}, 方法: {}.{}, 限制: {}次/{}秒, 限流类型: {}",
                            ip, className, methodName, maxCount, limitPeriod, limitType.getDescription());
                } else {
                    log.warn("AOP接口访问频率超限 - 方法: {}.{}, 限制: {}次/{}秒, 限流类型: {}",
                            className, methodName, maxCount, limitPeriod, limitType.getDescription());
                }

                // 统一抛出TooManyRequestException，由全局异常处理器处理
                throw new TooManyRequestException(ResponseCode.TOO_MANY_REQUESTS, message);
            }
        } catch (TooManyRequestException e) {
            // 直接抛出TooManyRequestException异常
            throw e;
        } catch (Exception e) {
            // Redis异常时，为确保系统可用性，记录异常但放行请求
            log.error("AOP限流功能异常，请求已放行: {}", e.getMessage(), e);
        }

        // 请求未超过限制，执行原方法
        return joinPoint.proceed();
    }

    /**
     * 根据限流类型构建Redis key
     *
     * @param attributes ServletRequestAttributes对象
     * @param joinPoint  切点
     * @param limitType  限流类型枚举
     * @return Redis键
     */
    private String buildLimitKey(ServletRequestAttributes attributes, ProceedingJoinPoint joinPoint,
                                 AccessType limitType) {
        StringBuilder keyBuilder = new StringBuilder(64);

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String baseKey = className + ":" + methodName;

        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 根据限流类型选择不同的键前缀
        switch (limitType) {
            // IP限流模式
            case IP -> {
                String ipAddress = (request != null) ? IPUtils.getIpAddr(request) : "non-web";
                keyBuilder.append(RedisConstants.ACCESS_LIMIT_IP).append(baseKey).append(":").append(ipAddress);
            }
            // 用户ID限流模式
            case USER -> {
                try {
                    SysUserDetails sysUserDetails = SecurityUtils.getLoginUser();
                    keyBuilder.append(RedisConstants.ACCESS_LIMIT_USER).append(baseKey)
                            .append(":").append(sysUserDetails.getUserId());
                } catch (Exception e) {
                    // 获取用户失败，降级为IP限流
                    String ipAddress = (request != null) ? IPUtils.getIpAddr(request) : "non-web";
                    keyBuilder.append(RedisConstants.ACCESS_LIMIT_IP).append(baseKey).append(":").append(ipAddress);
                    log.debug("获取用户信息失败，降级为IP限流: {}", ipAddress);
                }
            }
            // 自定义参数限流模式
            case CUSTOM -> {
                String uri = (request != null) ? request.getRequestURI() : "non-web";
                keyBuilder.append(RedisConstants.ACCESS_LIMIT_CUSTOM).append(baseKey).append(":").append(uri);
            }
            default -> {
                String ipAddress = (request != null) ? IPUtils.getIpAddr(request) : "non-web";
                keyBuilder.append(RedisConstants.ACCESS_LIMIT_IP).append(baseKey).append(":").append(ipAddress);
            }
        }

        return keyBuilder.toString();
    }

    /**
     * 判断当前方法是否为Controller层方法
     *
     * @param joinPoint 切点
     * @return 是否为Controller层方法
     */
    private boolean isControllerMethod(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        return className.contains(".controller.") ||
                className.endsWith("Controller");
    }
}
