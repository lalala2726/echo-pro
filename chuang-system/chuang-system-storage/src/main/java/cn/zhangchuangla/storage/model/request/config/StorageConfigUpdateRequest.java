package cn.zhangchuangla.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文件配置表
 */
@Data
@Schema(description = "文件配置修改请求类")
public class StorageConfigUpdateRequest {

    /**
     * ID
     */
    @Schema(description = "参数ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "参数ID不能小于1")
    private Integer id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数名称不能为空")
    private String storageName;

    /**
     * 参数键名
     */
    @NotBlank(message = "参数键名不能为空")
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String storageKey;

    /**
     * 存储类型
     */
    @NotBlank(message = "存储类型不能为空")
    @Schema(description = "存储类型", requiredMode = Schema.RequiredMode.REQUIRED)
    private String storageType;

    /**
     * 存储值
     */
    @NotBlank(message = "存储值不能为空")
    @Schema(description = "存储值", requiredMode = Schema.RequiredMode.REQUIRED)
    private String storageValue;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;

}
