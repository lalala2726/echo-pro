package cn.zhangchuangla.system.model.request.post;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "岗位信息列表请求类")
public class SysPostListRequest extends BasePageRequest {

    /**
     * 岗位ID
     */
    @Schema(name = "岗位ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "岗位ID不能小于1")
    private Integer postId;

    /**
     * 岗位编码
     */
    @Schema(name = "岗位编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 64, min = 1, message = "岗位编码长度在1-64个字符")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Size(max = 100, min = 1, message = "岗位名称长度在1-100个字符")
    private String postName;

    /**
     * 排序
     */
    @Schema(name = "排序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Min(value = 1, message = "排序不能小于1")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(name = "状态(0-正常,1-停用)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Max(value = 1, message = "状态(0-正常,1-停用)不能大于1")
    @Min(value = 0, message = "状态(0-正常,1-停用)不能小于0")
    private Integer status;

}
