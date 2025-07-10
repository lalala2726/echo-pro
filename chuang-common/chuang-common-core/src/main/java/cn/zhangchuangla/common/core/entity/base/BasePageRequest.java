package cn.zhangchuangla.common.core.entity.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 11:03
 */
@Data
public class BasePageRequest {

    /**
     * 当前页码
     */

    @Schema(description = "当前页码", type = "integer", format = "int32", defaultValue = "1", requiredMode = Schema.RequiredMode.AUTO)
    private int pageNum = 1;

    /**
     * 每页数量
     */
    @Schema(description = "当前页码", type = "integer", format = "int32", defaultValue = "10", requiredMode = Schema.RequiredMode.AUTO)
    private int pageSize = 10;
}
