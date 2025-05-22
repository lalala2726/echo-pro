package cn.zhangchuangla.framework.annotation;

import cn.zhangchuangla.common.core.enums.AccessType;

import java.lang.annotation.*;

/**
 * 接口限流注解，用于控制单位时间内的接口访问频率。
 * 支持多种限流策略（如：IP、用户ID、自定义KEY），适用于防止接口滥用或保护系统资源。
 * 注解信息可与 Google Guice 或 Spring AOP 等框架结合使用，实现运行时动态拦截和限流逻辑处理。
 *
 * @author Chuang
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
