package cn.zhangchuangla.generator.model.entity;

import cn.zhangchuangla.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 代码生成业务表
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gen_table")
@Schema(description = "代码生成业务表")
public class GenTable extends BaseEntity {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "编号")
    private Long tableId;

    /**
     * 表名称
     */
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 表描述
     */
    @Schema(description = "表描述")
    private String tableComment;

    /**
     * 关联子表的表名
     */
    @Schema(description = "关联子表的表名")
    private String subTableName;

    /**
     * 子表关联的外键名
     */
    @Schema(description = "子表关联的外键名")
    private String subTableFkName;

    /**
     * 实体类名称
     */
    @Schema(description = "实体类名称")
    private String className;

    /**
     * 使用的模板
     */
    @Schema(description = "使用的模板")
    private String tplCategory;

    /**
     * 表类型（single单表 master_child主子表 tree树形表）
     */
    @Schema(description = "表类型（single单表 master_child主子表 tree树形表）")
    private String tableType;

    /**
     * 树表主键字段
     */
    @Schema(description = "树表主键字段")
    private String treeCode;

    /**
     * 树表父级字段
     */
    @Schema(description = "树表父级字段")
    private String treeParentCode;

    /**
     * 树表名称字段
     */
    @Schema(description = "树表名称字段")
    private String treeName;

    /**
     * 生成包路径
     */
    @Schema(description = "生成包路径")
    private String packageName;

    /**
     * 生成模块名
     */
    @Schema(description = "生成模块名")
    private String moduleName;

    /**
     * 生成业务名
     */
    @Schema(description = "生成业务名")
    private String businessName;

    /**
     * 生成功能名
     */
    @Schema(description = "生成功能名")
    private String functionName;

    /**
     * 生成功能作者
     */
    @Schema(description = "生成功能作者")
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @Schema(description = "生成代码方式（0zip压缩包 1自定义路径）")
    private String genType;

    /**
     * 生成路径（不填默认项目路径）
     */
    @Schema(description = "生成路径")
    private String genPath;

    /**
     * 其它生成选项
     */
    @Schema(description = "其它生成选项")
    private String options;

    /**
     * 表列信息
     */
    @TableField(exist = false)
    @Schema(description = "表列信息")
    private List<GenTableColumn> columns;

    /**
     * 主键信息
     */
    @TableField(exist = false)
    @Schema(description = "主键信息")
    private GenTableColumn primaryKey;

    /**
     * 子表信息
     */
    @TableField(exist = false)
    @Schema(description = "子表信息")
    private GenTable subTable;
}