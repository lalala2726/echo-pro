package cn.zhangchuangla.common.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 访问限制类型枚举
 * 用于定义不同的限流策略类型
 *
 * @author Chuang
 * <p>
 * created on 2025/4/7 21:34
 */
@Getter
@RequiredArgsConstructor
public enum AccessType {

    /**
     * 基于IP地址的限流
     */
    IP(0, "基于IP地址限流"),

    /**
     * 基于用户ID的限流
     */
    USER(1, "基于用户ID限流"),

    /**
     * 基于自定义KEY的限流
     */
    CUSTOM(2, "基于自定义KEY限流");

    /**
     * 类型码
     */
    private final int code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据code获取对应的枚举值
     *
     * @param code 类型码
     * @return 对应的枚举值，如果没有匹配则返回IP类型
     */
    public static AccessType fromCode(int code) {
        for (AccessType type : AccessType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        // 默认返回IP限流类型
        return IP;
    }
}
