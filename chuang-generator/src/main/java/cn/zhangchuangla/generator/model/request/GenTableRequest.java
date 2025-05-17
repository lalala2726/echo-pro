package cn.zhangchuangla.generator.model.request;

import cn.zhangchuangla.generator.model.entity.GenTableColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 生成代码配置请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "生成代码配置请求")
public class GenTableRequest {

    /**
     * 编号
     */
    @Schema(description = "编号")
    private Long tableId;

    /**
     * 表名称
     */
    @NotBlank(message = "表名称不能为空")
    @Schema(description = "表名称")
    private String tableName;

    /**
     * 表描述
     */
    @NotBlank(message = "表描述不能为空")
    @Schema(description = "表描述")
    private String tableComment;

    /**
     * 实体类名称
     */
    @NotBlank(message = "实体类名称不能为空")
    @Schema(description = "实体类名称")
    private String className;

    /**
     * 生成包路径
     */
    @NotBlank(message = "生成包路径不能为空")
    @Schema(description = "生成包路径")
    private String packageName;

    /**
     * 生成模块名
     */
    @NotBlank(message = "生成模块名不能为空")
    @Schema(description = "生成模块名")
    private String moduleName;

    /**
     * 生成业务名
     */
    @NotBlank(message = "生成业务名不能为空")
    @Schema(description = "生成业务名")
    private String businessName;

    /**
     * 生成功能名
     */
    @NotBlank(message = "生成功能名不能为空")
    @Schema(description = "生成功能名")
    private String functionName;

    /**
     * 生成功能作者
     */
    @NotBlank(message = "作者不能为空")
    @Schema(description = "生成功能作者")
    private String functionAuthor;

    /**
     * 生成代码方式（0zip压缩包 1自定义路径）
     */
    @Schema(description = "生成代码方式（0zip压缩包 1自定义路径）")
    private String genType;

    /**
     * 生成路径
     */
    @Schema(description = "生成路径")
    private String genPath;

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
     * 其它生成选项
     */
    @Schema(description = "其它生成选项")
    private String options;

    /**
     * 表列信息
     */
    @Schema(description = "表列信息")
    private List<GenTableColumn> columns;

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
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}