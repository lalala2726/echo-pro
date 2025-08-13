package cn.zhangchuangla.system.core.enums;

import lombok.Getter;

/**
 * 菜单类型
 */
@Getter
public enum MenuTypeEnum {

    /**
     * 目录
     */
    CATALOG("catalog"),
    /**
     * 菜单
     */
    MENU("menu"),
    /**
     * 按钮
     */
    BUTTON("button"),
    /**
     * 内嵌
     */
    EMBEDDED("embedded"),
    /**
     * 外链
     */
    LINK("link");

    private final String value;

    MenuTypeEnum(String value) {
        this.value = value;
    }

    public static boolean isValid(String value) {
        if (value == null) return false;
        for (MenuTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static MenuTypeEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (MenuTypeEnum type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
