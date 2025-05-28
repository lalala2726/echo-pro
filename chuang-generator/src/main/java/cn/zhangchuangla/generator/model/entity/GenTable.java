package cn.zhangchuangla.generator.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 代码生成业务表
 *
 * @author Chuang
 */
@TableName(value = "gen_table")
@Data
public class GenTable {

    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long tableId;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 实体类名称
     */
    private String className;

    /**
     * 生成包路径
     */
    private String packageName;

    /**
     * 生成模块名
     */
    private String moduleName;

    /**
     * 生成业务名
     */
    private String businessName;

    /**
     * 生成功能名
     */
    private String functionName;

    /**
     * 生成功能作者
     */
    private String functionAuthor;

    /**
     * 生成模板类型
     */
    private String tplCategory;

    /**
     * 树编码字段（树表专用）
     */
    private String treeCode;

    /**
     * 树父编码字段（树表专用）
     */
    private String treeParentCode;

    /**
     * 树名称字段（树表专用）
     */
    private String treeName;

    /**
     * 关联子表的表名（主子表专用）
     */
    private String subTableName;

    /**
     * 子表关联的外键名（主子表专用）
     */
    private String subTableFkName;

    /**
     * 是否级联删除子节点 (树表专用 '1'是 '0'否)
     * (Indicates if child nodes should be cascade deleted for tree tables. '1' for true, '0' for false)
     */
    private String cascadeDeleteTree;

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

    /**
     * 表字段信息
     */
    @TableField(exist = false)
    private List<GenTableColumn> columns;
}
