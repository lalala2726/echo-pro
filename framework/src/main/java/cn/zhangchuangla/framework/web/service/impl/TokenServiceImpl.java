package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.config.TokenConfig;
import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Token服务
 *
 * @author Chuang
 */
@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

    private final TokenConfig tokenConfig;

    private final SecretKey key;

    public TokenServiceImpl(TokenConfig tokenConfig) {
        this.tokenConfig = tokenConfig;
        // 使用SignatureAlgorithm.HS256生成一个安全的密钥
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
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
     * @param request 令牌
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
        return null;
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
