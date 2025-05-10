package cn.zhangchuangla.framework.annotation;

import cn.zhangchuangla.common.enums.AccessType;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 用于限制接口在指定时间内的访问次数
 * 支持多种限流策略：IP限流、用户限流或自定义key限流
 *
 * @author Chuang
 * <p>
 * created on 2025/4/7 20:44
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /**
     * 最大访问次数
     */
    int maxCount() default 5;

    /**
     * 限流窗口期间(秒)
     */
    int second() default 60;

    /**
     * 限流类型
     */
    AccessType limitType() default AccessType.IP;

    /**
     * 限流提示消息
     */
    String message() default "操作过于频繁，请稍后再试";

}
