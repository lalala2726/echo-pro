package cn.zhangchuangla.common.annoation;

import cn.zhangchuangla.common.validator.RegexValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * 作用于字段，用于校验字段值是否符合指定的正则表达式规则，并支持定义是否允许空字符串。
 * <p>
 * 在某些业务场景下，前端传递的空字符串我们认为等同于未填写，因此需要控制是否允许空字符串通过校验。
 * 标准的 {@link Pattern} 注解仅在字段值为 null 时跳过校验，对于空字符串无法灵活处理，
 * 因此我们自定义了该注解以满足实际需求。
 *
 * @author Chuang
 * @since 2025/4/20 15:14
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
    String regexp();

    /**
     * 是否允许空字符串
     *
     * @return 是否允许空字符串
     */
    boolean allowEmpty() default false;
}
