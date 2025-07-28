package cn.zhangchuangla.system.model.request.user;

import cn.zhangchuangla.common.core.annotation.ValidRegex;
import cn.zhangchuangla.common.core.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;


/**
 * 注册参数类, 用于注册时使用的请求类
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:13
 */
@Data
@Schema(name = "添加用户请求类", description = "用于添加用户中使用")
public class SysUserAddRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名不能超过20个字符")
    @ValidRegex(regexp = RegularConstants.User.USERNAME, message = "用户名只能是英文数字和下划线", allowEmpty = false)
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", example = "Admin@123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度在 8-16 之间")
    @ValidRegex(regexp = RegularConstants.User.PASSWORD,
            message = "至少一个大写字母、一个小写字母、一个数字和一个特殊字符（只允许 !@#¥%&*()—+/ 这些特殊字符")
    private String password;

    /**
     * 头像
     */
    @Schema(description = "头像URL地址", example = "https://example.com/avatar.png")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别：1-男，2-女，3-未知", type = "integer", format = "int32", example = "1")
    private Integer gender;

    /**
     * 部门ID
     */
    @Schema(description = "所属部门唯一标识ID", type = "integer", format = "int64", example = "1001")
    @Min(value = 0, message = "部门ID不能小于0")
    private Long deptId;

    /**
     * 角色ID
     */
    @Schema(description = "分配的角色列表", type = "array", format = "int64", example = "[1,2,3]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> roleIds;

    /**
     * 手机号
     */
    @Schema(description = "手机号码", example = "13800001111")
    @ValidRegex(regexp = RegularConstants.User.PHONE, message = "手机号格式不正确")
    private String phone;

    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "管理员")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "电子邮箱地址", example = "admin@example.com")
    @ValidRegex(regexp = RegularConstants.User.EMAIL, message = "邮箱格式不正确")
    private String email;

    /**
     * 状态
     */
    @Schema(description = "账号状态：1-启用，0-停用", example = "1", requiredMode = Schema.RequiredMode.AUTO)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "其他备注信息", example = "系统管理员账号")
    private String remark;
}
