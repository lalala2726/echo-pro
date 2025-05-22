package cn.zhangchuangla.common.core.validator;

import cn.zhangchuangla.common.core.annoation.ValidRegex;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 正则表达式校验器
 *
 * @author Chuang
 * <p>
 * created on 2025/4/20 15:16
 */
public class RegexValidator implements ConstraintValidator<ValidRegex, String> {

    private boolean allowEmpty;
    private Pattern pattern;

    @Override
    public void initialize(ValidRegex constraintAnnotation) {
        this.allowEmpty = constraintAnnotation.allowEmpty();
        this.pattern = Pattern.compile(constraintAnnotation.regexp());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果值为null，直接通过验证
        if (value == null) {
            return true;
        }

        // 如果是空字符串，根据allowEmpty配置决定是否通过验证
        if (value.isEmpty()) {
            return allowEmpty;
        }

        // 非空则进行正则匹配
        return pattern.matcher(value).matches();
    }
}
