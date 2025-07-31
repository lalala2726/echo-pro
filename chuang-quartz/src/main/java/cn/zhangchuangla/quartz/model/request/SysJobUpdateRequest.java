package cn.zhangchuangla.quartz.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务更新请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务更新请求")
public class SysJobUpdateRequest {

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String jobName;

    /**
     * 调用目标字符串
     */
    @NotBlank(message = "调用目标不能为空")
    @Schema(description = "调用目标字符串", requiredMode = Schema.RequiredMode.REQUIRED)
    private String invokeTarget;

    /**
     * 调度策略（0=Cron表达式 1=固定频率 2=固定延迟 3=一次性执行）
     */
    @NotNull(message = "调度策略不能为空")
    @Schema(description = "调度策略", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer scheduleType;

    /**
     * cron执行表达式
     */
    @Schema(description = "cron执行表达式")
    private String cronExpression;

    /**
     * 固定频率间隔（毫秒）
     */
    @Schema(description = "固定频率间隔（毫秒）")
    private Long fixedRate;

    /**
     * 固定延迟间隔（毫秒）
     */
    @Schema(description = "固定延迟间隔（毫秒）")
    private Long fixedDelay;

    /**
     * 初始延迟时间（毫秒）
     */
    @Schema(description = "初始延迟时间（毫秒）")
    private Long initialDelay;

    /**
     * 计划执行错误策略（0=默认 1=立即执行 2=执行一次 3=放弃执行）
     */
    @Schema(description = "计划执行错误策略")
    private Integer misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Schema(description = "是否并发执行（0允许 1禁止）")
    private Integer concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Schema(description = "任务状态（0正常 1暂停）")
    private Integer status;

    /**
     * 任务优先级
     */
    @Schema(description = "任务优先级")
    private Integer priority;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    private String description;

    /**
     * 任务参数
     */
    @Schema(description = "任务参数")
    private String jobData;

    /**
     * 依赖任务ID（多个用逗号分隔）
     */
    @Schema(description = "依赖任务ID")
    private String dependentJobIds;

    /**
     * 最大重试次数
     */
    @Schema(description = "最大重试次数")
    private Integer maxRetryCount;

    /**
     * 重试间隔（毫秒）
     */
    @Schema(description = "重试间隔（毫秒）")
    private Long retryInterval;

    /**
     * 超时时间（毫秒）
     */
    @Schema(description = "超时时间（毫秒）")
    private Long timeout;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Date endTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
