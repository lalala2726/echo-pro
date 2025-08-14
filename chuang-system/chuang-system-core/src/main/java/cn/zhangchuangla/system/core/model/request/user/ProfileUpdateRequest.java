package cn.zhangchuangla.system.core.model.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/3 00:43
 */
@Data
public class ProfileUpdateRequest {

    /**
     * 昵称
     */
    @Schema(description = "昵称", type = "string", example = "张三")
    private String nickName;

    /**
     * 头像
     */
    @Schema(description = "头像", type = "string", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 性别
     */
    @Schema(description = "性别", type = "string", example = "1")
    private String gender;

    /**
     * 地区
     */
    @Schema(description = "地区", type = "string", example = "上海")
    private String region;

    /**
     * 个性签名
     */
    @Schema(description = "个性签名", type = "string", example = "Hello World")
    private String signature;

}
