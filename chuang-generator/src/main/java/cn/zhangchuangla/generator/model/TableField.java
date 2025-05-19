package cn.zhangchuangla.generator.model;

import lombok.Data;

/**
 * 表字段信息
 *
 * @author Chuang
 */
@Data
public class TableField {

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 字段注释
     */
    private String columnComment;

    /**
     * Java属性名称(首字母小写)
     */
    private String javaField;

    /**
     * Java属性名称(首字母大写)
     */
    private String javaFieldCapitalize;

    /**
     * Java类型
     */
    private String javaType;

    /**
     * 是否主键
     */
    private Boolean isPk;

    /**
     * 是否自增
     */
    private Boolean isIncrement;

    /**
     * 是否必填
     */
    private Boolean isRequired;

    /**
     * 是否为列表字段
     */
    private Boolean isList;

    /**
     * 是否为查询字段
     */
    private Boolean isQuery;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;
}