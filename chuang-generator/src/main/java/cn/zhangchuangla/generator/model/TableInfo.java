package cn.zhangchuangla.generator.model;

import cn.zhangchuangla.generator.model.enums.TableType;
import lombok.Data;

import java.util.List;

/**
 * 表信息
 *
 * @author Chuang
 */
@Data
public class TableInfo {

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 表描述
     */
    private String tableComment;

    /**
     * 实体类名称(首字母大写)
     */
    private String className;

    /**
     * 实体类名称(首字母小写)
     */
    private String classNameLower;

    /**
     * 主键信息
     */
    private TableField primaryKey;

    /**
     * 表字段信息
     */
    private List<TableField> fields;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 作者
     */
    private String author;

    /**
     * 表类型
     */
    private TableType tableType;

    // --- 主子表特定字段 ---
    /**
     * 子表名（原始，带下划线）
     */
    // 新增
    private String subTableName;

    /**
     * 子表关联的外键名（在子表中，原始，带下划线）
     */
    // 新增
    private String subTableFkName;

    /**
     * 子表名称（驼峰，首字母大写）
     */
    private String subClassName;

    /**
     * 子表名称（驼峰，首字母小写）
     */
    private String subClassNameLower;

    /**
     * 子表关联的外键Java字段名（在子表中，驼峰，首字母大写）
     */
    private String subTableFkNameCapitalized;

    // --- 树形表特定字段 ---
    /**
     * 树编码字段名（原始，带下划线）
     */
    // 新增
    private String treeCode;

    /**
     * 树表主键字段的Java属性名（驼峰，首字母大写）
     */
    private String treeCodeCapitalized;

    /**
     * 树表父级字段的Java属性名（驼峰，首字母大写）
     */
    private String treeParentCodeCapitalized;

    /**
     * 树父编码字段名（原始，带下划线）
     */
    // 新增
    private String treeParentCode;

    /**
     * 树名称字段名（原始，带下划线）
     */
    // 新增
    private String treeName;

    /**
     * 树表名称字段的Java属性名（驼峰，首字母大写）
     */
    private String treeNameCapitalized;

    /**
     * 子表实体对象列表字段名 (例如: sysDeptList)
     */
    private String subTableListName;
}
