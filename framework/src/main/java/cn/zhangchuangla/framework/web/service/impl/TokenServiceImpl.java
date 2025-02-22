package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.config.TokenConfig;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.AuthenticationException;
import cn.zhangchuangla.common.exception.ProFileException;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.web.service.TokenService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Token服务实现类
 * 该类负责生成、解析和验证JWT token。
 * 
 * @author Chuang
 */
@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

    private final TokenConfig tokenConfig;
    private final SecretKey key;
    private final RedisCache redisCache;

    public TokenServiceImpl(TokenConfig tokenConfig, RedisCache redisCache) {
        this.tokenConfig = tokenConfig;
        // 从配置文件中读取密钥字符串并转换为SecretKey
        this.key = Keys.hmacShaKeyFor(tokenConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.redisCache = redisCache;
    }

    /**
     * 创建Token
     *
     * @param userId 用户ID
     * @return token字符串
     */
    @Override
    public String createToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SystemConstant.LOGIN_USER_KEY, userId);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenConfig.getExpire() * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
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
     * 验证Token是否有效
     *
     * @param token Token字符串
     * @return 是否有效
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("验证Token失败", e);
            return false;
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
                .signWith(key, SignatureAlgorithm.HS256)
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
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Claims claims = parseToken(token);
        String userId = (String) claims.get(SystemConstant.LOGIN_USER_KEY);
        log.info("从Token中获取用户ID：{}", userId);
        // 根据userId获取并返回LoginUser对象
        return getLoginUserById(userId);
    }

    private LoginUser getLoginUserById(String userId) {
        // 从 Redis 中获取缓存对象，类型可能是 JSONObject
        Object cacheObject = redisCache.getCacheObject(RedisKeyConstant.LOGIN_TOKEN_KEY + userId);
        if (cacheObject == null) {
            throw new AuthenticationException(ResponseCode.USER_NOT_LOGIN);
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
        return request.getHeader(header);
    }
}



