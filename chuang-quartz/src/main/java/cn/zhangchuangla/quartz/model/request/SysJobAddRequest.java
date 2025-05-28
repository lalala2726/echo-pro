package cn.zhangchuangla.quartz.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 定时任务调度表
 *
 * @author Chuang
 */
@Data
@Schema(name = "定时任务调度表", description = "定时任务调度表")
public class SysJobAddRequest {


    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    @NotNull(message = "任务名称不能为空")
    private String jobName;

    /**
     * 任务组名
     */
    @Schema(description = "任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串")
    @NotNull(message = "调用目标字符串不能为空")
    private String invokeTarget;

    /**
     * cron执行表达式
     */
    @Schema(description = "cron执行表达式")
    @NotNull(message = "cron执行表达式不能为空")
    private String cronExpression;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @Schema(description = "计划执行错误策略（1立即执行 2执行一次 3放弃执行）")
    private String misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Schema(description = "是否并发执行（0允许 1禁止）")
    private String concurrent;

    /**
     * 状态（0正常 1暂停）
     */
    @Schema(description = "状态（0正常 1暂停）")
    private String status;
}
