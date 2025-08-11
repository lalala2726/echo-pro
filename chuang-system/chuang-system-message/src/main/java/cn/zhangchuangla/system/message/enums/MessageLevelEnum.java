package cn.zhangchuangla.system.message.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonValue
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
        if (value == null) return null;
        for (MessageLevelEnum level : values()) {
            if (level.value.equalsIgnoreCase(value)) {
                return level;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MessageLevelEnum fromJson(String raw) {
        if (raw == null) return null;
        for (MessageLevelEnum e : values()) {
            if (e.name().equalsIgnoreCase(raw) || e.value.equalsIgnoreCase(raw)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unsupported message level: " + raw);
    }

}
