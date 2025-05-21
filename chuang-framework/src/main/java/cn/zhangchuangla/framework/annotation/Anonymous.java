package cn.zhangchuangla.framework.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 建议不要在生产环境使用此注解，因为它将绕过Spring Security的权限检查。
 * <p>
 * 匿名访问注解，此注解表示该方法或类可以匿名访问。
 * 注意：在需要获取用户信息的上下文中使用此注解可能会导致异常，
 * 例如：在方法内部或者间接调用中尝试获取当前用户信息时会抛出异常。
 *
 * <p>与 {@link PreAuthorize} 注解相比，此注解的权限控制优先级较低。
 *
 * @author Chuang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anonymous {

}
