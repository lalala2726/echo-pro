package cn.zhangchuangla.system.model.request.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 */
@Data
public class MetaRequest {

    /**
     * 在面包屑中隐藏
     */
    @Schema(description = "在面包屑中隐藏", type = "boolean", example = "true")
    private Boolean hideInBreadcrumb;

    /**
     * 在标签栏中隐藏
     */
    @Schema(description = "在标签栏中隐藏", type = "boolean", example = "true")
    private Boolean hideInTab;

}
