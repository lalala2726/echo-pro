package cn.zhangchuangla.generator.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/24 01:25
 */
@Data
@Schema(name = "子表类型请求类", description = "用于修改子表类型信息")
public class SubTableTypeRequest {

    /**
     * 子表名
     */
    @Schema(description = "子表名", example = "user_info")
    private String subTableName;

    /**
     * 子表外键名
     */
    @Schema(description = "子表外键名", example = "user_id")
    private String subTableFkName;
}
