package cn.zhangchuangla.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 失火策略枚举
 *
 * @author Chuang
 */
@Getter
@AllArgsConstructor
public enum MisfirePolicy {

    /**
     * 默认策略
     */
    DEFAULT(0, "默认策略"),

    /**
     * 立即执行
     */
    IGNORE_MISFIRES(1, "立即执行"),

    /**
     * 执行一次
     */
    FIRE_AND_PROCEED(2, "执行一次"),

    /**
     * 放弃执行
     */
    DO_NOTHING(3, "放弃执行");

    /**
     * 策略码
     */
    private final Integer code;

    /**
     * 策略描述
     */
    private final String description;

    /**
     * 根据策略码获取枚举
     *
     * @param code 策略码
     * @return 失火策略枚举
     */
    public static MisfirePolicy getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MisfirePolicy policy : values()) {
            if (policy.getCode().equals(code)) {
                return policy;
            }
        }
        return null;
    }
}
