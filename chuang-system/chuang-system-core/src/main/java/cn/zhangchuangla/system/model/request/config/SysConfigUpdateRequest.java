package cn.zhangchuangla.system.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统配置表
 *
 * @author Chuang
 */
@Data
@Schema(name = "系统配置更新请求类", description = "系统配置更新请求类")
public class SysConfigUpdateRequest {


    /**
     * 主键ID
     */
    @Schema(description = "主键ID", type = "integer", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @Min(value = 1, message = "主键ID不能小于1")
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "系统名称")
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    /**
     * 参数键名
     */
    @NotBlank(message = "参数键名不能为空")
    @Schema(description = "参数键名", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys.name")
    private String configKey;

    /**
     * 参数键值
     */
    @NotBlank(message = "参数键值不能为空")
    @Schema(description = "参数键值", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "{\"key\":\"value\"}")
    private String configValue;

}
