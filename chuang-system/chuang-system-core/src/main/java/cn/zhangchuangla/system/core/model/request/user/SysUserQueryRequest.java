package cn.zhangchuangla.system.core.model.request.user;

import cn.zhangchuangla.common.core.annotation.ValidRegex;
import cn.zhangchuangla.common.core.constant.RegularConstants;
import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


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
    @ValidRegex(regexp = RegularConstants.User.EMAIL, message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @ValidRegex(regexp = RegularConstants.User.PHONE, message = "手机号格式不正确")
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


    /**
     * 开始时间
     */
    @Schema(description = "开始时间", example = "2023-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间", example = "2023-01-01", type = "string", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endTime;


}
