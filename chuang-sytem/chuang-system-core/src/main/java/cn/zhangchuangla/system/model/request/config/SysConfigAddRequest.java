package cn.zhangchuangla.system.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置表
 */
@Data
public class SysConfigAddRequest {

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    /**
     * 参数键名
     */
    @NotBlank(message = "参数键名不能为空")
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    @Schema(description = "参数键值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String configValue;

}
