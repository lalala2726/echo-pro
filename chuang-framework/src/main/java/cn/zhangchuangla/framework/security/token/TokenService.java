package cn.zhangchuangla.framework.security.token;

import cn.zhangchuangla.common.core.config.property.SecurityProperties;
import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.entity.security.AuthTokenVo;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AuthorizationException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.core.utils.UUIDUtils;
import cn.zhangchuangla.common.core.utils.client.IPUtils;
import cn.zhangchuangla.common.core.utils.client.UserAgentUtils;
import cn.zhangchuangla.framework.model.dto.LoginSessionDTO;
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

import static cn.zhangchuangla.common.core.constant.SecurityConstants.CLAIM_KEY_SESSION_ID;


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


    private final JwtTokenProvider jwtTokenProvider;
    private final SysRoleService sysRoleService;
    private final SecurityProperties securityProperties;
    private final SysUserService userService;
    private final RedisTokenStore redisTokenStore;


    /**
     * 生成JWT Token（访问令牌和刷新令牌）。
     *
     * @param authentication Spring Security的认证信息对象。
     * @return 包含JWT访问令牌和JWT刷新令牌的AuthenticationToken对象。
     */

    // todo 这边内部不负责获取设备信息IP等信息,统一让上层统一传入!
    public LoginSessionDTO createToken(Authentication authentication) {
        // 获取当前用户的信息
        SysUserDetails userDetails = (SysUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Long userId = userDetails.getUserId();

        // 随机生成刷新令牌和访问令牌的sessionId
        String accessTokenSessionId = UUIDUtils.simple();
        String refreshTokenSessionId = UUIDUtils.simple();

        // 构造在线用户信息
        OnlineLoginUser onlineLoginUser = buildOnlineUser(userDetails, accessTokenSessionId);
        setClientInfo(onlineLoginUser);

        redisTokenStore.setRefreshToken(refreshTokenSessionId, accessTokenSessionId);
        redisTokenStore.setAccessToken(accessTokenSessionId, onlineLoginUser);
        String jwtAccessToken = jwtTokenProvider.createJwt(accessTokenSessionId, username);
        String jwtRefreshToken = jwtTokenProvider.createJwt(refreshTokenSessionId, username);

        return LoginSessionDTO.builder()
                .userId(userId)
                .accessTokenSessionId(accessTokenSessionId)
                .refreshTokenSessionId(refreshTokenSessionId)
                .username(username)
                .accessToken(jwtAccessToken)
                .refreshToken(jwtRefreshToken)
                .build();
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

        String accessTokenSessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(accessTokenSessionId)) {
            log.warn("访问令牌JWT中未找到sessionId ({}): {}", CLAIM_KEY_SESSION_ID, accessToken);
            return null;
        }

        OnlineLoginUser onlineUser = redisTokenStore.getAccessToken(accessTokenSessionId);
        if (onlineUser == null) {
            return null;
        }
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

        String refreshTokenSessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(refreshTokenSessionId)) {
            return false;
        }
        return redisTokenStore.isValidRefreshToken(refreshTokenSessionId);
    }

    /**
     * 使用JWT刷新令牌刷新访问令牌。
     *
     * @param jwtRefreshToken JWT刷新令牌。
     * @return 新的AuthenticationToken，包含新的JWT访问令牌和原始JWT刷新令牌。
     * @throws AuthorizationException 如果刷新令牌无效或关联的用户会话不存在。
     */
    public AuthTokenVo refreshToken(String jwtRefreshToken) {

        //先经过JWT验证
        Claims refreshClaims = jwtTokenProvider.getClaimsFromToken(jwtRefreshToken);
        if (refreshClaims == null) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        String refreshTokenSessionId = refreshClaims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (!redisTokenStore.isValidRefreshToken(refreshTokenSessionId)) {
            throw new AuthorizationException(ResultCode.REFRESH_TOKEN_INVALID);
        }
        String username = refreshClaims.get(SecurityConstants.CLAIM_KEY_USERNAME, String.class);
        // 创建新的访问令牌
        String accessTokenSessionId = UUIDUtils.simple();
        String accessToken = jwtTokenProvider.createJwt(accessTokenSessionId, username);

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
        // 保存刷新令牌
        redisTokenStore.setAccessToken(accessTokenSessionId, onlineLoginUser);
        //重新设置新的访问令牌和刷新令牌的映射关系
        redisTokenStore.mapRefreshTokenToAccessToken(refreshTokenSessionId, accessTokenSessionId);
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
        String accessTokenSessionId = claims.get(CLAIM_KEY_SESSION_ID, String.class);
        if (StringUtils.isBlank(accessTokenSessionId)) {
            return false;
        }

        return redisTokenStore.isValidAccessToken(accessTokenSessionId);
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
     * 从token中解析sessionId
     *
     * @param token 令牌
     * @return sessionId
     */
    public String getSessionId(String token) {
        Assert.notEmpty(token, "令牌不能为空");
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        return claims.get(CLAIM_KEY_SESSION_ID, String.class);
    }
}
