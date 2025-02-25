package cn.zhangchuangla.system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String username;

    /**
     * 密码
     */
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
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
    private String phone;

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
