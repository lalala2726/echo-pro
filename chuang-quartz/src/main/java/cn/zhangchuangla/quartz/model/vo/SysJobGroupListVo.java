package cn.zhangchuangla.quartz.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务组列表视图对象
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务组列表视图对象")
public class SysJobGroupListVo {

    /**
     * 任务组ID
     */
    @Schema(description = "任务组ID")
    private Long id;

    /**
     * 任务组名称
     */
    @Schema(description = "任务组名称")
    private String groupName;

    /**
     * 任务组编码
     */
    @Schema(description = "任务组编码")
    private String groupCode;

    /**
     * 任务组描述
     */
    @Schema(description = "任务组描述")
    private String groupDescription;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）")
    private Integer status;

    /**
     * 状态描述
     */
    @Schema(description = "状态描述")
    private String statusDesc;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 任务数量
     */
    @Schema(description = "任务数量")
    private Integer jobCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
