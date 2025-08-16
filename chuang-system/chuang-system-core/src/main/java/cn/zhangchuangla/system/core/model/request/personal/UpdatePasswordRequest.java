package cn.zhangchuangla.system.core.model.request.personal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/7 22:07
 */
@Data
@Schema(name = "修改密码请求类", description = "用于修改密码时")
public class UpdatePasswordRequest {

    /**
     * 旧密码
     */
    @Schema(description = "旧密码", example = "Abc123456!")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     */
    @Schema(description = "新密码", example = "Abc123456!")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

