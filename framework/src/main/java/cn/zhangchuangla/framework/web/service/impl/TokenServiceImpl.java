package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.config.TokenConfig;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AccountException;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.framework.web.service.TokenService;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Token服务实现类
 * 该类负责生成、解析和验证JWT token。
 *
 * @author Chuang
 */
@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

    protected static final long MILLIS_SECOND = 1000;
    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;
    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;
    private final TokenConfig tokenConfig;
    private final SecretKey key;
    private final RedisCache redisCache;

    public TokenServiceImpl(TokenConfig tokenConfig, RedisCache redisCache) {
        this.tokenConfig = tokenConfig;
        // 从配置文件中读取密钥字符串并转换为SecretKey
        this.key = Keys.hmacShaKeyFor(tokenConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.redisCache = redisCache;
    }

    @Override
    public String createToken(LoginUser loginUser, HttpServletRequest request) {
        Map<String, Object> claims = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        loginUser.setSessionId(uuid);
        claims.put(SystemConstant.LOGIN_USER_KEY, uuid);

        setUserAgent(loginUser, request);
        refreshToken(loginUser);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    public void setUserAgent(LoginUser loginUser, HttpServletRequest request) {
        String ip = request.getRemoteAddr();//获取浏览器
        String header = request.getHeader("User-Agent");
        String os = request.getHeader("sec-ch-ua-platform");
        loginUser.setIpAddress(ip);
        loginUser.setOs(os);
        loginUser.setBrowser(header);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        Long expire = tokenConfig.getExpire();
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expire * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getSessionId());
        // 将登录用户信息缓存到 Redis 中
        redisCache.setCacheObject(userKey, loginUser, expire, TimeUnit.MINUTES);
    }

    private String getTokenKey(String uuid) {
        return RedisKeyConstant.LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 从Token中获取用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    @Override
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 验证Token
     *
     * @param loginUser 登录用户
     */
    @Override
    public void validateToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }

    }

    /**
     * 刷新Token
     *
     * @param token 原Token字符串
     * @return 新的Token字符串
     */
    @Override
    public String refreshToken(String token) {
        Claims claims = parseToken(token);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getExpire() * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token Token字符串
     * @return Claims对象
     */
    @Override
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取登录用户信息
     *
     * @param request 请求
     * @return 登录用户信息
     */
    @Override
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (!StringUtils.isBlank(token)) {
            try {
                Claims claims = parseToken(token);
                String sessionId = (String) claims.get(SystemConstant.LOGIN_USER_KEY);
                return getLoginUserByToken(sessionId);
            } catch (Exception e) {
                    log.warn("获取用户信息失败: {}", e.getMessage());
            }
        }
        return null;
    }

    private LoginUser getLoginUserByToken(String sessionId) {
        // 从 Redis 中获取缓存对象，类型可能是 JSONObject
        Object cacheObject = redisCache.getCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + sessionId);
        if (cacheObject == null) {
            throw new AccountException(ResponseCode.USER_NOT_LOGIN);
        }
        // 如果返回的是 JSONObject，可以使用 JSON.toJavaObject 进行转换
        return JSON.parseObject(JSON.toJSONString(cacheObject), LoginUser.class);
    }

    /**
     * 获取Token
     *
     * @param request 请求
     * @return token
     */
    @Override
    public String getToken(HttpServletRequest request) {
        String header = tokenConfig.getHeader();
        if (StringUtils.isBlank(header)) {
            return null;
        }
        return request.getHeader(header);
    }
}



