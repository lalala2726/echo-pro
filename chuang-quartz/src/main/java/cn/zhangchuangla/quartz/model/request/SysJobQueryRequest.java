package cn.zhangchuangla.quartz.model.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 定时任务查询请求
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务查询请求")
public class SysJobQueryRequest extends BasePageRequest {

    /**
     * 任务名称
     */
    @Schema(description = "任务名称", type = "string", example = "用户登录任务")
    private String jobName;

    /**
     * 任务状态
     */
    @Schema(description = "任务状态", type = "integer", example = "1")
    private Integer status;

    /**
     * 调用目标字符串
     */
    @Schema(description = "调用目标字符串", type = "string", example = "user.login")
    private String invokeTarget;

    /**
     * 调度策略
     */
    @Schema(description = "调度策略", type = "integer", example = "0")
    private Integer scheduleType;
}
