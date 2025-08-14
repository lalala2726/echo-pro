package cn.zhangchuangla.framework.model.request;

import cn.zhangchuangla.common.core.constant.RegularConstants;
import cn.zhangchuangla.common.core.enums.DeviceType;
import com.fasterxml.jackson.annotation.JsonSetter;
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
     * 验证码
     */
    @NotBlank(message = "验证码不能为空!")
    @Schema(description = "验证码", type = "string", example = "123456")
    private String code;

    /**
     * 验证码唯一标识
     */
    @NotBlank(message = "验证码唯一标识不能为空!")
    @Schema(description = "普通验证码唯一标识", type = "string", example = "1001")
    private String uuid;


    /**
     * 设备类型
     */
    @Schema(description = "设备类型", type = "string", allowableValues = {"web", "pc", "mobile", "miniProgram", "unknown"}, defaultValue = "WEB")
    private DeviceType deviceType = DeviceType.WEB;


    /**
     * 设置设备类型，支持字符串自动转换为枚举
     * 如果传入的字符串不是枚举中的值，则使用默认的 UNKNOWN
     *
     * @param deviceTypeStr 设备类型字符串
     */
    @JsonSetter("deviceType")
    public void setDeviceType(String deviceTypeStr) {
        this.deviceType = DeviceType.getByValue(deviceTypeStr);
    }
}
