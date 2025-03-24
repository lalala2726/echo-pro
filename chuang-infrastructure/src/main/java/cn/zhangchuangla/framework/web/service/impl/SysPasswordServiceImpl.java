package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.config.PasswordConfig;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AccountException;
import cn.zhangchuangla.framework.web.service.SysPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/25 15:42
 */
@Slf4j
@Service
public class SysPasswordServiceImpl implements SysPasswordService {

    private final RedisCache redisCache;
    private final PasswordConfig passwordConfig;

    public SysPasswordServiceImpl(RedisCache redisCache, PasswordConfig passwordConfig) {
        this.redisCache = redisCache;
        this.passwordConfig = passwordConfig;
    }


    /**
     * 密码错误尝试次数校验,如果超过设置的错误次数则会禁止登录
     *
     * @param username 用户名
     */
    @Override
    public void PasswordErrorCount(String username) {
        String key = RedisKeyConstant.PASSWORD_ERROR_COUNT + username;
        long maxRetryCount = passwordConfig.getMaxRetryCount();
        Integer retryCount = redisCache.getCacheObject(key);

        if (retryCount == null) {
            retryCount = 0;
        }
        Long keyExpire = redisCache.getKeyExpire(key);
        if (retryCount >= maxRetryCount) {
            throw new AccountException(ResponseCode.ACCOUNT_LOCKED, "账号已被锁定,请在" + keyExpire + "秒后重新尝试");
        }

        redisCache.setCacheObject(key, ++retryCount, passwordConfig.getLockTime(), TimeUnit.MINUTES);
    }

}
