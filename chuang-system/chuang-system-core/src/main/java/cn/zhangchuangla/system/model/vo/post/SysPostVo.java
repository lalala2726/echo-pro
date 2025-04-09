package cn.zhangchuangla.system.model.vo.post;

import cn.zhangchuangla.common.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "岗位视图对象")
public class SysPostVo extends BaseVO {

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID")
    private Integer postId;

    /**
     * 岗位编码
     */
    @Schema(description = "岗位编码")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称")
    private String postName;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(description = "状态(0-正常,1-停用)")
    private Integer status;


    /**
     * 是否删除(0-未删除,1-已删除)
     */
    private Integer isDeleted;
}
