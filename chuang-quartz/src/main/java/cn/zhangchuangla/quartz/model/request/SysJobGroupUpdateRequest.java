package cn.zhangchuangla.quartz.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 定时任务组更新请求
 *
 * @author Chuang
 */
@Data
@Schema(description = "定时任务组更新请求")
public class SysJobGroupUpdateRequest {

    /**
     * 任务组ID
     */
    @NotNull(message = "任务组ID不能为空")
    @Schema(description = "任务组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 任务组名称
     */
    @NotBlank(message = "任务组名称不能为空")
    @Size(max = 50, message = "任务组名称不能超过50个字符")
    @Schema(description = "任务组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统任务组")
    private String groupName;

    /**
     * 任务组编码
     */
    @NotBlank(message = "任务组编码不能为空")
    @Size(max = 50, message = "任务组编码不能超过50个字符")
    @Schema(description = "任务组编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SYSTEM_GROUP")
    private String groupCode;

    /**
     * 任务组描述
     */
    @Size(max = 200, message = "任务组描述不能超过200个字符")
    @Schema(description = "任务组描述", example = "系统相关的定时任务组")
    private String groupDescription;

    /**
     * 状态（0正常 1停用）
     */
    @Schema(description = "状态（0正常 1停用）", example = "0")
    private Integer status;

    /**
     * 排序
     */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注不能超过500个字符")
    @Schema(description = "备注")
    private String remark;
}
