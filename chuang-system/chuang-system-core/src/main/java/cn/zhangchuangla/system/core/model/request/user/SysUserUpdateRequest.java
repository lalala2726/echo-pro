package cn.zhangchuangla.system.core.model.request.user;

import cn.zhangchuangla.common.core.annotation.ValidRegex;
import cn.zhangchuangla.common.core.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * 修改用户请求类
 * <p>
 * 修改用户信息参数, 用于修改用户时使用
 *
 * @author Chuang
 */
@Data
@Schema(name = "修改用户请求类", description = "用于修改用户时")
public class SysUserUpdateRequest {

    /**
     * ID
     */
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 角色信息
     */
    @Schema(description = "角色ID列表", example = "[1,2,3]")
    private List<Long> roleIds;

    /**
     * 头像
     */
    @Schema(description = "头像URL地址", example = "http://example.com/avatar.jpg")
    private String avatar;


    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID", type = "integer", format = "int64", example = "10")
    private Long postId;

    /**
     * 性别
     */
    @Schema(description = "性别 0-未知 1-男 2-女", example = "1")
    @Range(min = 0, max = 2, message = "性别不正确")
    private Integer gender;

    /**
     * 部门ID
     */
    @Schema(description = "所属部门ID", example = "10")
    private Long deptId;


    /**
     * 手机号
     */
    @Schema(description = "手机号码", example = "13800001111")
    @ValidRegex(regexp = RegularConstants.User.PHONE, message = "手机号码格式不正确")
    private String phone;

    /**
     * 密码
     */
    @Schema(description = "登录密码", example = "Abc123456!")
    private String password;


    /**
     * 昵称
     */
    @Schema(description = "用户昵称", example = "Tom")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "电子邮箱地址", example = "tom@example.com")
    @ValidRegex(regexp = RegularConstants.User.EMAIL, message = "邮箱格式不正确")
    private String email;

    /**
     * 状态
     */
    @Schema(description = "账号状态 0-禁用 1-启用", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "用户备注信息", example = "这是一个测试用户")
    private String remark;
}
