package cn.zhangchuangla.quartz.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 定时任务日志查询请求
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务日志查询请求")
public class SysJobLogQueryRequest extends BasePageRequest {

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
     * 执行状态（0正常 1失败）
     */
    @Schema(description = "执行状态（0正常 1失败）")
    private Integer status;

    /**
     * 开始时间-开始
     */
    @Schema(description = "开始时间-开始")
    private Date startTimeBegin;

    /**
     * 开始时间-结束
     */
    @Schema(description = "开始时间-结束")
    private Date startTimeEnd;
}
