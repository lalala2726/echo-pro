package cn.zhangchuangla.system.storage.model.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 存储配置请求基本类
 *
 * @author Chuang
 * <p>
 * created on 2025/7/20 21:26
 */
@Data
public class StorageConfigBaseSaveRequest {

    /**
     * 存储配置名称
     */
    @Schema(description = "文件配置标志", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京阿里云对象存储")
    @NotBlank(message = "文件配置标志不能为空")
    private String storageName;

    /**
     * 参数键名
     */
    @Schema(description = "参数键名", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "BeijingAliyunCos001")
    @NotBlank(message = "参数键名不能为空")
    private String storageKey;
}
