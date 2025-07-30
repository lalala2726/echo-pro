package cn.zhangchuangla.quartz.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 定时任务组批量操作请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务组批量操作请求")
public class SysJobGroupBatchRequest {

    /**
     * 任务组ID列表
     */
    @NotEmpty(message = "任务组ID列表不能为空")
    @Schema(description = "任务组ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> ids;

    /**
     * 操作类型（enable-启用，disable-停用，delete-删除）
     */
    @NotNull(message = "操作类型不能为空")
    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"enable", "disable", "delete"})
    private String operation;
}
