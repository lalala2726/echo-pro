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
     * 主键
     */
    @Schema(description = "主键", type = "int", format = "int64", example = "1")
    private Long id;

    /**
     * 表名称
     */
    @Schema(description = "表名称", example = "user_table")
    private String tableName;

    /**
     * 表描述
     */
    @Schema(description = "表描述", example = "用户信息表")
    private String tableComment;

    /**
     * 表类型（0单表，1主子表,2树表）
     */
    @Schema(description = "表类型（0单表，1主子表,2树表）", example = "0")
    private Integer tableType;


    /**
     * 生成模块名
     */
    @Schema(description = "生成模块名", example = "user")
    private String moduleName;

    /**
     * 生成业务名
     */
    @Schema(description = "生成业务名", example = "user")
    private String businessName;

    /**
     * 生成功能名
     */
    @Schema(description = "生成功能名", example = "用户管理")
    private String functionName;

    /**
     * 生成功能作者
     */
    @Schema(description = "生成功能作者", example = "Chuang")
    private String functionAuthor;

    /**
     * 创建者
     */
    @Schema(description = "创建者", example = "admin")
    private String createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-05-20 11:01:00")
    private Date createTime;
}
