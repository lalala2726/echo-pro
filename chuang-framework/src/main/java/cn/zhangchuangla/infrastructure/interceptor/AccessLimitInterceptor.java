package cn.zhangchuangla.infrastructure.interceptor;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.enums.AccessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.TooManyRequestException;
import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.infrastructure.annotation.AccessLimit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 接口访问限流拦截器
 * 基于Redis实现分布式限流功能
 *
 * @author Chuang
 * <p>
 * created on 2025/4/7 21:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccessLimitInterceptor implements HandlerInterceptor {

    /**
     * 限流Lua脚本，保证原子性操作
     * 1. 获取当前访问次数
     * 2. 如果是第一次访问，设置初始值为1和过期时间
     * 3. 如果不是第一次访问且未超过限制，计数器加1
     * 4. 如果超过限制，直接拒绝
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

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 获取方法对象和AccessLimit注解
        Method method = handlerMethod.getMethod();
        AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);

        // 如果没有AccessLimit注解，直接放行
        if (accessLimit == null) {
            return true;
        }

        // 获取注解中的限流参数
        int maxCount = accessLimit.maxCount();
        int limitPeriod = accessLimit.second();
        AccessType limitType = accessLimit.limitType();
        String message = accessLimit.message();

        // 根据限流类型构建Redis key
        String redisKey = buildLimitKey(request, method, limitType);

        try {
            // 执行Lua脚本进行限流判断
            Long result = stringRedisTemplate.execute(
                    LIMIT_SCRIPT,
                    Collections.singletonList(redisKey),
                    String.valueOf(maxCount),
                    String.valueOf(limitPeriod));

            // 如果执行结果为0，表示超过访问限制
            if (result == 0) {
                String ip = IPUtils.getClientIp(request);
                String uri = request.getRequestURI();
                log.warn("接口访问频率超限 - IP: {}, URI: {}, 限制: {}次/{}秒, 限流类型: {}",
                        ip, uri, maxCount, limitPeriod, limitType.getDescription());

                // 直接抛出异常，由全局异常处理器处理
                throw new TooManyRequestException(ResponseCode.TOO_MANY_REQUESTS, message);
            }
        } catch (TooManyRequestException e) {
            // 直接将TooManyRequestException向上抛出
            throw e;
        } catch (Exception e) {
            // Redis异常时，为确保系统可用性，记录异常但放行请求
            log.error("限流功能异常，请求已放行: {}", e.getMessage(), e);
        }

        // 请求未超过限制，放行
        return true;
    }

    /**
     * 根据限流类型构建Redis key
     *
     * @param request   HTTP请求
     * @param method    限流的方法
     * @param limitType 限流类型枚举
     * @return Redis键
     */
    private String buildLimitKey(HttpServletRequest request, Method method, AccessType limitType) {
        StringBuilder keyBuilder = new StringBuilder(64);
        String className = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String baseKey = className + ":" + methodName;

        // 根据限流类型选择不同的键前缀
        switch (limitType) {
            // IP限流模式
            case IP -> {
                String ipAddress = IPUtils.getClientIp(request);
                keyBuilder.append(RedisKeyConstant.ACCESS_LIMIT_IP).append(baseKey).append(":").append(ipAddress);
            }
            // 用户ID限流模式
            case USER -> {
                // 尝试获取当前登录用户
                try {
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    keyBuilder.append(RedisKeyConstant.ACCESS_LIMIT_USER).append(baseKey)
                            .append(":").append(loginUser.getUserId());
                } catch (Exception e) {
                    // 未登录用户，默认降级为IP限流
                    String ipAddress = IPUtils.getClientIp(request);
                    keyBuilder.append(RedisKeyConstant.ACCESS_LIMIT_IP).append(baseKey).append(":").append(ipAddress);
                    log.debug("用户未登录，降级为IP限流: {}", ipAddress);
                }
            }
            // 自定义参数限流模式（此处使用URI作为自定义参数）
            case CUSTOM -> {
                String uri = request.getRequestURI();
                keyBuilder.append(RedisKeyConstant.ACCESS_LIMIT_CUSTOM).append(baseKey).append(":").append(uri);
            }
            default -> {
                // 默认采用IP限流
                String ipAddress = IPUtils.getClientIp(request);
                keyBuilder.append(RedisKeyConstant.ACCESS_LIMIT_IP).append(baseKey).append(":").append(ipAddress);
            }
        }

        return keyBuilder.toString();
    }
}
