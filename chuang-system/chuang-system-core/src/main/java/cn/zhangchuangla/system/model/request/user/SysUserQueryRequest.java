package cn.zhangchuangla.system.model.request.user;

import cn.zhangchuangla.common.annoation.ValidRegex;
import cn.zhangchuangla.common.base.BasePageRequest;
import cn.zhangchuangla.common.constant.RegularConstants;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
public class SysUserQueryRequest extends BasePageRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    @Min(value = 1, message = "部门ID不能小于1")
    private Long deptId;

    /**
     * 昵称
     */
    @Schema(description = "昵称", example = "张三")
    private String nickname;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @ValidRegex(regexp = RegularConstants.User.email, message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @ValidRegex(regexp = RegularConstants.User.phone, message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800001111")
    private String phone;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "开发人员")
    private String remark;

    /**
     * 创建人
     */
    @Schema(description = "创建人", example = "admin")
    private String createBy;

    /**
     * 修改人
     */
    @Schema(description = "修改人", example = "admin")
    private String updateBy;

}
