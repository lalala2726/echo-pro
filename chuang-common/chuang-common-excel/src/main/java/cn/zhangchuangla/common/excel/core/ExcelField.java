package cn.zhangchuangla.common.excel.core;

import cn.zhangchuangla.common.excel.annotation.Excel;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * Excel字段信息
 * 用于存储字段的元数据信息
 *
 * @author Chuang
 * @since 2025-01-23
 */
@Data
public class ExcelField {

    /**
     * 字段对象
     */
    private Field field;

    /**
     * Excel注解
     */
    private Excel excel;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 列标题
     */
    private String title;

    /**
     * 排序序号
     */
    private int sort;

    /**
     * 列宽度
     */
    private double width;

    /**
     * 列高度
     */
    private double height;

    /**
     * 对齐方式
     */
    private Excel.Align align;

    /**
     * 字典键
     */
    private String dictKey;

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 数字格式
     */
    private String numFormat;

    /**
     * 字体颜色
     */
    private String color;

    /**
     * 背景颜色
     */
    private String backgroundColor;

    /**
     * 是否加粗
     */
    private boolean isBold;

    /**
     * 列类型
     */
    private Excel.ColumnType columnType;

    /**
     * 是否导出
     */
    private boolean isExport;

    /**
     * 目标属性
     */
    private String targetAttr;

    public ExcelField(Field field, Excel excel) {
        this.field = field;
        this.excel = excel;
        this.fieldName = field.getName();
        this.title = excel.name();
        this.sort = excel.sort();
        this.width = excel.width();
        this.height = excel.height();
        this.align = excel.align();
        this.dictKey = excel.dictKey();
        this.dateFormat = excel.dateFormat();
        this.defaultValue = excel.defaultValue();
        this.suffix = excel.suffix();
        this.numFormat = excel.numFormat();
        this.color = excel.color();
        this.backgroundColor = excel.backgroundColor();
        this.isBold = excel.isBold();
        this.columnType = excel.type();
        this.isExport = excel.isExport();
        this.targetAttr = excel.targetAttr();
    }
}
