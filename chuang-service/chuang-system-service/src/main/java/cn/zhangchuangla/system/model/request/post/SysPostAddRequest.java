package cn.zhangchuangla.system.model.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 岗位表
 */
@Data
@Schema(name = "添加岗位请求类")
public class SysPostAddRequest {


    /**
     * 岗位编码
     */
    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "岗位编码必须大于等于1")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    /**
     * 排序
     */
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Max(value = 999, message = "排序必须小于999")
    @Min(value = 0, message = "排序必须大于等于0")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(description = "状态(0-正常,1-停用)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;


}
