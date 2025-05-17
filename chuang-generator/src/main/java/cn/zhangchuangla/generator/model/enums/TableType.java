package cn.zhangchuangla.generator.model.enums;

import lombok.Getter;

/**
 * 表类型枚举
 *
 * @author Chuang
 */
@Getter
public enum TableType {
    /**
     * 单表
     */
    SINGLE("single", "单表"),

    /**
     * 主子表
     */
    MASTER_CHILD("master_child", "主子表"),

    /**
     * 树形表
     */
    TREE("tree", "树形表");

    private final String code;
    private final String info;

    TableType(String code, String info) {
        this.code = code;
        this.info = info;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static TableType getByCode(String code) {
        for (TableType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return SINGLE;
    }
}