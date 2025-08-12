package cn.zhangchuangla.quartz.model.vo;

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
@Schema(description = "定时任务列表视图对象")
public class SysJobListVo {

    /**
     * 任务ID
     */
    @Schema(description = "任务ID", type = "integer", example = "1")
    private Long jobId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称", type = "string", example = "数据备份任务")
    private String jobName;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串", type = "string", example = "quartzTask.run")
    private String invokeTarget;

    /**
     * 调度策略
     */
    @Schema(description = "调度策略", type = "integer", example = "1")
    private Integer scheduleType;

    /**
     * 调度策略描述
     */
    @Schema(description = "任务状态（0正常 1暂停）", type = "integer", example = "0")
    private Integer status;

    /**
     * 任务状态描述
     */
    @Schema(description = "任务状态描述", type = "string", example = "正常")
    private String statusDesc;

    /**
     * 任务描述
     */
    @Schema(description = "任务描述", type = "string", example = "每日数据备份")
    private String description;

    /**
     * 任务参数
     */
    @Schema(description = "下次执行时间", type = "string", example = "2024-06-15 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextFireTime;

    /**
     * 上次执行时间
     */
    @Schema(description = "上次执行时间", type = "string", example = "2024-06-14 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date previousFireTime;
}
