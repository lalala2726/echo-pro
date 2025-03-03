package cn.zhangchuangla.framework.annotation;

import java.lang.annotation.*;

/**
 * 匿名访问注解
 * 
 * @author zhangchuang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {
} 