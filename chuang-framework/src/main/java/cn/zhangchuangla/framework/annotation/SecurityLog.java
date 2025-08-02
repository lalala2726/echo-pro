package cn.zhangchuangla.framework.annotation;

import cn.zhangchuangla.common.core.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 安全日志记录注解，用于标记需要记录安全敏感操作的方法
 * <p>
 * 该注解主要用于记录用户的敏感操作，如权限变更、密码修改、重要数据操作等。
 * 与普通操作日志不同，安全日志更关注安全相关的操作记录，便于安全审计和问题追踪。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 * @SecurityLog(title = "修改用户密码", businessType = BusinessType.UPDATE)
 * public Result changePassword(ChangePasswordRequest request) {
 *     // 业务逻辑
 * }
 * }
 * </pre>
 *
 * @author Chuang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecurityLog {

    /**
     * 安全操作模块标题
     * <p>
     * 用于描述当前安全操作的模块或功能名称，如"用户管理"、"权限配置"等
     * </p>
     *
     * @return 模块标题
     */
    String title();

    /**
     * 安全操作的业务类型
     * <p>
     * 定义当前操作的业务类型，默认为 BusinessType.SECURITY
     * 常用的安全相关业务类型包括：SECURITY、UPDATE、DELETE、GRANT等
     * </p>
     *
     * @return 业务类型枚举
     */
    BusinessType businessType() default BusinessType.SECURITY;
}
