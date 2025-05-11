package cn.zhangchuangla.system.model.vo.post;

import cn.zhangchuangla.common.base.BaseVo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "岗位视图对象")
public class SysPostVo extends BaseVo {

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
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

}
