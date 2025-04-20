package cn.zhangchuangla.system.model.request.user;

import cn.zhangchuangla.common.base.BasePageRequest;
import cn.zhangchuangla.common.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 用户查询参数,用户查询用户列表时筛选条件
 *
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "用户查询参数", description = "用户查询参数")
public class UserListRequest extends BasePageRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    @Pattern(regexp = RegularConstants.User.username)
    private String username;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID")
    @Min(value = 1, message = "部门ID不能小于1")
    private Long deptId;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @Pattern(regexp = RegularConstants.User.email, message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = RegularConstants.User.phone, message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private Integer gender;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createBy;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updateBy;

}
