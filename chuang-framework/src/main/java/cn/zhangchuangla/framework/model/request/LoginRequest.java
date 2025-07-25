package cn.zhangchuangla.framework.model.request;

import cn.zhangchuangla.common.core.constant.RegularConstants;
import cn.zhangchuangla.common.core.enums.DeviceType;
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
    @Pattern(regexp = RegularConstants.User.USERNAME, message = "用户名只能是字母、数字、下划线、减号!")
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin123")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空!")
    @Size(min = 8, max = 20, message = "密码长度在8-20位之间!")
    @Pattern(regexp = RegularConstants.User.PASSWORD, message = "密码格式不正确!")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "Abc123456!")
    private String password;


    /**
     * 设备类型
     */
    @Schema(description = "设备类型", type = "string", allowableValues = {"web", "pc", "ios", "android"}, defaultValue = "PC",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "PC")
    private String deviceType = DeviceType.WEB.getValue();
}
