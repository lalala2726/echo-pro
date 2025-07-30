package cn.zhangchuangla.system.core.model.vo.post;

import cn.zhangchuangla.common.excel.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岗位表
 *
 * @author Chuang
 */
@Data
@Schema(name = "岗位列表视图对象", description = "用于展示岗位列表的视图对象")
public class SysPostListVo {

    /**
     * 岗位ID
     */
    @Schema(name = "岗位ID")
    @Excel(name = "岗位ID")
    private Long id;

    /**
     * 岗位编码
     */
    @Schema(name = "岗位编码")
    @Excel(name = "岗位编码")
    private String postCode;

    /**
     * 岗位名称
     */
    @Schema(name = "岗位名称")
    @Excel(name = "岗位名称")
    private String postName;

    /**
     * 排序
     */
    @Schema(name = "排序")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 状态(0-正常,1-停用)
     */
    @Schema(name = "状态(0-正常,1-停用)")
    @Excel(name = "状态(0-正常,1-停用)")
    private Integer status;

}
