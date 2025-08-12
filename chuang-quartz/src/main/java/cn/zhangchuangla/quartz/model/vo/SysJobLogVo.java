package cn.zhangchuangla.quartz.model.vo;

import cn.zhangchuangla.common.core.entity.base.BaseVo;
import cn.zhangchuangla.common.excel.annotation.Excel;
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
public class SysJobLogVo extends BaseVo {

    /**
     * 任务日志ID
     */
    @Schema(description = "任务日志ID", type = "string", example = "1")
    @Excel(name = "任务日志ID")
    private Long jobLogId;

    /**
     * 任务ID
     */
    @Schema(description = "任务ID", type = "string", example = "1")
    @Excel(name = "任务ID")
    private Long jobId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称", type = "string", example = "用户登录任务")
    @Excel(name = "任务名称")
    private String jobName;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串", type = "string", example = "com.example.service.TaskService.execute()")
    @Excel(name = "调用目标字符串")
    private String invokeTarget;

    /**
     * 任务参数
     */
    @Schema(description = "任务参数", type = "string", example = "{\"userId\": 123}")
    @Excel(name = "任务参数")
    private String jobData;

    /**
     * 日志信息
     */
    @Schema(description = "日志信息", type = "string", example = "任务执行成功")
    @Excel(name = "日志信息")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @Schema(description = "执行状态（0正常 1失败）", type = "integer", example = "0")
    @Excel(name = "执行状态 (0正常 1失败)")
    private Integer status;

    /**
     * 执行状态描述
     */
    @Schema(description = "执行状态描述", type = "string", example = "正常")
    @Excel(name = "执行状态描述")
    private String statusDesc;

    /**
     * 异常信息
     */
    @Schema(description = "异常信息", type = "string", example = "空指针异常")
    @Excel(name = "异常信息")
    private String exceptionInfo;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间", type = "string", example = "2023-04-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", type = "string", example = "2023-04-01 12:00:05")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "结束时间")
    private Date endTime;

    /**
     * 执行耗时（毫秒）
     */
    @Schema(description = "执行耗时（毫秒）", type = "string", example = "5000")
    @Excel(name = "执行耗时（毫秒）")
    private Long executeTime;

    /**
     * 服务器IP
     */
    @Schema(description = "服务器IP", type = "string", example = "192.168.1.1")
    @Excel(name = "服务器IP")
    private String serverIp;

    /**
     * 服务器名称
     */
    @Schema(description = "服务器名称", type = "string", example = "server-01")
    @Excel(name = "服务器名称")
    private String serverName;

    /**
     * 重试次数
     */
    @Schema(description = "重试次数", type = "integer", example = "0")
    @Excel(name = "重试次数")
    private Integer retryCount;

    /**
     * 触发器类型
     */
    @Schema(description = "触发器类型", type = "string", example = "CRON")
    @Excel(name = "触发器类型")
    private String triggerType;
}
