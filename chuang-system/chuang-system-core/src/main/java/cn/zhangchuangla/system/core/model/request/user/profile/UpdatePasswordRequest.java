package cn.zhangchuangla.system.core.model.request.user.profile;

import cn.zhangchuangla.common.core.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = RegularConstants.User.USERNAME, message = "密码必须包含数字和大小写英文，可包含特殊符号，长度在8到20之间")
    private String newPassword;
}

