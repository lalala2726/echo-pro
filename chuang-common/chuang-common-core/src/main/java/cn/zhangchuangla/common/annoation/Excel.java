package cn.zhangchuangla.common.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于标记字段，支持导出到 Excel 的配置化定义，提供灵活的元数据控制。
 *
 * @author Chuang
 * created on 2025/5/21 14:15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {

    /**
     * 排序序号（升序）
     */
    int sort() default Integer.MAX_VALUE;

    /**
     * 导出到Excel中的名字
     */
    String name() default "";

    /**
     * 日期格式 如: yyyy-MM-dd, yyyy-MM-dd HH:mm:ss
     */
    String dateFormat() default "";

    /**
     * 导出类型（0数字 1字符串 2图片）
     */
    ColumnType type() default ColumnType.STRING;

    /**
     * 导出时在excel中每个列的高度 单位为字符
     */
    double height() default 14;

    /**
     * 导出时在excel中每个列的宽度 单位为字符
     */
    double width() default 16;

    /**
     * 文字后缀，如% 90 变成 90%
     */
    String suffix() default "";

    /**
     * 当值为空时，字段的默认值
     */
    String defaultValue() default "";

    /**
     * 提示信息
     */
    String prompt() default "";

    /**
     * 设置只能选择不能输入的列内容
     */
    String[] combo() default {};

    /**
     * 是否需要纵向合并单元格
     */
    boolean needMerge() default false;

    /**
     * 是否导出数据，应对需求:有时我们需要导出一份模板，这是标题需要但内容需要用户手工填写
     */
    boolean isExport() default true;

    /**
     * 另一个类中的属性名称，支持多级获取，以小数点隔开
     */
    String targetAttr() default "";

    /**
     * 是否自动统计数据，在最后追加一行统计数据总和
     */
    boolean isStatistics() default false;

    /**
     * 导出字段对齐方式（0：默认；1：靠左；2：居中；3：靠右）
     */
    Align align() default Align.AUTO;

    /**
     * 字体颜色（例如：red或#FFFFFF）
     */
    String color() default "";

    /**
     * 背景颜色（例如：red或#FFFFFF）
     */
    String backgroundColor() default "";

    /**
     * 字体加粗（true/false）
     */
    boolean isBold() default false;

    /**
     * 单元格格式化，例如：0.00表示保留两位小数
     */
    String numFormat() default "";

    /**
     * 单元格格式枚举
     */
    enum ColumnType {
        /**
         * 数字
         */
        NUMERIC(0),
        /**
         * 字符串
         */
        STRING(1),
        /**
         * 图片
         */
        IMAGE(2);

        private final int value;

        ColumnType(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }

    /**
     * 对齐方式枚举
     */
    enum Align {
        /**
         * 自动
         */
        AUTO(0),
        /**
         * 靠左
         */
        LEFT(1),
        /**
         * 居中
         */
        CENTER(2),
        /**
         * 靠右
         */
        RIGHT(3);

        private final int value;

        Align(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}
