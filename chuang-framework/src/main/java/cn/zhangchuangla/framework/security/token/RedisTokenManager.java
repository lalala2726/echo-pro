package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.core.security.model.OnlineLoginUser;
import cn.zhangchuangla.common.core.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.service.SysRoleService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zhangchuangla.common.core.enums.ResponseCode.ACCESS_TOKEN_INVALID;
import static cn.zhangchuangla.common.core.enums.ResponseCode.REFRESH_TOKEN_INVALID;


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

    private static final String CLAIM_KEY_SESSION_ID = "session";
    private static final String CLAIM_KEY_USERNAME = "username";

    private final SecurityProperties securityProperties;
    private final RedisCache redisCache;
    private final SysRoleService sysRoleService;
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

        String accessTokenId = UUID.randomUUID().toString();
        String refreshTokenId = UUID.randomUUID().toString();

        OnlineLoginUser onlineLoginUser = buildOnlineUser(userDetails, accessTokenId);
        setClientInfo(onlineLoginUser);

        String onlineUserKey = formatOnlineUserKeyBySessionId(accessTokenId);
        redisCache.setCacheObject(onlineUserKey, onlineLoginUser,
                securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户 {} 的在线信息已存储到Redis，访问会话ID: {}，Key: {}", username, accessTokenId, onlineUserKey);

        String jwtAccessToken = createJwt(accessTokenId, username,
                securityProperties.getSession().getAccessTokenExpireTime() * 1000L);
        String jwtRefreshToken = createJwt(refreshTokenId, username,
                securityProperties.getSession().getRefreshTokenExpireTime() * 1000L);

        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        // 刷新令牌的Redis value 存储的是它对应的 accessTokenId
        redisCache.setCacheObject(refreshTokenMappingKey, accessTokenId,
                securityProperties.getSession().getRefreshTokenExpireTime());
        log.debug("JWT刷新令牌与访问会话ID的映射已存储到Redis，RefreshID: {}, AccessID: {}, Key: {}", refreshTokenId, accessTokenId,
                refreshTokenMappingKey);

        handleSingleDeviceLogin(userId, accessTokenId);

        return AuthenticationToken.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .expires(securityProperties.getSession().getAccessTokenExpireTime())
                .build();
    }

    /**
     * 解析JWT访问令牌获取认证信息。
     * 此方法隐含期望一个访问令牌。
     *
     * @param jwtAccessToken JWT访问令牌。
     * @return 用户认证信息（Authentication对象），如果Token无效或解析失败则返回null。
     */
    @Override
    public Authentication parseToken(String jwtAccessToken) {
        // getClaimsFromToken 内部处理异常
        Claims claims = getClaimsFromToken(jwtAccessToken);
        // 如果getClaimsFromToken在无效时返回null而不是抛出异常
        if (claims == null) {
            log.warn("解析访问令牌失败或Claims为空: {}", jwtAccessToken);
            return null;
        }

        // tokenType 检查已被移除，因为此方法上下文就是处理访问令牌

        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(sessionId)) {
            log.warn("访问令牌JWT中未找到sessionId ({}): {}", CLAIM_KEY_SESSION_ID, jwtAccessToken);
            return null;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        OnlineLoginUser onlineUser = redisCache.getCacheObject(onlineUserKey);

        if (onlineUser == null) {
            log.debug("根据访问令牌JWT中的sessionId {} 未在Redis中找到在线用户信息，Key: {}", sessionId, onlineUserKey);
            return null;
        }

        redisCache.expire(onlineUserKey, securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户 {} 的会话已续期，会话ID: {}，Key: {}", onlineUser.getUsername(), sessionId, onlineUserKey);

        Set<SimpleGrantedAuthority> authorities = onlineUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + role))
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
        Claims claims;
        try {
            // 如果解析失败或过期，getClaimsFromToken会抛出异常
            claims = getClaimsFromToken(jwtAccessToken);
        } catch (ParamException | ServiceException e) { // 捕获自定义的token无效异常
            log.debug("验证访问令牌：JWT解析失败或无效: {}, 原因: {}", jwtAccessToken, e.getMessage());
            throw e;
        }
        // 以防万一 getClaimsFromToken 返回 null 而不是抛异常
        if (claims == null) {
            log.debug("验证访问令牌：解析后Claims为空: {}", jwtAccessToken);
            return false;
        }
        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(sessionId)) {
            log.debug("验证访问令牌：JWT中未找到sessionId ({}): {}", CLAIM_KEY_SESSION_ID, jwtAccessToken);
            return false;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        boolean isValid = redisCache.hasKey(onlineUserKey);
        if (isValid) {
            log.debug("验证访问令牌通过，会话ID: {}，Key: {}", sessionId, onlineUserKey);
        } else {
            log.debug("验证访问令牌失败：Redis中不存在会话ID {} 对应的用户，Key: {}", sessionId, onlineUserKey);
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
        Claims claims;
        try {
            claims = getClaimsFromToken(jwtRefreshToken);
        } catch (ParamException | ServiceException e) {
            log.debug("验证刷新令牌：JWT解析失败或无效: {}, 原因: {}", jwtRefreshToken, e.getMessage());
            return false;
        }
        if (claims == null) {
            log.debug("验证刷新令牌：解析后Claims为空: {}", jwtRefreshToken);
            return false;
        }
        // tokenType 检查已被移除

        // 刷新令牌JWT中的ID是refreshTokenId
        String refreshTokenId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(refreshTokenId)) {
            log.debug("验证刷新令牌：JWT中未找到sessionId ({} 作为 refreshTokenId): {}", CLAIM_KEY_SESSION_ID, jwtRefreshToken);
            return false;
        }
        boolean isValid = redisCache.hasKey(formatRefreshTokenMappingKey(refreshTokenId));
        if (isValid) {
            log.debug("验证刷新令牌通过，Refresh Token ID: {}", refreshTokenId);
        } else {
            log.debug("验证刷新令牌失败：Redis中不存在 Refresh Token ID {} 对应的映射, Key: {}", refreshTokenId,
                    formatRefreshTokenMappingKey(refreshTokenId));
        }
        return isValid;
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
        // 内部会处理过期等问题并抛异常
        Claims refreshClaims = getClaimsFromToken(jwtRefreshToken);
        // 以防万一
        if (refreshClaims == null) {
            throw new ServiceException(REFRESH_TOKEN_INVALID, "无法解析刷新令牌Claims");
        }
        // tokenType 检查已被移除

        String refreshTokenId = refreshClaims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(refreshTokenId)) {
            throw new ServiceException(REFRESH_TOKEN_INVALID, "JWT刷新令牌中缺少ID (" + CLAIM_KEY_SESSION_ID + ")");
        }

        // 验证刷新令牌本身是否存在于Redis (这一步其实 validateRefreshToken 已经做了，但双重检查无害)
        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        // 获取旧的accessTokenId
        String accessTokenId = redisCache.getCacheObject(refreshTokenMappingKey);
        if (StringUtils.isBlank(accessTokenId)) {
            log.warn("无效的刷新令牌ID {}，在Redis中未找到对应的访问会话ID映射, Key: {}", refreshTokenId, refreshTokenMappingKey);
            throw new ServiceException(REFRESH_TOKEN_INVALID, "刷新令牌已过期或无效");
        }

        // 用旧的accessTokenId找在线用户
        String onlineUserKey = formatOnlineUserKeyBySessionId(accessTokenId);
        OnlineLoginUser onlineUser = redisCache.getCacheObject(onlineUserKey);
        if (onlineUser == null) {
            log.warn("刷新令牌 {} 关联的原始用户会话 {} (旧accessTokenId) 不存在, Key: {}", refreshTokenId, accessTokenId,
                    onlineUserKey);
            // 清理无效的刷新令牌映射
            redisCache.deleteObject(refreshTokenMappingKey);
            throw new ServiceException(REFRESH_TOKEN_INVALID, "用户会话已失效，请重新登录");
        }

        // （可选）校验JWT中的用户名和Redis中的用户名是否一致
        String usernameFromRefreshToken = refreshClaims.get(CLAIM_KEY_USERNAME, String.class);
        if (!onlineUser.getUsername().equals(usernameFromRefreshToken)) {
            log.warn("刷新令牌中的用户名 {} 与Redis中的用户名 {} 不匹配。RefreshTokenId: {}, AccessTokenId: {}",
                    usernameFromRefreshToken, onlineUser.getUsername(), refreshTokenId, accessTokenId);
            // 根据安全策略，可以选择拒绝刷新
            // throw new ServiceException(REFRESH_TOKEN_INVALID, "令牌信息不一致");
        }

        // 生成新地访问令牌 ID 和 JWT
        String newAccessTokenId = UUID.randomUUID().toString().replaceAll("-", "");
        // 从 Redis 中的 onlineUser 获取用户名，更可靠
        String username = onlineUser.getUsername();

        // 更新 onlineUser 中的 sessionId 为新的 accessTokenId
        onlineUser.setSessionId(newAccessTokenId);
        // 更新登录时间和可能的IP等信息
        setClientInfo(onlineUser);
        String newOnlineUserKey = formatOnlineUserKeyBySessionId(newAccessTokenId);
        redisCache.setCacheObject(newOnlineUserKey, onlineUser,
                securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户 {} 刷新令牌成功，新的访问会话ID: {}, 旧访问会话ID: {}, Key: {}", username, newAccessTokenId, accessTokenId,
                newOnlineUserKey);

        // 如果启用了单点登录，需要更新 USER_ACCESS_TOKEN 映射
        if (securityProperties.getSession().getSingleLogin()) {
            redisCache.setCacheObject(RedisConstants.Auth.USER_ACCESS_TOKEN + onlineUser.getUserId(), newAccessTokenId,
                    securityProperties.getSession().getAccessTokenExpireTime());
            log.debug("用户ID {} 的当前访问会话ID已更新为 {} (刷新流程)", onlineUser.getUserId(), newAccessTokenId);
        }

        if (!newAccessTokenId.equals(accessTokenId)) {
            redisCache.deleteObject(onlineUserKey);
            log.debug("旧的在线用户会话已删除，旧会话ID: {}, Key: {}", accessTokenId, onlineUserKey);
        }

        // 更新刷新令牌映射关系中的 accessTokenId 为新的 accessTokenId
        // 并且续期刷新令牌的映射关系
        redisCache.setCacheObject(refreshTokenMappingKey, newAccessTokenId,
                securityProperties.getSession().getRefreshTokenExpireTime());
        log.debug("刷新令牌映射 {} 已更新，新的AccessID: {}, 并已续期", refreshTokenMappingKey, newAccessTokenId);

        long accessTokenValidityMillis = securityProperties.getSession().getAccessTokenExpireTime() * 1000L;
        String newJwtAccessToken = createJwt(newAccessTokenId, username, accessTokenValidityMillis);

        return AuthenticationToken.builder()
                .accessToken(newJwtAccessToken)
                // 刷新令牌通常可以保持不变，除非有刷新令牌轮换策略
                .refreshToken(jwtRefreshToken)
                .expires(securityProperties.getSession().getAccessTokenExpireTime())
                .build();
    }

    /**
     * 使Token失效（登出操作）。
     * 会删除与JWT访问令牌关联的Redis中的在线用户信息。
     * 注意：刷新令牌相关的映射也应该被清理。
     *
     * @param jwtAccessToken JWT访问令牌。
     */
    @Override
    public void invalidateToken(String jwtAccessToken) {
        Claims claims;
        try {
            claims = getClaimsFromToken(jwtAccessToken);
        } catch (ParamException | ServiceException e) {
            log.warn("尝试使无效的JWT访问令牌失效 (解析失败): {}, 原因: {}", jwtAccessToken, e.getMessage());
            return;
        }
        if (claims == null) {
            log.warn("尝试使无效的JWT访问令牌失效 (Claims为空): {}", jwtAccessToken);
            return;
        }

        // tokenType 检查已被移除

        // 这是 accessTokenId
        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(sessionId)) {
            log.warn("尝试使缺少sessionId ({}) 的JWT访问令牌失效: {}", CLAIM_KEY_SESSION_ID, jwtAccessToken);
            return;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        // 获取以便拿到userId和可能的refreshTokenId
        OnlineLoginUser onlineUser = redisCache.getCacheObject(onlineUserKey);
        // (如果设计如此)

        redisCache.deleteObject(onlineUserKey);
        log.debug("在线用户信息已从Redis删除，访问会话ID: {}，Key: {}", sessionId, onlineUserKey);

        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();
            // 清理单点登录的 userId -> accessTokenId 映射
            String userCurrentAccessIdKey = RedisConstants.Auth.USER_ACCESS_TOKEN + userId;
            String currentMappedAccessTokenId = redisCache.getCacheObject(userCurrentAccessIdKey);
            // 确保是当前会话被删除
            if (sessionId.equals(currentMappedAccessTokenId)) {
                redisCache.deleteObject(userCurrentAccessIdKey);
                log.debug("用户ID {} 的当前访问会话ID映射已删除，Key: {}", userId, userCurrentAccessIdKey);
            } else {
                log.debug("用户ID {} 的当前访问会话ID映射 {} 与登出的会话 {}不符，可能已被新会话覆盖，不删除Key: {}", userId, currentMappedAccessTokenId,
                        sessionId, userCurrentAccessIdKey);
            }
        }
    }

    /**
     * 创建JWT。不再包含tokenType。
     *
     * @param id               令牌的唯一ID (对于访问令牌是accessTokenId，对于刷新令牌是refreshTokenId)
     * @param username         用户名
     * @param validityInMillis 有效期（毫秒）
     * @return JWT字符串
     */
    private String createJwt(String id, String username, long validityInMillis) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_SESSION_ID, id);
        claims.put(CLAIM_KEY_USERNAME, username);
        // REMOVED: claims.put(CLAIM_KEY_TOKEN_TYPE, tokenType);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
                // 使用 setClaims 而不是逐个 set
                .setClaims(claims)
                // sub claim
                .setSubject(username)
                // iat claim
                .setIssuedAt(now)
                // exp claim
                .setExpiration(validity)
                // .setId(id) // jti claim, 可以考虑用 id 作为 jti，但我们已在自定义 claim 中有 sid
                .signWith(jwtSecretKey)
                .compact();
    }

    /**
     * 从JWT中解析Claims。
     * 遇到已知错误（过期、签名错误等）时抛出自定义异常。
     *
     * @param token JWT字符串
     * @return Claims对象，包含JWT的声明信息
     * @throws ParamException 如果JWT无效 (例如格式错误、签名错误、过期)
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
            log.warn("JWT已过期, message: {}", e.getMessage());
            // 或者更具体的 REFRESH_TOKEN_EXPIRED
            throw new ParamException(ACCESS_TOKEN_INVALID, "令牌已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("不支持的JWT格式, message: {}", e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID, "令牌格式不支持");
        } catch (MalformedJwtException e) {
            log.warn("JWT结构错误, message: {}", e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID, "令牌结构错误");
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败, message: {}", e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID, "令牌签名无效");
        } catch (IllegalArgumentException e) { // 通常是token为空或null
            log.warn("JWT claims字符串为空或无效参数, message: {}", e.getMessage());
            throw new ParamException(ACCESS_TOKEN_INVALID, "令牌参数无效");
        }
    }

    /**
     * 构建在线用户信息的Redis Key。
     * 例如：ACCESS_TOKEN_USER:{sessionId}
     *
     * @param sessionId 会话ID（访问令牌ID）
     * @return Redis Key 字符串
     */
    private String formatOnlineUserKeyBySessionId(String sessionId) {
        return RedisConstants.Auth.ACCESS_TOKEN_USER + sessionId;
    }

    /**
     * 构建刷新令牌映射的Redis Key。
     * 例如：REFRESH_TOKEN_MAPPING:{refreshTokenId}
     *
     * @param refreshTokenId 刷新令牌ID
     * @return Redis Key 字符串
     */
    private String formatRefreshTokenMappingKey(String refreshTokenId) {
        return RedisConstants.Auth.REFRESH_TOKEN_MAPPING + refreshTokenId;
    }

    /**
     * 构建在线用户信息对象。
     * 包括用户名、会话ID、IP地址、地区、部门ID、用户ID和角色集合。
     *
     * @param user          用户详情对象
     * @param accessTokenId 访问令牌ID
     * @return OnlineLoginUser 对象
     */
    private OnlineLoginUser buildOnlineUser(SysUserDetails user, String accessTokenId) {

        Set<String> roleSetByRoleId = sysRoleService.getRoleSetByRoleId(user.getUserId());

        // 应检查是否为null
        HttpServletRequest httpServletRequest = SecurityUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(httpServletRequest);
        // IPUtils.getRegion 可能需要处理 "Unknown" IP
        String region = IPUtils.getRegion(ipAddr);

        OnlineLoginUser.OnlineLoginUserBuilder builder = OnlineLoginUser.builder()
                .username(user.getUsername())
                // 这是访问令牌ID
                .sessionId(accessTokenId)
                .IP(ipAddr)
                .region(region)
                .deptId(user.getDeptId())
                .userId(user.getUserId())
                .roles(roleSetByRoleId);
        // 如果计划在登出时清理刷新令牌，或者 OnlineLoginUser 需要知道它的 refreshTokenId
        // 可以在这里或生成token后set进去，例如：.refreshTokenId(generatedRefreshTokenId)
        return builder.build();
    }

    /**
     * 设置在线用户的客户端信息。
     * 包括IP地址、地区、操作系统、浏览器、设备制造商等信息。
     *
     * @param onlineUser 在线用户信息
     */
    private void setClientInfo(OnlineLoginUser onlineUser) {
        // 应检查是否为null
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
     * 构建用户详情对象。
     *
     * @param onlineUser  在线用户信息
     * @param authorities 权限集合
     * @return SysUserDetails 对象
     */
    private SysUserDetails buildUserDetails(OnlineLoginUser onlineUser, Set<SimpleGrantedAuthority> authorities) {
        SysUserDetails userDetails = new SysUserDetails();
        userDetails.setUserId(onlineUser.getUserId());
        userDetails.setUsername(onlineUser.getUsername());
        userDetails.setDeptId(onlineUser.getDeptId());
        userDetails.setAuthorities(authorities);
        return userDetails;
    }

    /**
     * 处理单设备登录逻辑。
     * 如果启用了单设备登录，检查当前用户的旧会话ID，并将其强制下线。
     *
     * @param userId           用户ID
     * @param newAccessTokenId 新的访问令牌ID
     */
    private void handleSingleDeviceLogin(Long userId, String newAccessTokenId) {
        if (!securityProperties.getSession().getSingleLogin()) {
            return;
        }

        String userPreviousAccessIdKey = RedisConstants.Auth.USER_ACCESS_TOKEN + userId;
        String previousAccessTokenId = redisCache.getCacheObject(userPreviousAccessIdKey);

        if (StringUtils.isNotBlank(previousAccessTokenId) && !previousAccessTokenId.equals(newAccessTokenId)) {
            String oldOnlineUserKey = formatOnlineUserKeyBySessionId(previousAccessTokenId);
            OnlineLoginUser oldOnlineUser = redisCache.getCacheObject(oldOnlineUserKey);
            if (oldOnlineUser != null) {
                log.info("单设备登录：用户 {} (ID:{}) 的旧会话 {} (Key:{}) 将被新会话 {} 强制下线。",
                        oldOnlineUser.getUsername(), userId, previousAccessTokenId, oldOnlineUserKey, newAccessTokenId);
                redisCache.deleteObject(oldOnlineUserKey);
            } else {
                log.warn("单设备登录：用户ID {} 的旧会话 {} 在Redis中未找到OnlineLoginUser对象，Key: {}", userId, previousAccessTokenId,
                        oldOnlineUserKey);
            }
        }

        redisCache.setCacheObject(userPreviousAccessIdKey, newAccessTokenId,
                securityProperties.getSession().getAccessTokenExpireTime());
        log.debug("用户ID {} 的当前访问会话ID已映射为 {}，Key: {}", userId, newAccessTokenId, userPreviousAccessIdKey);
    }
}
