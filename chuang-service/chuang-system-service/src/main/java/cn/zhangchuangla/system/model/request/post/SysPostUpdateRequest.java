package cn.zhangchuangla.system.model.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 岗位表
 */
@Data
@Schema(name = "修改岗位请求类")
public class SysPostUpdateRequest {

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "岗位ID不能为空")
    private Integer postId;

    /**
     * 岗位编码
     */
    @Schema(description = "岗位编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String postName;

    /**
     * 排序
     */
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(description = "状态(0-正常,1-停用)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;


    /**
     * 是否删除(0-未删除,1-已删除)
     */
    private Integer isDeleted;
}
