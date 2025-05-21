package cn.zhangchuangla.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识需要进行二次验证的方法，用于在敏感操作时校验用户身份。
 *
 * <p>该注解适用于需要更高安全级别的场景（如修改密码、删除账户等），
 * 使用此注解的方法需通过JSON请求体传递验证参数。</p>
 *
 * <b>注意：</b>本注解依赖JSON请求体传递验证信息，因此GET请求等不支持
 * 请求体的HTTP方法将无法正常使用此功能。
 *
 * @author Chuang
 * created on 2025/5/10 00:39
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresSecondAuth {

    /**
     * 二次验证中使用的密码参数名称，默认值为"password"。
     *
     * <p>该参数应包含在请求的JSON正文中。</p>
     *
     * @return 密码参数的名称
     */
    String passwordParam() default "password";

    /**
     * 验证失败时返回的错误消息，默认为"二次认证失败：密码错误或用户未认证。"。
     *
     * @return 自定义的错误提示消息
     */
    String message() default "二次认证失败：密码错误或用户未认证。";
}
