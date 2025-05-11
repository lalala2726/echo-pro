package cn.zhangchuangla.framework.security.token;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.config.property.SecurityProperties;
import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.security.model.OnlineLoginUser;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.UserAgentUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.zhangchuangla.common.enums.ResponseCode.ACCESS_TOKEN_INVALID;

/**
 * Redis Token 管理器
 * <p>
 * 用于生成、解析、校验、刷新 JWT Token
 *
 * @author Ray.Hao
 * @since 2024/11/15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisTokenManager implements TokenManager {

    private static final String CLAIM_KEY_SESSION_ID = "sessionId";
    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_TOKEN_TYPE = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    private final SecurityProperties securityProperties;
    private final RedisCache redisCache;
    private SecretKey jwtSecretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(securityProperties.getSecret());
        this.jwtSecretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成JWT Token（访问令牌和刷新令牌）。
     *
     * @param authentication Spring Security的认证信息对象。
     * @return 包含JWT访问令牌和JWT刷新令牌的AuthenticationToken对象。
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        String accessTokenId = IdUtil.fastSimpleUUID();
        String refreshTokenId = IdUtil.fastSimpleUUID();

        OnlineLoginUser onlineLoginUser = buildOnlineUser(userDetails, accessTokenId);
        setClientInfo(onlineLoginUser);

        String onlineUserKey = formatOnlineUserKeyBySessionId(accessTokenId);
        redisCache.setCacheObject(onlineUserKey, onlineLoginUser, securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户 {} 的在线信息已存储到Redis，会话ID: {}，Key: {}", username, accessTokenId, onlineUserKey);

        String jwtAccessToken = createJwt(accessTokenId, username, TOKEN_TYPE_ACCESS, securityProperties.getSession().getAccessTokenExpireTime() * 1000L);
        String jwtRefreshToken = createJwt(refreshTokenId, username, TOKEN_TYPE_REFRESH, securityProperties.getSession().getRefreshTokenExpireTime() * 1000L);

        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        redisCache.setCacheObject(refreshTokenMappingKey, accessTokenId, securityProperties.getSession().getRefreshTokenExpireTime());
        log.debug("JWT刷新令牌与访问会话ID的映射已存储到Redis，RefreshID: {}, AccessID: {}, Key: {}", refreshTokenId, accessTokenId, refreshTokenMappingKey);

        handleSingleDeviceLogin(userId, accessTokenId);

        return AuthenticationToken.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .expires(securityProperties.getSession().getAccessTokenExpireTime())
                .build();
    }

    /**
     * 解析JWT Token获取认证信息。
     *
     * @param jwtToken JWT访问令牌。
     * @return 用户认证信息（Authentication对象），如果Token无效或解析失败则返回null。
     */
    @Override
    public Authentication parseToken(String jwtToken) {
        Claims claims = getClaimsFromToken(jwtToken);
        if (claims == null || !TOKEN_TYPE_ACCESS.equals(claims.get(CLAIM_KEY_TOKEN_TYPE, String.class))) {
            log.warn("提供的Token不是有效的JWT访问令牌或解析Claims失败: {}", jwtToken);
            return null;
        }

        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StrUtil.isBlank(sessionId)) {
            log.warn("JWT中未找到sessionId: {}", jwtToken);
            return null;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        OnlineLoginUser onlineUser = redisCache.getCacheObject(onlineUserKey);

        if (onlineUser == null) {
            log.debug("根据JWT中的sessionId {} 未在Redis中找到在线用户信息，Key: {}", sessionId, onlineUserKey);
            return null;
        }

        redisCache.expire(onlineUserKey, securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户 {} 的会话已续期，会话ID: {}，Key: {}", onlineUser.getUsername(), sessionId, onlineUserKey);

        Set<SimpleGrantedAuthority> authorities = onlineUser.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        SysUserDetails userDetails = buildUserDetails(onlineUser, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /**
     * 校验JWT访问令牌是否有效。
     * 验证JWT签名、是否过期，并检查Redis中是否存在对应的会话信息。
     *
     * @param jwtAccessToken JWT访问令牌。
     * @return 如果有效返回true，否则返回false。
     */
    @Override
    public boolean validateAccessToken(String jwtAccessToken) {
        Claims claims = getClaimsFromToken(jwtAccessToken);
        if (claims == null || !TOKEN_TYPE_ACCESS.equals(claims.get(CLAIM_KEY_TOKEN_TYPE, String.class))) {
            log.debug("验证访问令牌：提供的Token不是有效的JWT访问令牌或解析Claims失败: {}", jwtAccessToken);
            return false;
        }

        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StrUtil.isBlank(sessionId)) {
            log.debug("验证访问令牌：JWT中未找到sessionId: {}", jwtAccessToken);
            return false;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        boolean isValid = redisCache.hasKey(onlineUserKey);
        if (isValid) {
            log.debug("验证访问令牌通过，会话ID: {}，Key: {}", sessionId, onlineUserKey);
        }
        return isValid;
    }

    /**
     * 校验JWT刷新令牌是否有效。
     * 验证JWT签名、是否过期，并检查Redis中是否存在对应的映射关系。
     *
     * @param jwtRefreshToken JWT刷新令牌。
     * @return 如果有效返回true，否则返回false。
     */
    @Override
    public boolean validateRefreshToken(String jwtRefreshToken) {
        Claims claims = getClaimsFromToken(jwtRefreshToken);
        if (claims == null || !TOKEN_TYPE_REFRESH.equals(claims.get(CLAIM_KEY_TOKEN_TYPE, String.class))) {
            log.debug("验证刷新令牌：提供的Token不是有效的JWT刷新令牌或解析Claims失败: {}", jwtRefreshToken);
            return false;
        }

        String refreshTokenId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StrUtil.isBlank(refreshTokenId)) {
            log.debug("验证刷新令牌：JWT中未找到sessionId (refreshTokenId): {}", jwtRefreshToken);
            return false;
        }
        return redisCache.hasKey(formatRefreshTokenMappingKey(refreshTokenId));
    }

    /**
     * 使用JWT刷新令牌刷新访问令牌。
     *
     * @param jwtRefreshToken JWT刷新令牌。
     * @return 新的AuthenticationToken，包含新的JWT访问令牌和原始JWT刷新令牌。
     * @throws ServiceException 如果刷新令牌无效或关联的用户会话不存在。
     */
    @Override
    public AuthenticationToken refreshToken(String jwtRefreshToken) {
        Claims refreshClaims = getClaimsFromToken(jwtRefreshToken);
        if (refreshClaims == null || !TOKEN_TYPE_REFRESH.equals(refreshClaims.get(CLAIM_KEY_TOKEN_TYPE, String.class))) {
            throw new ServiceException(ResponseCode.REFRESH_TOKEN_INVALID, "提供的不是有效的JWT刷新令牌");
        }

        String refreshTokenId = refreshClaims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StrUtil.isBlank(refreshTokenId)) {
            throw new ServiceException(ResponseCode.REFRESH_TOKEN_INVALID, "JWT刷新令牌中缺少ID");
        }

        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        String accessTokenId = redisCache.getCacheObject(refreshTokenMappingKey);
        if (StrUtil.isBlank(accessTokenId)) {
            log.warn("无效的刷新令牌ID {}，在Redis中未找到对应的访问会话ID映射, Key: {}", refreshTokenId, refreshTokenMappingKey);
            throw new ServiceException(ResponseCode.REFRESH_TOKEN_INVALID, "刷新令牌已过期或无效");
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(accessTokenId);
        OnlineLoginUser onlineUser = redisCache.getCacheObject(onlineUserKey);
        if (onlineUser == null) {
            log.warn("刷新令牌 {} 关联的原始用户会话 {} 不存在, Key: {}", refreshTokenId, accessTokenId, onlineUserKey);
            redisCache.deleteObject(refreshTokenMappingKey);
            throw new ServiceException(ResponseCode.REFRESH_TOKEN_INVALID, "用户会话已失效，请重新登录");
        }

        String newAccessTokenId = IdUtil.fastSimpleUUID();
        String username = onlineUser.getUsername();

        onlineUser.setSessionId(newAccessTokenId);
        setClientInfo(onlineUser);
        String newOnlineUserKey = formatOnlineUserKeyBySessionId(newAccessTokenId);
        redisCache.setCacheObject(newOnlineUserKey, onlineUser, securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户 {} 刷新令牌成功，新的访问会话ID: {}, Key: {}", username, newAccessTokenId, newOnlineUserKey);

        if (!newAccessTokenId.equals(accessTokenId)) {
            redisCache.deleteObject(onlineUserKey);
            log.debug("旧的在线用户会话已删除，旧会话ID: {}, Key: {}", accessTokenId, onlineUserKey);
        }

        redisCache.setCacheObject(StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId()), newAccessTokenId, securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户ID {} 的当前访问会话ID已更新为 {}，Key: {}", onlineUser.getUserId(), newAccessTokenId, StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId()));

        redisCache.expire(refreshTokenMappingKey, securityProperties.getSession().getRefreshTokenExpireTime());

        long accessTokenValidityMillis = securityProperties.getSession().getAccessTokenExpireTime() * 1000L;
        String newJwtAccessToken = createJwt(newAccessTokenId, username, TOKEN_TYPE_ACCESS, accessTokenValidityMillis);

        return AuthenticationToken.builder()
                .accessToken(newJwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .expires(securityProperties.getSession().getAccessTokenExpireTime())
                .build();
    }

    /**
     * 使Token失效（登出操作）。
     * 会删除与JWT访问令牌关联的Redis中的在线用户信息，以及相关的刷新令牌信息。
     *
     * @param jwtAccessToken JWT访问令牌。
     */
    @Override
    public void invalidateToken(String jwtAccessToken) {
        Claims claims = getClaimsFromToken(jwtAccessToken);
        if (claims == null || !TOKEN_TYPE_ACCESS.equals(claims.get(CLAIM_KEY_TOKEN_TYPE, String.class))) {
            log.warn("尝试使无效的JWT访问令牌失效: {}", jwtAccessToken);
            return;
        }

        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StrUtil.isBlank(sessionId)) {
            log.warn("尝试使缺少sessionId的JWT访问令牌失效: {}", jwtAccessToken);
            return;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        OnlineLoginUser onlineUser = redisCache.getCacheObject(onlineUserKey);
        redisCache.deleteObject(onlineUserKey);
        log.debug("在线用户信息已从Redis删除，会话ID: {}，Key: {}", sessionId, onlineUserKey);

        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();
            String userCurrentAccessIdKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
            redisCache.deleteObject(userCurrentAccessIdKey);
            log.debug("用户ID {} 的当前访问会话ID映射已删除，Key: {}", userId, userCurrentAccessIdKey);
        }
    }

    /**
     * 创建JWT。
     *
     * @param id               会话ID (对于访问令牌是accessTokenId，对于刷新令牌是refreshTokenId)
     * @param username         用户名
     * @param tokenType        令牌类型 (access/refresh)
     * @param validityInMillis 有效期（毫秒）
     * @return JWT字符串
     */
    private String createJwt(String id, String username, String tokenType, long validityInMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_SESSION_ID, id);
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_TOKEN_TYPE, tokenType);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(jwtSecretKey)
                .compact();
    }

    /**
     * 从JWT中解析Claims。
     *
     * @param token JWT字符串
     * @return Claims对象，包含JWT的声明信息
     */
    @Override
    public Claims getClaimsFromToken(String token) {
        try {
            Jws<Claims> jwsClaims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return jwsClaims.getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT已过期: {}, message: {}", token, e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID);
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT格式: {}, message: {}", token, e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            log.warn("JWT结构错误: {}, message: {}", token, e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID);
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败: {}, message: {}", token, e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims字符串为空: {}, message: {}", token, e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID);
        }
    }

    /**
     * 构建在线用户信息的Redis Key
     *
     * @param sessionId 会话ID
     * @return Redis Key
     */
    private String formatOnlineUserKeyBySessionId(String sessionId) {
        return StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, sessionId);
    }

    /**
     * 构建刷新令牌ID与访问令牌ID的映射Key
     *
     * @param refreshTokenId 刷新令牌ID
     * @return 映射Key
     */
    private String formatRefreshTokenMappingKey(String refreshTokenId) {
        return StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_MAPPING, refreshTokenId);
    }

    /**
     * 构建在线用户信息对象
     *
     * @param user          用户详情对象
     * @param accessTokenId 会话ID
     * @return 在线用户信息对象
     */
    private OnlineLoginUser buildOnlineUser(SysUserDetails user, String accessTokenId) {
        HttpServletRequest httpServletRequest = SecurityUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(httpServletRequest);
        String region = IPUtils.getRegion(ipAddr);
        return OnlineLoginUser.builder()
                .username(user.getUsername())
                .sessionId(accessTokenId)
                .IP(ipAddr)
                .region(region)
                .deptId(user.getDeptId())
                .userId(user.getUserId())
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * 设置在线用户的客户端信息
     *
     * @param onlineUser 在线用户信息
     */
    private void setClientInfo(OnlineLoginUser onlineUser) {
        HttpServletRequest httpServletRequest = SecurityUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        String osName = UserAgentUtils.getOsName(userAgent);
        String browserName = UserAgentUtils.getBrowserName(userAgent);
        String deviceManufacturer = UserAgentUtils.getDeviceManufacturer(userAgent);

        onlineUser.setIP(ipAddr);
        onlineUser.setRegion(IPUtils.getRegion(ipAddr));
        onlineUser.setOs(osName);
        onlineUser.setBrowser(browserName);
        onlineUser.setDevice(deviceManufacturer);
        onlineUser.setLoginTime(System.currentTimeMillis());
        onlineUser.setUserAgent(userAgent);
    }

    /**
     * 构建用户详情对象
     *
     * @param onlineUser  在线用户信息
     * @param authorities 权限集合
     * @return SysUserDetails 用户详情对象
     */
    private SysUserDetails buildUserDetails(OnlineLoginUser onlineUser, Set<SimpleGrantedAuthority> authorities) {
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(onlineUser.getUserId());
        userDetails.setUsername(onlineUser.getUsername());
        userDetails.setDeptId(onlineUser.getDeptId());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    private void handleSingleDeviceLogin(Long userId, String newAccessTokenId) {
        if (!securityProperties.getSession().getSingleLogin()) {
            return;
        }

        String userPreviousAccessIdKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
        String previousAccessTokenId = redisCache.getCacheObject(userPreviousAccessIdKey);

        if (StrUtil.isNotBlank(previousAccessTokenId) && !previousAccessTokenId.equals(newAccessTokenId)) {
            String oldOnlineUserKey = formatOnlineUserKeyBySessionId(previousAccessTokenId);
            redisCache.deleteObject(oldOnlineUserKey);
            log.info("单设备登录：用户 {} 的旧会话 {} 已被新会话 {} 强制下线。旧Key: {}", userId, previousAccessTokenId, newAccessTokenId, oldOnlineUserKey);
        }

        redisCache.setCacheObject(userPreviousAccessIdKey, newAccessTokenId, securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户ID {} 的当前访问会话ID已映射为 {}，Key: {}", userId, newAccessTokenId, userPreviousAccessIdKey);
    }
}
