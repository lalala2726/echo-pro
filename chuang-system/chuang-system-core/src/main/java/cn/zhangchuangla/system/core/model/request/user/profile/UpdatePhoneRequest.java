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
@Schema(name = "修改手机号请求类", description = "用于修改手机号时")
public class UpdatePhoneRequest {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", type = "string", requiredMode = Schema.RequiredMode.REQUIRED, example = "18888888888")
    private String phone;

    /**
     * 验证码
     */
    @Schema(description = "验证码", type = "string", requiredMode = Schema.RequiredMode.AUTO, example = "123456")
    private String code;

    /**
     * 验证码的uuid
     */
    @NotBlank(message = "uuid不能为空")
    @Schema(description = "uuid", type = "string", requiredMode = Schema.RequiredMode.AUTO, example = "123456")
    private String uuid;

}
