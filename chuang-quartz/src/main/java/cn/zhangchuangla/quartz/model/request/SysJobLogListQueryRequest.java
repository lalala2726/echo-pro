package cn.zhangchuangla.quartz.model.request;

import cn.zhangchuangla.common.core.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务调度日志表
 *
 * @author Chuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysJobLogListQueryRequest extends BasePageRequest {

    /**
     * 任务日志ID
     */
    @Schema(description = "任务日志ID")
    private Long jobLogId;

    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
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
    private String invokeTarget;

    /**
     * 日志信息
     */
    @Schema(description = "日志信息")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败）
     */
    @Schema(description = "执行状态（0正常 1失败）")
    private String status;

    /**
     * 异常信息
     */
    @Schema(description = "异常信息")
    private String exceptionInfo;

}
