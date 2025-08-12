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
    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, type = "integer", example = "1")
    private Long jobId;

    /**
     * 任务名称
     */
    @NotBlank(message = "任务名称不能为空")
    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", example = "数据备份任务")
    private String jobName;

    /**
     * 调用目标字符串
     */
    @NotBlank(message = "调用目标不能为空")
    @Schema(description = "调用目标字符串", requiredMode = Schema.RequiredMode.REQUIRED, type = "string", example = "quartzTaskService.executeBackupTask()")
    private String invokeTarget;

    /**
     * 调度策略（0=Cron表达式 1=固定频率 2=固定延迟 3=一次性执行）
     */
    @NotNull(message = "调度策略不能为空")
    @Schema(description = "调度策略（0=Cron表达式 1=固定频率 2=固定延迟 3=一次性执行）", requiredMode = Schema.RequiredMode.REQUIRED, type = "integer", example = "0")
    private Integer scheduleType;

    /**
     * cron执行表达式
     */
    @Schema(description = "cron执行表达式", type = "string", example = "0 0 2 * * ?")
    private String cronExpression;

    /**
     * 固定频率间隔（毫秒）
     */
    @Schema(description = "固定频率间隔（毫秒）", type = "integer", example = "3600000")
    private Long fixedRate;

    /**
     * 固定延迟间隔（毫秒）
     */
    @Schema(description = "固定延迟间隔（毫秒）", type = "integer", example = "1800000")
    private Long fixedDelay;

    /**
     * 初始延迟时间（毫秒）
     */
    @Schema(description = "初始延迟时间（毫秒）", type = "integer", example = "5000")
    private Long initialDelay;

    /**
     * 计划执行错误策略（0=默认 1=立即执行 2=执行一次 3=放弃执行）
     */
    @Schema(description = "计划执行错误策略（0=默认 1=立即执行 2=执行一次 3=放弃执行）", type = "integer", example = "1")
    private Integer misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Schema(description = "是否并发执行（0允许 1禁止）", type = "integer", example = "0")
    private Integer concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Schema(description = "任务状态（0正常 1暂停）", type = "integer", example = "0")
    private Integer status;

    /**
     * 任务优先级
     */
    @Schema(description = "任务优先级", type = "integer", example = "5")
    private Integer priority;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述", type = "string", example = "每日凌晨2点执行数据备份")
    private String description;

    /**
     * 任务参数
     */
    @Schema(description = "任务参数", type = "string", example = "{\"database\":\"user_db\",\"table\":\"user_info\"}")
    private String jobData;

    /**
     * 依赖任务ID（多个用逗号分隔）
     */
    @Schema(description = "依赖任务ID（多个用逗号分隔）", type = "string", example = "2,3,5")
    private String dependentJobIds;

    /**
     * 最大重试次数
     */
    @Schema(description = "最大重试次数", type = "integer", example = "3")
    private Integer maxRetryCount;

    /**
     * 重试间隔（毫秒）
     */
    @Schema(description = "重试间隔（毫秒）", type = "integer", example = "60000")
    private Long retryInterval;

    /**
     * 超时时间（毫秒）
     */
    @Schema(description = "超时时间（毫秒）", type = "integer", example = "300000")
    private Long timeout;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", type = "string", format = "date-time", example = "2023-01-01T00:00:00")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", type = "string", format = "date-time", example = "2023-12-31T23:59:59")
    private Date endTime;

    /**
     * 备注
     */
    @Schema(description = "备注", type = "string", example = "重要业务数据备份")
    private String remark;
}
