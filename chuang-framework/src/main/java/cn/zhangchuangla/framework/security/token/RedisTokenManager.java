package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.entity.security.AuthenticationToken;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.UUIDUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.zhangchuangla.common.core.enums.ResultCode.ACCESS_TOKEN_INVALID;
import static cn.zhangchuangla.common.core.enums.ResultCode.REFRESH_TOKEN_INVALID;


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
    private final SysUserService userService;


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
        // 获取当前用户的信息
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        String accessTokenId = UUIDUtils.simple();
        String refreshTokenId = UUIDUtils.simple();

        OnlineLoginUser onlineLoginUser = buildOnlineUser(userDetails, accessTokenId);
        setClientInfo(onlineLoginUser);

        String onlineUserKey = formatOnlineUserKeyBySessionId(accessTokenId);
        redisCache.setCacheObject(onlineUserKey, onlineLoginUser,
                securityProperties.getSession().getAccessTokenExpireTime());

        String jwtAccessToken = createJwt(accessTokenId, username);
        String jwtRefreshToken = createJwt(refreshTokenId, username);

        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        // 刷新令牌的Redis value 存储的是它对应的 accessTokenId
        redisCache.setCacheObject(refreshTokenMappingKey, accessTokenId,
                securityProperties.getSession().getRefreshTokenExpireTime());

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
            return null;
        }

        redisCache.expire(onlineUserKey, securityProperties.getSession().getAccessTokenExpireTime());

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
        // 如果解析失败或过期，getClaimsFromToken会抛出异常
        claims = getClaimsFromToken(jwtAccessToken);
        // 以防万一 getClaimsFromToken 返回 null 而不是抛异常
        if (claims == null) {
            return false;
        }
        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(sessionId)) {
            return false;
        }

        String onlineUserKey = formatOnlineUserKeyBySessionId(sessionId);
        return redisCache.hasKey(onlineUserKey);
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
        } catch (AuthorizationException e) {
            return false;
        }
        if (claims == null) {
            return false;
        }
        // tokenType 检查已被移除

        // 刷新令牌JWT中的ID是refreshTokenId
        String refreshTokenId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(refreshTokenId)) {
            return false;
        }
        return redisCache.hasKey(formatRefreshTokenMappingKey(refreshTokenId));
    }

    /**
     * 使用JWT刷新令牌刷新访问令牌。
     *
     * @param jwtRefreshToken JWT刷新令牌。
     * @return 新的AuthenticationToken，包含新的JWT访问令牌和原始JWT刷新令牌。
     * @throws AuthorizationException 如果刷新令牌无效或关联的用户会话不存在。
     */
    @Override
    public AuthenticationToken refreshToken(String jwtRefreshToken) {
        // 内部会处理过期等问题并抛异常
        Claims refreshClaims = getClaimsFromToken(jwtRefreshToken);
        if (refreshClaims == null) {
            throw new AuthorizationException(REFRESH_TOKEN_INVALID, "无法解析刷新令牌Claims");
        }

        // 检验当前刷新令牌是否是合法的
        if (validateAccessToken(jwtRefreshToken)) {
            throw new AuthorizationException(ResultCode.INVALID_TOKEN);
        }

        String refreshTokenId = refreshClaims.get(CLAIM_KEY_SESSION_ID, String.class);

        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        if (!redisCache.hasKey(refreshTokenMappingKey)) {
            throw new AuthorizationException(REFRESH_TOKEN_INVALID, "刷新令牌已失效");
        }

        String username = refreshClaims.get(CLAIM_KEY_USERNAME, String.class);

        // 创建新的访问令牌
        String simpleUuid = UUIDUtils.simple();
        String accessToken = createJwt(simpleUuid, username);


        // 获取用户角色并构建用户详情对象
        SysUser user = userService.getUserInfoByUsername(username);
        Set<String> roleSetByUserId = sysRoleService.getRoleSetByUserId(user.getUserId());
        OnlineLoginUser onlineLoginUser = OnlineLoginUser.builder()
                .userId(user.getUserId())
                .roles(roleSetByUserId)
                .deptId(user.getDeptId())
                .username(user.getUsername())
                .build();


        // 设置客户端信息
        setClientInfo(onlineLoginUser);

        // 保存在线用户信息
        redisCache.setCacheObject(formatOnlineUserKeyBySessionId(simpleUuid), onlineLoginUser, securityProperties.getSession().getAccessTokenExpireTime());

        //更新刷新令牌的映射关系
        Long keyExpire = redisCache.getKeyExpire(refreshTokenMappingKey);
        redisCache.setCacheObject(refreshTokenMappingKey, simpleUuid, keyExpire);
        // 返回新的访问令牌
        return AuthenticationToken.builder()
                .accessToken(accessToken)
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
        } catch (AuthorizationException e) {
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

        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();
            // 清理单点登录的 userId -> accessTokenId 映射
            String userCurrentAccessIdKey = RedisConstants.Auth.USER_ACCESS_TOKEN + userId;
            String currentMappedAccessTokenId = redisCache.getCacheObject(userCurrentAccessIdKey);
            // 确保是当前会话被删除
            if (sessionId.equals(currentMappedAccessTokenId)) {
                redisCache.deleteObject(userCurrentAccessIdKey);
            }
        }
    }

    /**
     * 创建JWT。不再包含tokenType。
     *
     * @param id       令牌的唯一ID (对于访问令牌是accessTokenId，对于刷新令牌是refreshTokenId)
     * @param username 用户名
     * @return JWT字符串
     */
    private String createJwt(String id, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_SESSION_ID, id);
        claims.put(CLAIM_KEY_USERNAME, username);
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

        HttpServletRequest httpServletRequest = SecurityUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddress(httpServletRequest);
        String region = IPUtils.getRegion(ipAddr);

        OnlineLoginUser.OnlineLoginUserBuilder builder = OnlineLoginUser.builder()
                .username(user.getUsername())
                // 这是访问令牌ID
                .sessionId(accessTokenId)
                .ip(ipAddr)
                .region(region)
                .deptId(user.getDeptId())
                .userId(user.getUserId())
                .roles(roleSetByRoleId);
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
        String ipAddr = IPUtils.getIpAddress(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        String osName = UserAgentUtils.getOsName(userAgent);
        String browserName = UserAgentUtils.getBrowserName(userAgent);
        String deviceManufacturer = UserAgentUtils.getDeviceManufacturer(userAgent);

        onlineUser.setIp(ipAddr);
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
    }
}
