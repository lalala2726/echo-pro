package cn.zhangchuangla.generator.model.request;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 数据库表查询对象
 *
 * @author Chuang
 * <p>
 * created on 2025/5/20 13:15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "数据库表查询对象", description = "用户查询当前数据库中表")
public class DatabaseTableQueryRequest extends BasePageRequest {

    /**
     * 表名
     */
    @Schema(description = "表名", type = "string", example = "sys_user")
    private String tableName;

    /**
     * 表描述
     */
    @Schema(description = "表描述", type = "string", example = "用户表")
    private String tableComment;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", type = "string", example = "2025-05-20 13:15:00")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间", type = "string", example = "2025-05-20 13:15:00")
    private LocalDateTime updateTime;
}
