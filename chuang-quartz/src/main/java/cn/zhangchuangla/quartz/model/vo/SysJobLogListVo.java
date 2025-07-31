package cn.zhangchuangla.quartz.model.vo;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 定时任务日志视图对象
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务日志视图对象")
public class SysJobLogListVo extends BaseVo {


    /**
     * 任务日志ID
     */
    @Schema(description = "任务日志ID")
    private Long jobLogId;

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
     * 调度类型（0=Cron表达式 1=固定频率 2=固定延迟 3=一次性执行）
     */
    @Schema(description = "调度类型")
    private Integer scheduleType;

    /**
     * 调度类型描述
     */
    @Schema(description = "调度类型描述")
    private String scheduleTypeDesc;

    /**
     * 任务状态（0=正常 1=暂停）
     */
    @Schema(description = "任务状态")
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
     * 上次执行时间
     */
    @Schema(description = "上次执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date previousFireTime;

    /**
     * 下次执行时间
     */
    @Schema(description = "下次执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date nextFireTime;
}
