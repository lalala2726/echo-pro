package cn.zhangchuangla.framework.annotation;

import cn.zhangchuangla.common.core.enums.BusinessType;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/3 03:45
 */
public @interface SecurityLog {

    String title();

    BusinessType businessType() default BusinessType.OTHER;
}
