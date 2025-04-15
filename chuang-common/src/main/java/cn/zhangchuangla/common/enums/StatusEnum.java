package cn.zhangchuangla.common.enums;

import cn.zhangchuangla.common.base.BaseEnum;
import lombok.Getter;

/**
 * 状态枚举
 *
 * @author haoxr
 * @since 2022/10/14
 */
@Getter
public enum StatusEnum implements BaseEnum<Integer> {

    ENABLE(1, "启用"),
    DISABLE(0, "禁用");

    private final Integer value;


    private final String label;

    StatusEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }
}
