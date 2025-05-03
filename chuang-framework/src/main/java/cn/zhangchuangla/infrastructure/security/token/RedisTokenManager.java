package cn.zhangchuangla.infrastructure.security.token;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.config.property.SecurityProperties;
import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.AuthenticationToken;
import cn.zhangchuangla.common.core.security.model.OnlineLoginUser;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.UserAgentUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

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
public class RedisTokenManager implements TokenManager {

    private final SecurityProperties securityProperties;
    private final RedisCache redisCache;

    public RedisTokenManager(SecurityProperties securityProperties, RedisCache redisCache) {
        this.securityProperties = securityProperties;
        this.redisCache = redisCache;
    }

    /**
     * 生成 Token
     *
     * @param authentication 用户认证信息
     * @return 生成的 AuthenticationToken 对象
     */
    @Override
    public AuthenticationToken generateToken(Authentication authentication) {
        SysUserDetails user = (SysUserDetails) authentication.getPrincipal();
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();

        // 构建用户在线信息
        OnlineLoginUser onlineUser = OnlineLoginUser.builder()
                .username(user.getUsername())
                .deptId(user.getDeptId())
                .userId(user.getUserId())
                .roles(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
        setClientInfo(onlineUser);
        // 存储访问令牌、刷新令牌和刷新令牌映射
        storeTokensInRedis(accessToken, refreshToken, onlineUser);

        // 单设备登录控制
        handleSingleDeviceLogin(user.getUserId(), accessToken);

        return AuthenticationToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expires(securityProperties.getSession().getAccessTokenExpireTime())
                .build();
    }

    private void setClientInfo(OnlineLoginUser onlineUser) {
        //从Security中获取当前request
        HttpServletRequest httpServletRequest = SecurityUtils.getHttpServletRequest();
        String ipAddr = IPUtils.getIpAddr(httpServletRequest);
        String userAgent = UserAgentUtils.getUserAgent(httpServletRequest);
        String osName = UserAgentUtils.getOsName(userAgent);
        String browserName = UserAgentUtils.getBrowserName(userAgent);
        String deviceManufacturer = UserAgentUtils.getDeviceManufacturer(userAgent);

        //设置基本的信息
        String region = IPUtils.getRegion(ipAddr);
        onlineUser.setIP(ipAddr);
        onlineUser.setRegion(region);
        onlineUser.setOs(osName);
        onlineUser.setBrowser(browserName);
        onlineUser.setDevice(deviceManufacturer);
        onlineUser.setLoginTime(System.currentTimeMillis());
        onlineUser.setUserAgent(userAgent);
    }

    /**
     * 根据 token 解析用户信息
     *
     * @param token JWT Token
     * @return 构建的 Authentication 对象
     */
    @Override
    public Authentication parseToken(String token) {
        OnlineLoginUser onlineUser = redisCache.getCacheObject(formatTokenKey(token));
        if (onlineUser == null) return null;

        // 构建用户权限集合
        Set<SimpleGrantedAuthority> authorities = null;

        Set<String> roles = onlineUser.getRoles();
        if (CollectionUtil.isNotEmpty(roles)) {
            authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }

        // 构建用户详情对象
        SysUserDetails userDetails = buildUserDetails(onlineUser, authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /**
     * 校验访问令牌是否有效
     *
     * @param token 访问令牌
     * @return 是否有效
     */
    @Override
    public boolean validateAccessToken(String token) {
        return redisCache.hasKey(formatTokenKey(token));
    }

    /**
     * 校验刷新令牌是否有效
     *
     * @param refreshToken 刷新令牌
     * @return 是否有效
     */
    @Override
    public boolean validateRefreshToken(String refreshToken) {
        String format = StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
        return redisCache.hasKey(format);
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新生成的 AuthenticationToken 对象
     */
    @Override
    public AuthenticationToken refreshToken(String refreshToken) {
        OnlineLoginUser onlineUser = redisCache.getCacheObject(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
        if (onlineUser == null) {
            throw new ServiceException(ResponseCode.REFRESH_TOKEN_INVALID);
        }

        String oldAccessToken = redisCache.getCacheObject(StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId()));

        // 删除旧的访问令牌记录
        if (oldAccessToken != null) {
            redisCache.deleteObject(formatTokenKey(oldAccessToken));
        }

        // 生成新访问令牌并存储
        String newAccessToken = IdUtil.fastSimpleUUID();
        storeAccessToken(newAccessToken, onlineUser);

        int accessTtl = securityProperties.getSession().getAccessTokenExpireTime();
        return AuthenticationToken.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .expires(accessTtl)
                .build();
    }

    /**
     * 使访问令牌失效
     *
     * @param token 访问令牌
     */
    @Override
    public void invalidateToken(String token) {
        OnlineLoginUser onlineUser = redisCache.getCacheObject(formatTokenKey(token));
        if (onlineUser != null) {
            Long userId = onlineUser.getUserId();
            // 1. 删除访问令牌相关
            String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
            String accessToken = (String) redisCache.getCacheObject(userAccessKey);
            if (accessToken != null) {
                redisCache.deleteObject(formatTokenKey(accessToken));
                redisCache.deleteObject(userAccessKey);
            }

            // 2. 删除刷新令牌相关
            String userRefreshKey = StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, userId);
            String refreshToken = redisCache.getCacheObject(userRefreshKey);
            if (refreshToken != null) {
                redisCache.deleteObject(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
                redisCache.deleteObject(userRefreshKey);
            }
        }
    }

    /**
     * 将访问令牌和刷新令牌存储至 Redis
     *
     * @param accessToken  访问令牌
     * @param refreshToken 刷新令牌
     * @param onlineUser   在线用户信息
     */
    private void storeTokensInRedis(String accessToken, String refreshToken, OnlineLoginUser onlineUser) {
        // 访问令牌 -> 用户信息
        setRedisValue(formatTokenKey(accessToken), onlineUser, securityProperties.getSession().getAccessTokenExpireTime());

        // 刷新令牌 -> 用户信息
        String refreshTokenKey = StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
        setRedisValue(refreshTokenKey, onlineUser, securityProperties.getSession().getRefreshTokenExpireTime());

        // 用户ID -> 刷新令牌
        setRedisValue(StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, onlineUser.getUserId()),
                refreshToken,
                securityProperties.getSession().getRefreshTokenExpireTime());
    }

    /**
     * 处理单设备登录控制
     *
     * @param userId      用户ID
     * @param accessToken 新生成的访问令牌
     */
    private void handleSingleDeviceLogin(Long userId, String accessToken) {
        Boolean allowMultiLogin = securityProperties.getSession().getSingleLogin();
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
        // 单设备登录控制，删除旧的访问令牌
        if (!allowMultiLogin) {
            String oldAccessToken = (String) redisCache.getCacheObject(userAccessKey);
            if (oldAccessToken != null) {
                redisCache.deleteObject(formatTokenKey(oldAccessToken));
            }
        }
        // 存储访问令牌映射（用户ID -> 访问令牌），用于单设备登录控制删除旧的访问令牌和刷新令牌时删除旧令牌
        setRedisValue(userAccessKey, accessToken, securityProperties.getSession().getAccessTokenExpireTime());
    }

    /**
     * 存储新的访问令牌
     *
     * @param newAccessToken 新访问令牌
     * @param onlineUser     在线用户信息
     */
    private void storeAccessToken(String newAccessToken, OnlineLoginUser onlineUser) {
        setRedisValue(StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, newAccessToken), onlineUser, securityProperties.getSession().getAccessTokenExpireTime());
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId());
        setRedisValue(userAccessKey, newAccessToken, securityProperties.getSession().getAccessTokenExpireTime());
    }

    /**
     * 构建用户详情对象
     *
     * @param onlineUser  在线用户信息
     * @param authorities 权限集合
     * @return SysUserDetails 用户详情
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
     * 格式化访问令牌的 Redis 键
     *
     * @param token 访问令牌
     * @return 格式化后的 Redis 键
     */
    private String formatTokenKey(String token) {
        return StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
    }

    /**
     * 将值存储到 Redis
     *
     * @param key   键
     * @param value 值
     * @param ttl   过期时间（秒），-1表示永不过期
     */
    private void setRedisValue(String key, Object value, int ttl) {
        if (ttl != -1) {
            redisCache.setCacheObject(key, value, ttl);
        } else {
            // ttl=-1时永不过期
            redisCache.setCacheObject(key, value);
        }
    }
}
