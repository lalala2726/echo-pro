package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代码生成业务表字段修改请求类
 *
 * @author Chuang
 */
@Data
@Schema(name = "代码生成业务表字段修改请求类", description = "用于修改低代码业务表字段信息")
public class ColumnUpdateRequest {

    /**
     * 编号
     */
    @Schema(description = "编号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long columnId;

    /**
     * 归属表编号
     */
    @Schema(description = "归属表编号", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tableId;

    /**
     * 列名称
     */
    @Schema(description = "列名称", example = "user_id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String columnName;

    /**
     * 列描述
     */
    @Schema(description = "列描述", example = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String columnComment;

    /**
     * 列类型
     */
    @Schema(description = "列类型", example = "int(11)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String columnType;

    /**
     * JAVA类型
     */
    @Schema(description = "JAVA类型", example = "String", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String javaType;

    /**
     * JAVA字段名
     */
    @Schema(description = "JAVA字段名", example = "userName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String javaField;

    /**
     * 是否主键（1是）
     */
    @Schema(description = "是否主键（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isPk;

    /**
     * 是否自增（1是）
     */
    @Schema(description = "是否自增（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isIncrement;

    /**
     * 是否必填（1是）
     */
    @Schema(description = "是否必填（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isRequired;

    /**
     * 是否为插入字段（1是）
     */
    @Schema(description = "是否为插入字段（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isInsert;

    /**
     * 是否编辑字段（1是）
     */
    @Schema(description = "是否编辑字段（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isEdit;

    /**
     * 是否列表字段（1是）
     */
    @Schema(description = "是否列表字段（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isList;

    /**
     * 是否查询字段（1是）
     */
    @Schema(description = "是否查询字段（1表示是）", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String isQuery;

    /**
     * 查询方式（等于、不等于、大于、小于、范围）
     */
    @Schema(description = "查询方式", example = "EQ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String queryType;

    /**
     * 显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）
     */
    @Schema(description = "显示类型", example = "input", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String htmlType;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型", example = "sys_user_sex", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dictType;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;
}
