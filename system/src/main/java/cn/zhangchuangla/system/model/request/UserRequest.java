package cn.zhangchuangla.system.model.request;

import cn.zhangchuangla.common.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UserRequest extends BasePageRequest {

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 昵称
     */
    @Schema(description = "昵称")
    private String nickName;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 手机号
     */
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
