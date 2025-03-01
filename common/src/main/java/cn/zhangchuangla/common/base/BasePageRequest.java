package cn.zhangchuangla.common.base;

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
    @Schema(description = "当前页码", defaultValue = "1", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long pageNum = 1L;

    /**
     * 每页数量
     */
    @Schema(description = "当前页码", defaultValue = "10", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Long pageSize = 10L;
}
