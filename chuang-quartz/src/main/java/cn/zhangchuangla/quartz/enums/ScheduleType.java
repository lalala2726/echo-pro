package cn.zhangchuangla.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 调度策略枚举
 *
 * @author Chuang
 */
@Getter
@AllArgsConstructor
public enum ScheduleType {

    /**
     * Cron表达式调度
     */
    CRON(0, "Cron表达式"),

    /**
     * 固定频率调度
     */
    FIXED_RATE(1, "固定频率"),

    /**
     * 固定延迟调度
     */
    FIXED_DELAY(2, "固定延迟"),

    /**
     * 一次性调度
     */
    ONCE(3, "一次性执行");

    /**
     * 类型码
     */
    private final Integer code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据类型码获取枚举
     *
     * @param code 类型码
     * @return 调度策略枚举
     */
    public static ScheduleType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ScheduleType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
