package cn.zhangchuangla.quartz.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务视图对象
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务列表视图对象")
public class SysJobListVo {

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    private Long jobId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String jobName;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串")
    private String invokeTarget;

    /**
     * 调度策略
     */
    @Schema(description = "调度策略")
    private Integer scheduleType;

    /**
     * 调度策略描述
     */
    @Schema(description = "任务状态（0正常 1暂停）")
    private Integer status;

    /**
     * 任务状态描述
     */
    @Schema(description = "任务状态描述")
    private String statusDesc;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String description;

    /**
     * 任务参数
     */
    @Schema(description = "下次执行时间")
    private Date nextFireTime;

    /**
     * 上次执行时间
     */
    @Schema(description = "上次执行时间")
    private Date previousFireTime;
}
