package cn.zhangchuangla.infrastructure.web.service.impl;

import cn.zhangchuangla.common.config.property.SecurityProperties;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.RedisKeyConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.LoginException;
import cn.zhangchuangla.common.utils.IPUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.common.utils.UserAgentUtils;
import cn.zhangchuangla.infrastructure.web.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final SecurityProperties securityProperties;
    private final SecretKey key;
    private final RedisCache redisCache;

    @Autowired
    public TokenServiceImpl(SecurityProperties securityProperties, RedisCache redisCache) {
        this.securityProperties = securityProperties;
        this.key = Keys.hmacShaKeyFor(securityProperties.getSecret().getBytes(StandardCharsets.UTF_8));// 从配置文件中读取密钥字符串并转换为SecretKey
        this.redisCache = redisCache;
    }

    /**
     * 创建token
     *
     * @param sysUserDetails 用户信息
     * @param request        请求
     * @return 返回创建的token
     */
    @Override
    public String createToken(SysUserDetails sysUserDetails, HttpServletRequest request) {
        // 开始生成 token 并将用户信息存储在 Redis 中
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> claims = new HashMap<>();
        sysUserDetails.setSessionId(uuid);
        claims.put(Constants.LOGIN_USER_KEY, uuid);
        setUserAgent(sysUserDetails, request);
        // 将用户信息存储到 Redis 中
        refreshToken(sysUserDetails);
        // 返回 Token
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key)
                .compact();
    }

    /**
     * 获取登录设备基本信息
     *
     * @param sysUserDetails 登录用户信息
     * @param request        请求
     */
    public void setUserAgent(SysUserDetails sysUserDetails, HttpServletRequest request) {
        String userAgent = UserAgentUtils.getUserAgent(request);
        if (StringUtils.isNotBlank(userAgent)) {
            String browserName = UserAgentUtils.getBrowserName(userAgent);
            sysUserDetails.setBrowser(browserName);
            sysUserDetails.setOs(UserAgentUtils.getOsName(userAgent));
        }
        String clientIp = IPUtils.getClientIp(request);
        if (StringUtils.isNotBlank(clientIp)) {
            String addressByIp = IPUtils.getAddressByIp(clientIp);
            sysUserDetails.setIp(clientIp);
            sysUserDetails.setAddress(addressByIp);
        }
    }

    /**
     * 如果是新会话时候将用户基本信息存入到Redis中,如果是旧会话就重新刷新Token
     *
     * @param sysUserDetails 登录信息
     */
    public void refreshToken(SysUserDetails sysUserDetails) {
        Integer expire = securityProperties.getExpire();
        sysUserDetails.setLoginTime(System.currentTimeMillis());
        //fixme 待优化，过期时间优化
        sysUserDetails.setExpireTime(sysUserDetails.getLoginTime() + expire * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(sysUserDetails.getSessionId());
        // 将登录用户信息缓存到 Redis 中
        redisCache.setCacheObject(userKey, sysUserDetails, expire, TimeUnit.MINUTES);
    }

    /**
     * 根据uuid获取登录用户信息
     *
     * @param session 登录用户会话ID
     * @return 登录用户信息
     */
    private String getTokenKey(String session) {
        return RedisKeyConstant.LOGIN_TOKEN_KEY + session;
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
     * @param sysUserDetails 登录用户
     */
    @Override
    public void validateToken(SysUserDetails sysUserDetails) {
        long expireTime = sysUserDetails.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(sysUserDetails);
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
        claims.setExpiration(new Date(System.currentTimeMillis() + securityProperties.getExpire() * 1000));

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
     * @throws JwtException 如果token解析失败
     */
    @Override
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException | SignatureException e) {
            log.warn("非法Token: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("解析Token发生未知错误: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 检查Token状态
     *
     * @param token Token字符串
     * @return Token状态
     */
    @Override
    public TokenStatus checkTokenStatus(String token) {
        if (StringUtils.isBlank(token)) {
            return TokenStatus.INVALID;
        }

        try {
            // 尝试解析token
            Claims claims = parseToken(token);
            String sessionId = (String) claims.get(Constants.LOGIN_USER_KEY);

            if (StringUtils.isBlank(sessionId)) {
                return TokenStatus.INVALID;
            }

            // 检查Redis中是否存在对应的用户信息
            String redisKey = RedisKeyConstant.LOGIN_TOKEN_KEY + sessionId;
            if (!redisCache.hasKey(redisKey)) {
                return TokenStatus.EXPIRED;
            }

            return TokenStatus.VALID;

        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (MalformedJwtException | SignatureException e) {
            return TokenStatus.INVALID;
        } catch (Exception e) {
            return TokenStatus.ERROR;
        }
    }

    /**
     * 获取登录用户信息
     *
     * @param request 请求
     * @return 登录用户信息
     */
    @Override
    public SysUserDetails getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isBlank(token)) {
            return null;
        }

        try {
            Claims claims = parseToken(token);
            String sessionId = (String) claims.get(Constants.LOGIN_USER_KEY);

            if (StringUtils.isBlank(sessionId)) {
                throw new LoginException("Token非法，无法获取用户会话标识");
            }

            return getLoginUserByToken(sessionId);

        } catch (ExpiredJwtException e) {
            throw new LoginException(ResponseCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SignatureException e) {
            throw new LoginException(ResponseCode.INVALID_TOKEN);
        } catch (LoginException le) {
            // 直接抛出登录异常
            throw le;
        } catch (Exception e) {
            throw new LoginException(ResponseCode.USER_NOT_LOGIN);
        }
    }

    /**
     * 根据会话ID获取登录用户信息
     *
     * @param sessionId 会话ID
     * @return 返回登录用户信息
     */
    private SysUserDetails getLoginUserByToken(String sessionId) {
        try {
            String redisKey = RedisKeyConstant.LOGIN_TOKEN_KEY + sessionId;
            Object cacheObject = redisCache.getCacheObject(redisKey);

            // 如果没有找到Redis中的对应对象，则直接认为Token已过期
            if (cacheObject == null) {
                throw new LoginException(ResponseCode.TOKEN_EXPIRED, "会话已过期，请重新登录");
            }

            SysUserDetails sysUserDetails = null;
            // 处理可能的类型转换问题
            if (cacheObject instanceof SysUserDetails) {
                sysUserDetails = (SysUserDetails) cacheObject;
            } else {
                try {
                    // 如果不是LoginUser类型但有值，尝试使用FastJson2进行转换
                    sysUserDetails = com.alibaba.fastjson2.JSON.to(SysUserDetails.class, cacheObject);
                } catch (Exception e) {
                    log.error("用户对象转换失败: {}, 类型: {}", e.getMessage(), cacheObject.getClass().getName(), e);
                    throw new LoginException(ResponseCode.USER_NOT_LOGIN, "用户数据异常");
                }
            }

            // 再次检查转换后的对象是否有效
            if (sysUserDetails == null) {
                throw new LoginException(ResponseCode.USER_NOT_LOGIN, "用户数据为空");
            }
            return sysUserDetails;
        } catch (LoginException le) {
            // 用户未登录或会话过期的异常直接抛出
            throw le;
        } catch (Exception e) {
            log.error("获取用户失败: {}", e.getMessage(), e);
            throw new LoginException(ResponseCode.USER_NOT_LOGIN, "获取用户信息失败");
        }
    }

    /**
     * 获取Token
     *
     * @param request 请求
     * @return token
     */
    @Override
    public String getToken(HttpServletRequest request) {
        String header = securityProperties.getHeader();
        if (StringUtils.isBlank(header)) {
            return null;
        }
        return request.getHeader(header);
    }
}
