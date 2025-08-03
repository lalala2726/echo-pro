package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.CaptchaUtils;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.core.model.request.CaptchaRequest;
import cn.zhangchuangla.system.core.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/4 03:04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

    private final static String CAPTCHA_CODE_KEY = "captcha:code:";
    private final RedisCache redisCache;
    private final long timeout = 5;

    @Override
    public void sendEmail(CaptchaRequest request) {
        String code = CaptchaUtils.randomNumeric();
        log.info("邮箱验证码:{}", code);
        redisCache.setCacheObject(CAPTCHA_CODE_KEY + request.getEmail(), code, timeout, TimeUnit.MINUTES);
    }

    @Override
    public void sendPhone(CaptchaRequest request) {
        String code = CaptchaUtils.randomNumeric();
        log.info("手机验证码:{}", code);
        redisCache.setCacheObject(CAPTCHA_CODE_KEY + request.getPhone(), code, timeout, TimeUnit.MINUTES);
    }

    /**
     * 普通字符验证码
     *
     * @param request 请求参数
     * @return 验证结果
     */
    @Override
    public boolean verify(CaptchaRequest request) {
        return false;
    }

    /**
     * 邮箱验证码验证
     *
     * @param email 邮箱
     * @return 验证结果
     */
    @Override
    public boolean verifyEmail(String email, String code) {
        Assert.hasText(code, "验证码不能为空");
        Assert.hasText(email, "邮箱不能为空");
        String redisKey = CAPTCHA_CODE_KEY + email;
        String redisCode = redisCache.getCacheObject(redisKey);
        Assert.hasText(redisCode, "验证码已过期");
        if (redisCode.equals(code)) {
            redisCache.deleteObject(redisKey);
            return true;
        }
        return false;

    }

    /**
     * 手机验证码验证
     *
     * @param phone 手机
     * @return 验证结果
     */
    @Override
    public boolean verifyPhone(String phone, String code) {
        Assert.hasText(phone, "手机不能为空");
        Assert.hasText(code, "验证码不能为空");
        String redisKey = CAPTCHA_CODE_KEY + phone;
        String redisCode = redisCache.getCacheObject(redisKey);
        Assert.hasText(redisCode, "验证码已过期");
        if (redisCode.equals(code)) {
            redisCache.deleteObject(redisKey);
            return true;
        }
        return false;
    }


}
