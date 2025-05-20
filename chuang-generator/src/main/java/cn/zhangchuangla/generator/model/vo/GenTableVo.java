package cn.zhangchuangla.generator.model.vo;

import cn.zhangchuangla.common.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成业务表
 *
 * @author zhangchuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "代码生成业务表视图对象", description = "用于展示详细的低代码业务表信息")
public class GenTableVo extends BaseVo {

    /**
     * 编号
     */
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
     * 实体类名称
     */
    @Schema(description = "实体类名称")
    private String className;

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
     * 生成模板类型
     */
    @Schema(description = "生成模板类型")
    private String tplCategory;
}
