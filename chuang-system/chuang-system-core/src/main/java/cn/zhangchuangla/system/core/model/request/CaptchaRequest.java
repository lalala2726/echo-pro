package cn.zhangchuangla.system.core.model.request;

import cn.zhangchuangla.common.core.annotation.ValidRegex;
import cn.zhangchuangla.common.core.constant.RegularConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/4 02:58
 */
@Data
@Schema(name = "验证码请求参数", description = "用于发送验证码和验证")
public class CaptchaRequest {

    /**
     * 手机号码
     */
    @ValidRegex(message = "手机号格式错误", regexp = RegularConstants.User.PHONE, allowEmpty = true)
    @Schema(description = "手机号码", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "13800001111")
    private String phone;

    /**
     * 邮箱地址
     */
    @ValidRegex(message = "邮箱格式错误", regexp = RegularConstants.User.EMAIL, allowEmpty = true)
    @Schema(description = "邮箱地址", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "admin@example.com")
    private String email;

    /**
     * 验证码
     */
    @Schema(description = "验证码", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "123456")
    private String code;

    /**
     * 用户唯一标识
     */
    @Schema(description = "用户唯一标识", type = "string", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1001")
    private String uid;
}
