package cn.zhangchuangla.system.model.request.post;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private Integer postId;

    /**
     * 岗位编码
     */
    @Schema(name = "岗位编码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String postName;

    /**
     * 排序
     */
    @Schema(name = "排序", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(name = "状态(0-正常,1-停用)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

}
