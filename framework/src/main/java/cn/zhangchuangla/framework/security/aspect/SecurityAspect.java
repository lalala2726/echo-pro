package cn.zhangchuangla.framework.security.aspect;

import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.security.annotation.RequiresPermissions;
import cn.zhangchuangla.framework.security.annotation.RequiresRoles;
import cn.zhangchuangla.framework.security.context.AuthenticationContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 安全切面
 * 用于处理权限和角色的校验
 */
@Aspect
@Component
public class SecurityAspect {

    private final RedisCache redisCache;

    public SecurityAspect(RedisCache redisCache) {
        this.redisCache = redisCache;
    }


    /**
     * 校验权限
     *
     * @param joinPoint           切点
     * @param requiresPermissions 权限注解
     * @return Object
     * @throws Throwable 异常
     */
    @Around("@annotation(requiresPermissions)")
    public Object checkPermissions(ProceedingJoinPoint joinPoint, RequiresPermissions requiresPermissions) throws Throwable {
        LoginUser loginUser = AuthenticationContextHolder.getContext();
        if (loginUser == null || !isUserInRedis(loginUser.getSysUser()
                .getUserId())) {
            throw new SecurityException("用户未登录或会话已过期");
        }
        return joinPoint.proceed(); // 继续执行方法
    }

    private boolean isUserInRedis(Long userId) {
        return redisCache.getCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + userId) != null;
    }

    /**
     * 校验角色
     *
     * @param joinPoint     切点
     * @param requiresRoles 角色注解
     * @return Object
     * @throws Throwable 异常
     */
    @Around("@annotation(requiresRoles)")
    public Object checkRoles(ProceedingJoinPoint joinPoint, RequiresRoles requiresRoles) throws Throwable {
        LoginUser loginUser = AuthenticationContextHolder.getContext();
        if (loginUser == null) {
            throw new SecurityException("用户未登录");
        }

//        List<String> roles = loginUser.getRoles(); // 获取当前用户的角色
//        if (!roles.contains(requiresRoles.value())) {
//            throw new SecurityException("没有角色访问该资源");
//        }

        return joinPoint.proceed(); // 继续执行方法
    }
}
