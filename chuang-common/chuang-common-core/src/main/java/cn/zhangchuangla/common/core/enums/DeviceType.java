package cn.zhangchuangla.common.core.enums;

import lombok.Getter;

/**
 * 设备类型枚举
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 23:31
 */
@Getter
public enum DeviceType {

    /**
     * 网页端
     */
    WEB("web"),

    /**
     * PC端
     */
    PC("pc"),

    /**
     * 安卓APP
     */
    ANDROID("android"),

    /**
     * 苹果APP
     */
    IOS("ios"),

    /**
     * WINDOWS端
     */
    WINDOWS("windows"),

    /**
     * 苹果电脑
     */
    MAC("mac"),

    /**
     * 鸿蒙系统
     */
    HARMONY("harmony"),

    /**
     * linux系统
     */
    LINUX("linux"),

    /**
     * 微信小程序
     */
    MINI_PROGRAM("miniProgram"),

    /**
     * 未知
     */
    UNKNOWN("unknown");

    private final String value;

    DeviceType(String value) {
        this.value = value;
    }

    /**
     * 根据枚举值获取枚举对象
     */
    public static DeviceType getByValue(String value) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.value.equals(value)) {
                return deviceType;
            }
        }
        return UNKNOWN;
    }
}
