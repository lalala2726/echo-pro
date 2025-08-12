package cn.zhangchuangla.system.core.model.vo.post;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "岗位视图对象", description = "用于展示岗位的视图对象")
public class SysPostVo extends BaseVo {

    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", type = "integer", example = "1")
    private Long id;

    /**
     * 岗位编码
     */
    @Schema(description = "岗位编码", type = "string", example = "POST001")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(description = "岗位名称", type = "string", example = "系统管理员")
    private String postName;

    /**
     * 排序
     */
    @Schema(description = "排序", type = "integer", example = "1")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(description = "状态(0-正常,1-停用)", type = "integer", example = "0")
    private Integer status;

}
