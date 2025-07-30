package cn.zhangchuangla.quartz.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 定时任务批量操作请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务批量操作请求")
public class SysJobBatchRequest {

    /**
     * 任务ID列表
     */
    @NotEmpty(message = "任务ID列表不能为空")
    @Schema(description = "任务ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> jobIds;

    /**
     * 操作类型（start=启动, pause=暂停, resume=恢复, delete=删除）
     */
    @Schema(description = "操作类型")
    private String operation;
}
