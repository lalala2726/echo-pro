package cn.zhangchuangla.infrastructure.model.request;

import cn.zhangchuangla.common.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:56
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空!")
    @Size(min = 5, max = 20, message = "用户名长度在5-20位之间!")
    @Pattern(regexp = RegularConstants.User.username, message = "用户名只能是字母、数字、下划线、减号!")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空!")
    @Size(min = 8, max = 20, message = "密码长度在8-20位之间!")
    @Pattern(regexp = RegularConstants.User.password, message = "密码格式不正确!")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * 验证码KEY
     */
//    @NotBlank(message = "验证码KEY不能为空!")
    @Schema(description = "验证码KEY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaKey;

    /**
     * 验证码
     */
//    @NotBlank(message = "验证码不能为空!")
    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaCode;
}
