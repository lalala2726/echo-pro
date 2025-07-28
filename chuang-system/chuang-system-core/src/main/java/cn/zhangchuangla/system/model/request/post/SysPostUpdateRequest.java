package cn.zhangchuangla.system.model.request.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 修改岗位请求对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "修改岗位请求对象", description = "修改岗位请求对象")
public class SysPostUpdateRequest {

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", example = "1", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * 岗位编码
     */
    @Schema(description = "岗位编码", example = "POST001", type = "string")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", example = "管理员", type = "string")
    private String postName;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "100", type = "integer")
    @Range(min = 0, max = 999, message = "排序必须在0到999之间")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(description = "状态(0-正常,1-停用)", example = "0", type = "integer")
    @Range(min = 0, max = 1, message = "状态只能为0或1")
    private Integer status;

}
