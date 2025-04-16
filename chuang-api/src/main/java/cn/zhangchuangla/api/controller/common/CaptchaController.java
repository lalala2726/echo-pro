package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.Base64;
import cn.zhangchuangla.common.utils.UUIDUtils;
import com.google.code.kaptcha.Producer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/2 14:39
 */
@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码相关")
public class CaptchaController extends BaseController {


    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取验证码
     *
     * @return 返回验证码对应的UUID
     */
    @GetMapping
    @Operation(summary = "获取验证码")
    public AjaxResult getCaptcha() {
        // 保存验证码信息
        String uuid = UUIDUtils.simpleUUID();

        AjaxResult ajax = AjaxResult.success();
        String capStr, code;
        BufferedImage image;

        // 生成验证码
        String capText = captchaProducerMath.createText();
        capStr = capText.substring(0, capText.lastIndexOf("@"));
        code = capText.substring(capText.lastIndexOf("@") + 1);
        image = captchaProducerMath.createImage(capStr);

        // 将验证码存储到redis中，有效期2分钟
        String verifyKey = RedisConstants.CAPTCHA_CODE + uuid;
        redisTemplate.opsForValue().set(verifyKey, code, 2, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }
}
