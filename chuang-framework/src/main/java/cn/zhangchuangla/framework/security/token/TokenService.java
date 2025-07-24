package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.entity.security.AuthTokenVo;
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
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static cn.zhangchuangla.common.core.enums.ResultCode.REFRESH_TOKEN_INVALID;

/**
 * AccessToken/RefreshToken生成与校验
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 20:34
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private static final String CLAIM_KEY_SESSION_ID = "session";
    private static final String CLAIM_KEY_USERNAME = "username";

    private final JwtTokenProvider jwtTokenProvider;
    private final SysRoleService sysRoleService;
    private final SecurityProperties securityProperties;
    private final RedisCache redisCache;
    private final SysUserService userService;


    /**
     * 生成JWT Token（访问令牌和刷新令牌）。
     *
     * @param authentication Spring Security的认证信息对象。
     * @return 包含JWT访问令牌和JWT刷新令牌的AuthenticationToken对象。
     */
    public AuthTokenVo createToken(Authentication authentication) {
        // 获取当前用户的信息
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        String accessTokenId = UUIDUtils.simple();
        String refreshTokenId = UUIDUtils.simple();

        OnlineLoginUser onlineLoginUser = buildOnlineUser(userDetails, accessTokenId);
        setClientInfo(onlineLoginUser);

        String onlineUserKey = formatOnlineUserKeyBySessionId(accessTokenId);
        redisCache.setCacheObject(onlineUserKey, onlineLoginUser,
                securityProperties.getSession().getAccessTokenExpireTime());

        String jwtAccessToken = jwtTokenProvider.createJwt(accessTokenId, username);
        String jwtRefreshToken = jwtTokenProvider.createJwt(refreshTokenId, username);

        String refreshTokenMappingKey = formatRefreshTokenMappingKey(refreshTokenId);
        // 刷新令牌的Redis value 存储的是它对应的 accessTokenId
        redisCache.setCacheObject(refreshTokenMappingKey, accessTokenId,
                securityProperties.getSession().getRefreshTokenExpireTime());

        return AuthTokenVo.builder()
                .accessToken(jwtAccessToken)
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
    public void invalidateToken(String jwtAccessToken) {
        Claims claims;
        try {
            claims = jwtTokenProvider.getClaimsFromToken(jwtAccessToken);
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
     * 解析JWT访问令牌获取认证信息。
     * 此方法隐含期望一个访问令牌。
     *
     * @param accessToken JWT访问令牌。
     * @return 用户认证信息（Authentication对象），如果Token无效或解析失败则返回null。
     */
    public Authentication parseAccessToken(String accessToken) {
        // getClaimsFromToken 内部处理异常
        Claims claims = jwtTokenProvider.getClaimsFromToken(accessToken);
        // 如果getClaimsFromToken在无效时返回null而不是抛出异常
        if (claims == null) {
            log.warn("解析访问令牌失败或Claims为空: {}", accessToken);
            return null;
        }

        String sessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(sessionId)) {
            log.warn("访问令牌JWT中未找到sessionId ({}): {}", CLAIM_KEY_SESSION_ID, accessToken);
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
     * 校验JWT刷新令牌是否有效。
     * 验证JWT签名、是否过期，并检查Redis中是否存在对应的映射关系。
     *
     * @param jwtRefreshToken JWT刷新令牌。
     * @return 如果有效返回true，否则返回false。
     */
    public boolean validateRefreshToken(String jwtRefreshToken) {
        Claims claims;
        try {
            claims = jwtTokenProvider.getClaimsFromToken(jwtRefreshToken);
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
    public AuthTokenVo refreshToken(String jwtRefreshToken) {
        // 内部会处理过期等问题并抛异常
        Claims refreshClaims = jwtTokenProvider.getClaimsFromToken(jwtRefreshToken);
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
        String accessToken = jwtTokenProvider.createJwt(simpleUuid, username);


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
        return AuthTokenVo.builder()
                .accessToken(accessToken)
                .refreshToken(jwtRefreshToken)
                .expires(securityProperties.getSession().getAccessTokenExpireTime())
                .build();
    }

    /**
     * 校验JWT访问令牌是否有效。
     * 验证JWT签名、是否过期，并检查Redis中是否存在对应的会话信息。
     *
     * @param jwtAccessToken JWT访问令牌。
     * @return 如果有效返回true，否则返回false。
     */
    public boolean validateAccessToken(String jwtAccessToken) {
        Claims claims;
        // 如果解析失败或过期，getClaimsFromToken会抛出异常
        claims = jwtTokenProvider.getClaimsFromToken(jwtAccessToken);
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
}
