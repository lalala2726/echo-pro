package cn.zhangchuangla.system.model.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 * 注册参数类, 用于注册时使用的请求类
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 10:13
 */
@Data
@Schema(name = "添加用户请求类", description = "用于添加用户中使用")
public class AddUserRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 5, max = 20, message = "用户名不能超过20个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]{5,20}$", message = "用户名只能是英文数字和下划线")
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度在 8-16 之间")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#¥%&*()—+/])[A-Za-z0-9!@#¥%&*()—+/]{8,20}$\n",
            message = "至少一个大写字母、一个小写字母、一个数字和一个特殊字符（只允许 !@#¥%&*()—+/ 这些特殊字符")
    private String password;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private Integer gender;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "邮箱格式不正确")
    private String email;

    /**
     * 状态
     */
    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
