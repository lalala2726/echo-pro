package cn.zhangchuangla.generator.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于数据库表
 *
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:22
 */
@Data
@Schema(name = "数据库表", description = "数据库表")
public class DatabaseTableVo {

    /**
     * 表名
     */
    @Schema(description = "表明", type = "string")
    private String tableName;

    /**
     * 表注释
     */
    @Schema(description = "表注释", type = "string")
    private String tableComment;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", type = "string")
    private LocalDateTime updateTime;
}
