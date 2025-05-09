package cn.zhangchuangla.infrastructure.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此方法不支持GET请求
 *
 * @author Chuang
 * <p>
 * created on 2025/5/10 00:39
 */
@Target(ElementType.METHOD) // 此注解仅能应用于方法
@Retention(RetentionPolicy.RUNTIME) // 此注解在运行时保留，允许通过反射读取
public @interface RequiresSecondAuth {

    /**
     * 二次验证的参数名称，默认是"password"。
     *
     * @return 参数名称
     */
    String passwordParam() default "password";

    /**
     * 二次验证失败时返回的错误消息。
     *
     * @return 错误消息
     */
    String message() default "二次认证失败：密码错误或用户未认证。";
}
