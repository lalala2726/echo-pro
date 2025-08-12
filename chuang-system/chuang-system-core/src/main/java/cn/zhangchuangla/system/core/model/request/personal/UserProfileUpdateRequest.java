package cn.zhangchuangla.system.core.model.request.personal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * Created on 2025/3/1 18:06
 */
@Data
@Schema(name = "用户资料修改请求对象", description = "用户资料修改请求对象")
public class UserProfileUpdateRequest {

    /**
     * 昵称
     */
    @Schema(description = "昵称", type = "string", example = "张三")
    private String nickname;

    /**
     * 头像
     */
    @Schema(description = "头像", type = "string", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别", type = "integer", example = "1")
    private Integer gender;

    /**
     * 手机号
     */
    @Schema(description = "手机号", type = "string", example = "13800001111")
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", type = "string", example = "zhangsan@example.com")
    private String email;


}
