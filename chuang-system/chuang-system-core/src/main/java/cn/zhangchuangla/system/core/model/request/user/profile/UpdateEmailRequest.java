package cn.zhangchuangla.system.core.model.request.user.profile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/3 23:52
 */
@Data
@Schema(name = "修改邮箱请求类", description = "用于修改邮箱时")
public class UpdateEmailRequest {

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "新邮箱", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin@emai.com")
    private String email;

    /**
     * 验证码
     */
    @Schema(description = "验证码", type = "string", requiredMode = Schema.RequiredMode.AUTO, example = "123456")
    private String code;

}
