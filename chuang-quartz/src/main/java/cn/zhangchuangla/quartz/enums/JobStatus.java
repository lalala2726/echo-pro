package cn.zhangchuangla.quartz.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 *
 * @author Chuang
 */
@Getter
@AllArgsConstructor
public enum JobStatus {

    /**
     * 正常运行
     */
    NORMAL(0, "正常"),

    /**
     * 暂停
     */
    PAUSED(1, "暂停"),

    /**
     * 已完成
     */
    COMPLETE(2, "完成"),

    /**
     * 错误
     */
    ERROR(3, "错误"),

    /**
     * 阻塞
     */
    BLOCKED(4, "阻塞");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return 任务状态枚举
     */
    public static JobStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (JobStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
