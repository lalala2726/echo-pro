package cn.zhangchuangla.generator.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据库表信息
 *
 * @author Chuang
 */
@Data
@Schema(description = "数据库表信息")
public class DbTableVO {

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String updateTime;
}