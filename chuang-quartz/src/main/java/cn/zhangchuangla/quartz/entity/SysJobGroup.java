package cn.zhangchuangla.quartz.entity;

import cn.zhangchuangla.common.core.entity.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务组表
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_job_group")
@Schema(description = "定时任务组")
public class SysJobGroup extends BaseEntity {

    /**
     * 任务组ID
     */
    @TableId(type = IdType.AUTO)
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
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;
}
