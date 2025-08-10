package cn.zhangchuangla.system.message.enums;

import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/10 11:12
 */
@Getter
public enum MessageLevelEnum {

    /**
     * 普通
     */
    NORMAL("normal"),
    /**
     * 重要
     */
    IMPORTANT("important"),
    /**
     * 紧急
     */
    URGENT("urgent");

    private final String value;

    MessageLevelEnum(String value) {
        this.value = value;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 枚举
     */
    public static MessageLevelEnum getByValue(String value) {
        for (MessageLevelEnum level : values()) {
            if (level.value.equals(value)) {
                return level;
            }
        }
        return null;
    }

}
