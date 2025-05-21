package cn.zhangchuangla.generator.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代码生成业务表字段视图对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "代码生成业务表字段视图对象", description = "代码生成业务表字段视图对象")
public class GenTableColumnVo {

    /**
     * 编号
     */
    @Schema(description = "编号")
    private Long columnId;

    /**
     * 归属表编号
     */
    @Schema(description = "归属表编号")
    private Long tableId;

    /**
     * 列名称
     */
    @Schema(description = "列名称")
    private String columnName;

    /**
     * 列描述
     */
    @Schema(description = "列描述")
    private String columnComment;

    /**
     * 列类型
     */
    @Schema(description = "列类型")
    private String columnType;

    /**
     * JAVA类型
     */
    @Schema(description = "JAVA类型")
    private String javaType;

    /**
     * JAVA字段名
     */
    @Schema(description = "JAVA字段名")
    private String javaField;

    /**
     * 是否主键（1是）
     */
    @Schema(description = "是否主键（1是）")
    private String isPk;

    /**
     * 是否自增（1是）
     */
    @Schema(description = "是否自增（1是）")
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @Schema(description = "是否必填（1是）")
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @Schema(description = "是否为插入字段（1是）")
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @Schema(description = "是否编辑字段（1是）")
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @Schema(description = "是否列表字段（1是）")
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @Schema(description = "是否查询字段（1是）")
    private String isQuery;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    @Schema(description = "查询方式（等于、不等于、大于、小于、范围）")
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    @Schema(description = "显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）")
    private String htmlType;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型")
    private String dictType;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
}
