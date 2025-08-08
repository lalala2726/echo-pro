package cn.zhangchuangla.system.core.model.vo.captcha;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图形验证码返回对象
 *
 * @author Chuang
 */
@Data
@Schema(name = "图形验证码响应", description = "返回验证码UUID和Base64图片")
public class CaptchaImageVo {

    @Schema(description = "验证码唯一标识")
    private String uuid;

    @Schema(description = "验证码图片Base64，含data:image/png;base64, 前缀")
    private String imgBase64;
}





