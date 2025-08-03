package cn.zhangchuangla.quartz.model.vo;

import cn.zhangchuangla.common.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 定时任务视图对象
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务视图对象")
public class SysJobVo {

    /**
     * 任务ID
     */
    @Schema(description = "任务ID")
    @Excel(name = "任务ID")
    private Long jobId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    @Excel(name = "任务名称")
    private String jobName;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串")
    @Excel(name = "调用目标字符串")
    private String invokeTarget;

    /**
     * 调度策略（0=Cron表达式 1=固定频率 2=固定延迟 3=一次性执行）
     */
    @Schema(description = "调度策略")
    @Excel(name = "调度策略")
    private Integer scheduleType;

    /**
     * 调度策略描述
     */
    @Schema(description = "调度策略描述")
    @Excel(name = "调度策略描述")
    private String scheduleTypeDesc;

    /**
     * cron执行表达式
     */
    @Schema(description = "cron执行表达式")
    @Excel(name = "cron执行表达式")
    private String cronExpression;

    /**
     * 固定频率间隔（毫秒）
     */
    @Schema(description = "固定频率间隔（毫秒）")
    @Excel(name = "固定频率间隔（毫秒）")
    private Long fixedRate;

    /**
     * 固定延迟间隔（毫秒）
     */
    @Schema(description = "固定延迟间隔（毫秒）")
    @Excel(name = "固定延迟间隔（毫秒）")
    private Long fixedDelay;

    /**
     * 初始延迟时间（毫秒）
     */
    @Schema(description = "初始延迟时间（毫秒）")
    @Excel(name = "初始延迟时间（毫秒）")
    private Long initialDelay;

    /**
     * 计划执行错误策略（0=默认 1=立即执行 2=执行一次 3=放弃执行）
     */
    @Schema(description = "计划执行错误策略")
    @Excel(name = "计划执行错误策略")
    private Integer misfirePolicy;

    /**
     * 失火策略描述
     */
    @Schema(description = "失火策略描述")
    @Excel(name = "失火策略描述")
    private String misfirePolicyDesc;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @Schema(description = "是否并发执行（0允许 1禁止）")
    @Excel(name = "是否并发执行")
    private Integer concurrent;

    /**
     * 任务状态（0正常 1暂停）
     */
    @Schema(description = "任务状态（0正常 1暂停）")
    @Excel(name = "任务状态")
    private Integer status;

    /**
     * 任务状态描述
     */
    @Schema(description = "任务状态描述")
    @Excel(name = "任务状态描述")
    private String statusDesc;

    /**
     * 任务优先级
     */
    @Schema(description = "任务优先级")
    @Excel(name = "任务优先级")
    private Integer priority;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述")
    @Excel(name = "任务描述")
    private String description;

    /**
     * 任务参数
     */
    @Schema(description = "任务参数")
    @Excel(name = "任务参数")
    private String jobData;

    /**
     * 依赖任务ID（多个用逗号分隔）
     */
    @Schema(description = "依赖任务ID")
    @Excel(name = "依赖任务ID")
    private String dependentJobIds;

    /**
     * 最大重试次数
     */
    @Schema(description = "最大重试次数")
    @Excel(name = "最大重试次数")
    private Integer maxRetryCount;

    /**
     * 重试间隔（毫秒）
     */
    @Schema(description = "重试间隔（毫秒）")
    @Excel(name = "重试间隔（毫秒）")
    private Long retryInterval;

    /**
     * 超时时间（毫秒）
     */
    @Schema(description = "超时时间（毫秒）")
    @Excel(name = "超时时间（毫秒）")
    private Long timeout;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "结束时间")
    private Date endTime;

    /**
     * 下次执行时间
     */
    @Schema(description = "下次执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "下次执行时间")
    private Date nextFireTime;

    /**
     * 上次执行时间
     */
    @Schema(description = "上次执行时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "上次执行时间")
    private Date previousFireTime;
}
