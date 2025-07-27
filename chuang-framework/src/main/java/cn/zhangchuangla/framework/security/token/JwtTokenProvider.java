package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static cn.zhangchuangla.common.core.enums.ResultCode.ACCESS_TOKEN_INVALID;

/**
 * JWT创建/验证工具
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 20:25
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecurityProperties securityProperties;
    private SecretKey jwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(securityProperties.getSecret());
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * 创建JWT。不再包含tokenType。
     *
     * @param tokenId       令牌的唯一ID (对于访问令牌是accessTokenId，对于刷新令牌是refreshTokenId)
     * @param username 用户名
     * @return JWT字符串
     */
    public String createJwt(String tokenId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstants.CLAIM_KEY_SESSION_ID, tokenId);
        claims.put(SecurityConstants.CLAIM_KEY_USERNAME, username);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(jwtSecretKey)
                .compact();
    }


    /**
     * 从JWT中解析Claims。
     * 遇到已知错误（过期、签名错误等）时抛出自定义异常。
     *
     * @param token JWT字符串
     * @return Claims对象，包含JWT的声明信息
     * @throws AuthorizationException 如果JWT无效 (例如格式错误、签名错误、过期)
     */
    public Claims getClaimsFromToken(String token) {
        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return jwsClaims.getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT已过期, message: {}", e.getMessage());
            // 或者更具体的 REFRESH_TOKEN_EXPIRED
            throw new AuthorizationException(ACCESS_TOKEN_INVALID, "令牌已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT格式, message: {}", e.getMessage());
            throw new AuthorizationException(ACCESS_TOKEN_INVALID, "令牌格式不支持");
        } catch (MalformedJwtException e) {
            log.warn("JWT结构错误, message: {}", e.getMessage());
            throw new AuthorizationException(ACCESS_TOKEN_INVALID, "令牌结构错误");
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败, message: {}", e.getMessage());
            throw new AuthorizationException(ACCESS_TOKEN_INVALID, "令牌签名无效");
        } catch (IllegalArgumentException e) { // 通常是token为空或null
            log.warn("JWT claims字符串为空或无效参数, message: {}", e.getMessage());
            throw new AuthorizationException(ACCESS_TOKEN_INVALID, "令牌参数无效");
        }
    }
}
