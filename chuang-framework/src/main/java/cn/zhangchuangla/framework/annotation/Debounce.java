package cn.zhangchuangla.framework.annotation;

import cn.zhangchuangla.common.core.enums.AccessType;

import java.lang.annotation.*;

/**
 * 防抖注解：在设定的时间窗口内，阻止重复请求/调用。
 * 典型用途：表单防重复提交、接口点击过快保护等。
 * <p>
 * 使用：
 * - 可用于方法或类级别（类级别对类内所有方法生效，方法级别可覆盖类级别配置）。
 * - 维度支持 IP/USER/CUSTOM（CUSTOM 支持 SpEL 构建 Key）。
 * <p>
 * created by Chuang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Debounce {

    /**
     * 防抖窗口（毫秒）
     */
    long windowMillis() default 2000L;

    /**
     * 防抖维度类型
     */
    AccessType type() default AccessType.USER;

    /**
     * 自定义维度 Key（支持 SpEL，如 '#request.requestURI + ":" + #user.userId'）
     * 当 type=CUSTOM 时优先使用；为空时默认取请求 URI
     */
    String key() default "";

    /**
     * 提示信息
     */
    String message() default "操作过快，请稍后再试";

    /**
     * 是否启用
     */
    boolean enable() default true;
}


