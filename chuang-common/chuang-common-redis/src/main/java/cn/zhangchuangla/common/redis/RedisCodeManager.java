package cn.zhangchuangla.common.redis;

import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.core.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 负责验证码在 Redis 中的存取、验证和删除。
 *
 * @author Chuang
 * <p>
 * created on 2025/8/4 00:06
 */
@Component
@RequiredArgsConstructor
public class RedisCodeManager {

    private final static String CAPTCHA_CODE_KEY = "captcha:code:";
    private final RedisCache redisCache;

    /**
     * 从Redis中读取验证码
     *
     * @param uuid 验证码唯一标识符
     * @return 验证码
     */
    public String getCode(String uuid) {
        Assert.notEmpty(uuid, "uuid不能为空");
        return redisCache.getCacheObject(CAPTCHA_CODE_KEY + uuid);
    }

    /**
     * 存储验证码,默认有效期5分钟
     *
     * @param uuid 验证码唯一标识符
     * @param code 验证码
     */
    public void setCode(String uuid, String code) {
        final long timeout = 5;
        Assert.notEmpty(uuid, "uuid不能为空");
        Assert.notEmpty(code, "验证码不能为空");
        redisCache.setCacheObject(CAPTCHA_CODE_KEY + uuid, code, timeout, java.util.concurrent.TimeUnit.MINUTES);
    }


    /**
     * 存储验证码
     *
     * @param uuid    验证码唯一标识符
     * @param code    验证码
     * @param timeout 有效期
     */
    public void setCode(String uuid, String code, long timeout) {
        Assert.notEmpty(uuid, "uuid不能为空");
        Assert.notEmpty(code, "验证码不能为空");
        Assert.isTrue(timeout > 0, "有效期不能小于0");
        redisCache.setCacheObject(CAPTCHA_CODE_KEY + uuid, code, timeout, TimeUnit.MINUTES);
    }

    /**
     * 存储验证码
     *
     * @param uuid     验证码唯一标识符
     * @param code     验证码
     * @param timeout  有效期
     * @param timeUnit 时间单位
     */
    public void setCode(String uuid, String code, long timeout, TimeUnit timeUnit) {
        Assert.notEmpty(uuid, "uuid不能为空");
        Assert.notEmpty(code, "验证码不能为空");
        Assert.isTrue(timeout > 0, "有效期不能小于0");
        redisCache.setCacheObject(CAPTCHA_CODE_KEY + uuid, code, timeout, timeUnit);
    }

}
