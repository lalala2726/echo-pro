package cn.zhangchuangla.framework.security.session;

import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.framework.model.vo.OnlineLoginUser;
import cn.zhangchuangla.framework.security.device.DeviceService;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import cn.zhangchuangla.system.core.model.request.monitor.OnlineUserQueryRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/28 14:38
 */
@Service
@AllArgsConstructor
@Slf4j
public class SessionService {

    private final RedisTokenStore redisTokenStore;
    private final DeviceService deviceService;
    private RedisCache redisCache;

    /**
     * 获取在线用户列表
     *
     * @param request 查询参数
     * @return 在线用户列表
     */
    public PageResult<OnlineLoginUser> sessionList(OnlineUserQueryRequest request) {
        String accessTokenRedisKey = RedisConstants.Auth.USER_ACCESS_TOKEN + "*";
        List<OnlineLoginUser> sessionList = new ArrayList<>();
        Map<String, Object> stringObjectMap = redisCache.scanKeysWithValues(accessTokenRedisKey);
        stringObjectMap.forEach((key, value) -> {
            if (value instanceof OnlineLoginUser) {
                sessionList.add((OnlineLoginUser) value);
            }
        });

        return queryOnlineUsers(sessionList, request);
    }

    /**
     * 查询在线用户
     *
     * @param onlineUsers 在线用户列表
     * @param request     查询参数
     * @return 查询结果
     */
    private PageResult<OnlineLoginUser> queryOnlineUsers(List<OnlineLoginUser> onlineUsers, OnlineUserQueryRequest request) {
        // 关键字段过滤条件
        String sessionIdKeyword = request.getSessionId();
        String usernameKeyword = request.getUsername();
        Long userIdKeyword = request.getUserId();
        String ipKeyword = request.getIp();
        String regionKeyword = request.getRegion();

        // 分页参数（-1 表示不分页）
        long pageNum = request.getPageNum();
        long pageSize = request.getPageSize();
        boolean noPaging = pageNum == -1 && pageSize == -1;

        // 构造过滤+排序流
        List<OnlineLoginUser> filtered = onlineUsers.stream()
                // 会话ID模糊查询
                .filter(user -> {
                    if (sessionIdKeyword == null || sessionIdKeyword.isBlank()) {
                        return true;
                    }
                    String sessionId = user.getAccessTokenId();
                    return sessionId != null && !sessionId.isBlank() && sessionId.toLowerCase().contains(sessionIdKeyword.toLowerCase());
                })
                // 用户名模糊查询
                .filter(user -> {
                    if (usernameKeyword == null || usernameKeyword.isBlank()) {
                        return true;
                    }
                    String username = user.getUsername();
                    return username != null && !username.isBlank() && username.toLowerCase().contains(usernameKeyword.toLowerCase());
                })
                // 用户ID匹配
                .filter(user -> {
                    if (userIdKeyword == null) {
                        return true;
                    }
                    return userIdKeyword.equals(user.getUserId());
                })
                // IP 模糊匹配
                .filter(user -> {
                    if (ipKeyword == null || ipKeyword.isBlank()) {
                        return true;
                    }
                    String ip = user.getIp();
                    return ip != null && !ip.isBlank() && ip.toLowerCase().contains(ipKeyword.toLowerCase());
                })
                // 地区模糊匹配
                .filter(user -> {
                    if (regionKeyword == null || regionKeyword.isBlank()) {
                        return true;
                    }
                    String region = user.getLocation();
                    return region != null && !region.isBlank() && region.toLowerCase().contains(regionKeyword.toLowerCase());
                })
                // 排序：按 loginTime 倒序（时间晚的排前面）
                .sorted(Comparator.comparing(
                        OnlineLoginUser::getAccessTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .collect(Collectors.toList());

        long total = filtered.size();
        List<OnlineLoginUser> rows;

        if (noPaging) {
            // 不分页，返回所有
            rows = filtered;
        } else {
            // 正常分页
            long validPageNum = Math.max(pageNum, 1);
            long validPageSize = Math.max(pageSize, 1);
            long skip = (validPageNum - 1) * validPageSize;
            rows = filtered.stream()
                    .skip(skip)
                    .limit(validPageSize)
                    .collect(Collectors.toList());
        }

        return PageResult.<OnlineLoginUser>builder()
                .pageNum(noPaging ? -1L : pageNum)
                .pageSize(noPaging ? -1L : pageSize)
                .total(total)
                .rows(rows)
                .build();
    }

    /**
     * 获取会话详情
     *
     * @param accessTokenId 访问令牌ID
     * @return 会话详情
     */
    public OnlineLoginUser sessionDetail(String accessTokenId) {
        Assert.isTrue(!accessTokenId.isBlank(), "会话ID不存在!");
        return redisTokenStore.getAccessToken(accessTokenId);
    }

    /**
     * 删除会话
     *
     * @param accessTokenId 访问令牌ID
     * @return 是否删除成功
     */
    public boolean deleteSession(String accessTokenId) {
        Assert.isTrue(redisTokenStore.isValidAccessToken(accessTokenId), "会话ID不存在!");
        String refreshTokenId = redisTokenStore.getRefreshTokenIdByAccessTokenId(accessTokenId);
        //删除设备信息
        deviceService.deleteDevice(refreshTokenId);
        //删除会话信息
        redisTokenStore.deleteAccessToken(accessTokenId);
        redisTokenStore.deleteRefreshToken(refreshTokenId);
        return true;
    }
}
