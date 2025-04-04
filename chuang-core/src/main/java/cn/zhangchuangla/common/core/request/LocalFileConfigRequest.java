package cn.zhangchuangla.common.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "本地文件配置实体类")
public class LocalFileConfigRequest {

    /**
     * 文件配置标志
     */
    @Schema(description = "文件配置标志", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件配置标志不能为空")
    private String storageName;


    /**
     * 参数键名
     */
    @Schema(description = "参数键名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "参数键名不能为空")
    private String storageKey;


    /**
     * 文件上传路径
     */
    @Schema(description = "文件上传路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件上传路径不能为空")
    private String uploadPath;


}
