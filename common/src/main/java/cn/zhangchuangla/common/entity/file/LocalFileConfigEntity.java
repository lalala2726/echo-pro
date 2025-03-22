package cn.zhangchuangla.common.entity.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 09:56
 */
@Data
@Schema(description = "本地文件配置实体类")
public class LocalFileConfigEntity {

    /**
     * 文件上传路径
     */
    @Schema(description = "文件上传路径", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件上传路径不能为空")
    private String uploadPath;

    public LocalFileConfigEntity(String uploadPath) {
        this.uploadPath = uploadPath;
    }

}
