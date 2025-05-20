package cn.zhangchuangla.generator.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 代码生成列表视图对象
 *
 * @author Chuang
 * <p>
 * created on 2025-05-20 11:01
 */
@Data
@Schema(name = "代码生成列表视图对象", description = "用于展示基本的列表信息")
public class GenTableListVo {

    /**
     * 编号
     */
    @Schema(description = "编号", type = "int", format = "int64")
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
     * 生成模板类型
     */
    @Schema(description = "生成模板类型")
    private String tplCategory;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private String status;

    /**
     * 创建者
     */
    @Schema(description = "创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;


    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
