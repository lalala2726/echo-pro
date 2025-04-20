package cn.zhangchuangla.common.annoation;

import cn.zhangchuangla.common.validator.RegexValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/20 15:14
 */
@Documented
@Constraint(validatedBy = RegexValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegex {

    /**
     * 错误提示信息
     *
     * @return 错误提示信息
     */
    String message() default "格式不正确";

    /**
     * 组
     *
     * @return 组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     *
     * @return 负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 正则表达式
     *
     * @return 正则表达式
     */
    String regexp();  // 正则表达式

    /**
     * 是否允许空字符串
     *
     * @return 是否允许空字符串
     */
    boolean allowEmpty() default true;  // 是否允许空字符串
}
