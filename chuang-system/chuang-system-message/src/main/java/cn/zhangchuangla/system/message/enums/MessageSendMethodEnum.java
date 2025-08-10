package cn.zhangchuangla.system.message.enums;

import lombok.Getter;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/10 11:06
 */
@Getter
public enum MessageSendMethodEnum {

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

    private final String value;


    MessageSendMethodEnum(String value) {
        this.value = value;
    }

}
