package cn.zhangchuangla.api.controller.common;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.zhangchuangla.common.core.constant.Constants;
import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.framework.annotation.AccessLimit;
import cn.zhangchuangla.framework.config.kaptcha.KaptchaTextCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 验证码相关接口
 * 提供获取验证码的功能
 *
 * @author Chuang
 * created on 2025/4/2 14:39
 */
@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码接口", description = "提供获取验证码的功能")
@RequiredArgsConstructor
public class CaptchaController extends BaseController {

    private final RedisCache redisCache;
    private final SecureRandom random = new SecureRandom();
    @Resource(name = "captchaTextCreator")
    private KaptchaTextCreator captchaTextCreator;

    /**
     * 获取验证码
     *
     * @return 返回验证码对应的UUID和Base64图片
     */
    @GetMapping
    @Operation(summary = "获取验证码")
    @AccessLimit(maxCount = 20)
    public AjaxResult<HashMap<String, String>> getCaptcha() {
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        HashMap<String, String> ajax = new HashMap<>(2);

        // 生成数学公式验证码
        String mathText = captchaTextCreator.getText();
        String formula = mathText.substring(0, mathText.lastIndexOf("@"));
        String code = mathText.substring(mathText.lastIndexOf("@") + 1);

        // 生成简单的验证码图片
        BufferedImage image = createCaptchaImage(formula);

        // 将验证码存储到redis中，有效期2分钟
        String verifyKey = RedisConstants.CAPTCHA_CODE + uuid;
        redisCache.setCacheObject(verifyKey, code, 2, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("captchaKey", uuid);
        ajax.put("captchaBase64", Constants.BASE64_CODE + Base64.encode(os.toByteArray()));
        return success(ajax);
    }

    /**
     * 创建简单的验证码图片
     *
     * @param text 验证码文本
     * @return 验证码图片
     */
    private BufferedImage createCaptchaImage(String text) {
        int width = 160;
        int height = 60;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 设置背景色
        g.setColor(new Color(random.nextInt(80) + 170, random.nextInt(80) + 170, random.nextInt(80) + 170));
        g.fillRect(0, 0, width, height);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));

        // 绘制验证码文本
        g.drawString(text, 30, 40);

        // 添加干扰线
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();
        return image;
    }
}
