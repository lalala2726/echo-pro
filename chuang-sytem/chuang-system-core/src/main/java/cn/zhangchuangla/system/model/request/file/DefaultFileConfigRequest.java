package cn.zhangchuangla.system.model.request.file;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/3/21 20:57
 */
@Data
public class DefaultFileConfigRequest {

    /**
     * 文件上传方式
     */
    @NotBlank(message = "文件上传方式不能为空")
    private String fileUploadType;

}
