package cn.zhangchuangla.framework.model.request;

import cn.zhangchuangla.common.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


/**
 * 注册参数类, 用于注册时使用的请求类
 *
 * @author Chuang
 * <p>
 * created on 2025/2/19 15:00
 */
@Data
@Schema(name = "注册请求类", description = "用于注册时使用的请求类")
public class RegisterRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)

    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = RegularConstants.User.PASSWORD, message = "密码格式不正确")
    private String password;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Pattern(regexp = RegularConstants.User.EMAIL, message = "邮箱格式不正确")
    private String email;

    /**
     * 手机
     */
    @Pattern(regexp = RegularConstants.User.PHONE, message = "手机格式不正确")
    @Schema(description = "手机")
    private String phone;
}
