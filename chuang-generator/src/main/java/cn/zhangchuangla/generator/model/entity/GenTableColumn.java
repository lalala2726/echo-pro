package cn.zhangchuangla.generator.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 代码生成业务表字段
 *
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
@TableName(value = "gen_table_column")
@Data
public class GenTableColumn {

    /**
     * 编号
     */
    private Long columnId;

    /**
     * 归属表编号
     */
    private Long tableId;

    /**
     * 列名称
     */
    private String columnName;

    /**
     * 列描述
     */
    private String columnComment;

    /**
     * 列类型
     */
    private String columnType;

    /**
     * JAVA类型
     */
    private String javaType;

    /**
     * JAVA字段名
     */
    private String javaField;

    /**
     * 是否主键（0否 1是）
     */
    private Integer isPk;

    /**
     * 是否自增（0否 1是）
     */
    private Integer isIncrement;

    /**
     * 是否必填（0否 1是）
     */
    private Integer isRequired;

    /**
     * 是否为插入字段（0否 1是）
     */
    private Integer isInsert;

    /**
     * 是否编辑字段（0否 1是）
     */
    private Integer isEdit;

    /**
     * 是否列表字段（0否 1是）
     */
    private Integer isList;

    /**
     * 是否查询字段（0否 1是）
     */
    private Integer isQuery;

    /**
     * 查询方式（EQ等于、NE不等于、GT大于、LT小于、LIKE模糊、BETWEEN范围）
     */
    private String queryType;

    /**
     * 显示类型（input输入框、textarea文本域、select下拉框、checkbox复选框、radio单选框、date日期控件、datetime日期时间控件、upload上传控件、summernote富文本控件）
     */
    private String htmlType;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}
