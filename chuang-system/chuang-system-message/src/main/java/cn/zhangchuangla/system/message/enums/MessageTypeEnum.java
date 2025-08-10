package cn.zhangchuangla.system.message.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/10 11:10
 */
@Getter
public enum MessageTypeEnum {

    /**
     * 系统消息
     */
    SYSTEM("system"),

    /**
     * 通知消息
     */
    NOTICE("notice"),

    /**
     * 公告消息
     */
    ANNOUNCEMENT("announcement");

    @JsonValue
    private final String value;

    MessageTypeEnum(String value) {
        this.value = value;
    }

    public static MessageTypeEnum getByValue(String value) {
        if (value == null) {
            return null;
        }
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MessageTypeEnum fromJson(String raw) {
        if (raw == null) {
            return null;
        }
        for (MessageTypeEnum e : values()) {
            if (e.name().equalsIgnoreCase(raw) || e.value.equalsIgnoreCase(raw)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unsupported message type: " + raw);
    }
}
