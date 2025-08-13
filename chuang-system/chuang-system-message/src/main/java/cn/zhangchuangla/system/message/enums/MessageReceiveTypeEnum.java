package cn.zhangchuangla.system.message.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/10 11:06
 */
@Getter
public enum MessageReceiveTypeEnum {

    /**
     * 给指定用户发送消息
     */
    USER("user"),

    /**
     * 给指定角色发送消息
     */
    ROLE("role"),

    /**
     * 给指定部门发送消息
     */
    DEPT("dept"),

    /**
     * 给所有用户发送消息
     */
    ALL("all");

    @JsonValue
    private final String value;


    MessageReceiveTypeEnum(String value) {
        this.value = value;
    }

    public static MessageReceiveTypeEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (MessageReceiveTypeEnum e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MessageReceiveTypeEnum fromJson(String raw) {
        if (raw == null) {
            return null;
        }
        for (MessageReceiveTypeEnum e : values()) {
            if (e.name().equalsIgnoreCase(raw) || e.value.equalsIgnoreCase(raw)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unsupported send method: " + raw);
    }

}
