package cn.zhangchuangla.system.model.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 修改用户信息参数, 用于修改用户时使用
 */
@Data
@Schema(name = "修改用户请求类", description = "用于修改用户时")
public class UpdateUserRequest {

    /**
     * ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色信息
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Long> roles;

    /**
     * 头像
     */
    @Schema(description = "头像", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer gender;

    /**
     * 手机号
     */
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;

    /**
     * 昵称
     */
    @Schema(description = "昵称", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    /**
     * 状态
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
