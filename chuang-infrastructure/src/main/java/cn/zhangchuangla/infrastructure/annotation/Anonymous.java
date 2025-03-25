package cn.zhangchuangla.infrastructure.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 匿名访问注解,此方法没有权限注解{@link PreAuthorize }优先级高
 *
 * @author zhangchuang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {

}
