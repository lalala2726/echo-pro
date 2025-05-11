package cn.zhangchuangla.system.model.request.user;

import cn.zhangchuangla.common.annoation.ValidRegex;
import cn.zhangchuangla.common.constant.RegularConstants;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 修改用户信息参数, 用于修改用户时使用
 */
@Data
@Schema(name = "修改用户请求类", description = "用于修改用户时")
public class UserUpdateRequest {

    /**
     * ID
     */
    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色信息
     */
    @Schema(description = "角色ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<Long> roleIds;

    /**
     * 头像
     */
    @Schema(description = "头像", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Range(min = 0, max = 2, message = "性别不正确")
    private Integer gender;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long deptId;


    /**
     * 手机号
     */
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ValidRegex(regexp = RegularConstants.User.phone, message = "手机号码格式不正确")
    private String phone;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @ValidRegex(regexp = RegularConstants.User.password, message = "密码格式不正确")
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
    @ValidRegex(regexp = RegularConstants.User.email, message = "邮箱格式不正确")
    private String email;

    /**
     * 状态
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Range(min = 0, max = 1, message = "用户状态不正确")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String remark;
}
