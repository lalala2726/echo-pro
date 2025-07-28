package cn.zhangchuangla.framework.security.device;

import cn.zhangchuangla.common.core.constant.SecurityConstants;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.security.OnlineLoginUser;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.enums.DeviceType;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.AccessDeniedException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.common.redis.core.RedisHashCache;
import cn.zhangchuangla.common.redis.core.RedisZSetCache;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import cn.zhangchuangla.framework.security.token.RedisTokenStore;
import cn.zhangchuangla.framework.security.token.TokenService;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 登录设备管理服务类，提供设备会话的查询、删除和强制下线功能。
 * <p>
 * 该服务确保在删除设备信息时，自动清理相关的访问令牌和刷新令牌，以维护系统数据一致性。
 * </p>
 *
 * @author Chuang
 * @since 2025/7/24 22:06
 */
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RedisZSetCache redisZSetCache;
    private final RedisHashCache redisHashCache;
    private final SysUserService sysUserService;
    private final RedisTokenStore redisTokenStore;
    private final RedisCache redisCache;
    private final TokenService tokenService;

    /**
     * 查询指定用户的登录设备列表
     *
     * @param username 用户名
     * @param request  查询参数
     * @return 登录设备列表
     */
    public PageResult<SessionDevice> getDeviceListByUsername(String username, SessionDeviceQueryRequest request) {
        Assert.isTrue(!username.isBlank(), "用户名不能为空");
        SysUser user = sysUserService.getUserInfoByUsername(username);
        Assert.isTrue(user != null, "用户不存在");

        Set<SessionDevice> deviceSet = new LinkedHashSet<>();
        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        Set<ZSetOperations.TypedTuple<String>> allDeviceIndex = redisZSetCache.getAllWithScore(deviceIndexRedisKey);

        //获取用户设备的索引信息并转换为SessionDevice
        allDeviceIndex.forEach(tuple -> {
            String member = tuple.getValue();
            Double score = tuple.getScore();
            // 转换成 SessionDevice
            SessionDevice device = convertToSessionDevice(member, score);
            if (device != null) {
                deviceSet.add(device);
            }
        });

        List<SessionDevice> sessionDevices = new ArrayList<>(deviceSet);
        return querySessionDevice(sessionDevices, request);
    }

    /**
     * 查看在线用户详细信息
     *
     * @param refreshTokenId 刷新令牌ID
     * @return 在线用户详细信息
     */
    public OnlineLoginUser getOnlineLoginUser(String refreshTokenId) {
        Assert.isTrue(tokenService.validateRefreshToken(refreshTokenId), "刷新令牌无效!");
        String accessTokenId = redisTokenStore.getRefreshToken(refreshTokenId);
        String accessTokenRedisKey = RedisConstants.Auth.USER_ACCESS_TOKEN + accessTokenId;
        boolean exists = redisCache.exists(accessTokenRedisKey);
        Assert.isTrue(exists, "访问令牌不存在!");
        return redisTokenStore.getAccessToken(accessTokenRedisKey);
    }


    /**
     * 系统管理员或开发人员专用：根据刷新令牌会话ID，删除对应设备和会话。
     *
     * @param refreshTokenId 刷新令牌会话ID，不能为空
     * @return 删除是否成功
     */
    public boolean deleteDevice(String refreshTokenId) {
        Assert.hasText(refreshTokenId, "刷新令牌会话ID不能为空");

        // 先查出这个 tokenId 所对应的用户名
        String redisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
        boolean exists = redisCache.exists(redisKey);
        if (!exists) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "设备信息不存在");
        }
        Map<String, String> info = redisHashCache.hGetAll(redisKey);
        Assert.notEmpty(info, "设备信息不存在");

        String username = info.get(SecurityConstants.USER_NAME);
        // 直接走公共删除逻辑
        removeDeviceSessions(refreshTokenId, username);
        return true;
    }

    /**
     * 终端用户专用：只能删除自己名下的设备。
     *
     * @param refreshTokenId 刷新令牌会话ID，不能为空
     * @param username       当前登录用户名，不能为空
     * @return 删除是否成功
     */
    public boolean deleteDeviceAsUser(String refreshTokenId, String username) {
        Assert.hasText(refreshTokenId, "刷新令牌会话ID不能为空");
        Assert.hasText(username, "用户名不能为空");

        String redisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
        boolean exists = redisCache.exists(redisKey);
        if (!exists) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "设备信息不存在");
        }
        Map<String, String> info = redisHashCache.hGetAll(redisKey);
        Assert.notEmpty(info, "设备信息不存在");
        String owner = info.get(SecurityConstants.USER_NAME);
        // 只能删除自己的
        if (!username.equals(owner)) {
            throw new AccessDeniedException("您只能删除自己名下的设备");
        }

        removeDeviceSessions(refreshTokenId, username);
        return true;
    }

    /**
     * 删除设备会话
     *
     * @param refreshTokenId 刷新令牌会话ID
     * @param username       设备所属用户名，用于定位 ZSet 索引
     */
    private void removeDeviceSessions(String refreshTokenId, String username) {
        // 1. 从用户的 session 索引 ZSet 中移除这条记录
        String indexKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        redisZSetCache.zRemove(indexKey, refreshTokenId);

        // 2. 删除设备详情 Hash
        String infoKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
        redisCache.deleteObject(infoKey);

        // 3. 删除相关的 OAuth2 刷新令牌和访问令牌
        redisTokenStore.deleteRefreshTokenAndAccessToken(refreshTokenId);
    }


    /**
     * 查询设备列表
     *
     * @param request 查询条件
     * @return 设备列表
     */
    public PageResult<SessionDevice> listDevice(SessionDeviceQueryRequest request) {
        // 1. 构造 Key 模式，匹配所有用户的 session 索引 ZSet
        String keyPattern = RedisConstants.Auth.SESSIONS_INDEX_KEY + "*";

        // 2. 批量扫描出所有符合模式的 ZSet Key 以及它们的成员和分数
        Map<String, Set<ZSetOperations.TypedTuple<Object>>> allDeviceIndex =
                redisZSetCache.scanKeysWithValues(keyPattern);
        // 3. 结果集合，用于去重
        Set<SessionDevice> deviceSet = new LinkedHashSet<>();
        // 4. 遍历每个 ZSet Key
        allDeviceIndex.forEach((zsetKey, tuples) -> {
            // 5. 遍历该用户 ZSet 中的每个成员
            for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                String member = String.valueOf(tuple.getValue());
                Double score = tuple.getScore();
                // 6. 转换成 SessionDevice
                SessionDevice device = convertToSessionDevice(member, score);
                if (device != null) {
                    deviceSet.add(device);
                }
            }
        });
        List<SessionDevice> sessionDevices = new ArrayList<>(deviceSet);
        return querySessionDevice(sessionDevices, request);
    }


    /**
     * 查询会话设备
     *
     * @param sessionDevices 会话设备列表
     * @param request        查询参数
     * @return 查询结果
     */
    public PageResult<SessionDevice> querySessionDevice(List<SessionDevice> sessionDevices, SessionDeviceQueryRequest request) {
        // 关键字段过滤条件
        String nameKeyword = request.getDeviceName();
        DeviceType typeKeyword = request.getDeviceType();
        String ipKeyword = request.getIp();
        String locKeyword = request.getLocation();

        // 分页参数（-1 表示不分页）
        long pageNum = request.getPageNum();
        long pageSize = request.getPageSize();
        boolean noPaging = pageNum == -1 && pageSize == -1;

        // 构造过滤+排序流
        List<SessionDevice> filtered = sessionDevices.stream()
                // 名称模糊查询
                .filter(sd -> {
                    if (nameKeyword == null || nameKeyword.isBlank()) {
                        return true;
                    }
                    String n = sd.getDeviceName();
                    return n != null && !n.isBlank() && n.toLowerCase().contains(nameKeyword.toLowerCase());
                })
                // 类型匹配
                .filter(sd -> {
                    if (typeKeyword == null) {
                        return true;
                    }
                    return sd.getDeviceType() == typeKeyword;
                })
                // IP 模糊匹配
                .filter(sd -> {
                    if (ipKeyword == null || ipKeyword.isBlank()) {
                        return true;
                    }
                    String ip = sd.getIp();
                    return ip != null && !ip.isBlank() && ip.toLowerCase().contains(ipKeyword.toLowerCase());
                })
                // location 模糊匹配
                .filter(sd -> {
                    if (locKeyword == null || locKeyword.isBlank()) {
                        return true;
                    }
                    String loc = sd.getLocation();
                    return loc != null && !loc.isBlank() && loc.toLowerCase().contains(locKeyword.toLowerCase());
                })
                // 排序：按 loginTime 倒序（时间晚的排前面）
                .sorted(Comparator.comparing(
                        SessionDevice::getLoginTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();

        long total = filtered.size();
        List<SessionDevice> rows;

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
                    .toList();
        }

        return PageResult.<SessionDevice>builder()
                .pageNum(noPaging ? -1L : pageNum)
                .pageSize(noPaging ? -1L : pageSize)
                .total(total)
                .rows(rows)
                .build();
    }


    /**
     * 根据字符串和值转换为SessionDevice对象。示例方法，具体实现需按你的业务补全字段解析。
     *
     * @param value redis存储的设备信息（如JSON或ID字符串）
     * @param score 分值（如需使用可扩展，部分场景可忽略）
     * @return SessionDevice对象或null
     */
    private SessionDevice convertToSessionDevice(String value, Double score) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String deviceRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + value;
        Map<String, Object> deviceInfo = redisHashCache.hGetAll(deviceRedisKey);
        if (deviceInfo.isEmpty()) {
            return null;
        }

        DeviceType deviceType = null;
        Object typeObj = deviceInfo.get(SecurityConstants.DEVICE_TYPE);
        if (typeObj != null) {
            deviceType = DeviceType.getByValue(typeObj.toString());
        }
        String deviceName = Optional.ofNullable(deviceInfo.get(SecurityConstants.DEVICE_NAME))
                .map(Object::toString)
                .orElse(null);

        String username = Optional.ofNullable(deviceInfo.get(SecurityConstants.USER_NAME))
                .map(Object::toString)
                .orElse(null);

        Long userId = Long.valueOf(Objects.requireNonNull(Optional.ofNullable(deviceInfo.get(SecurityConstants.USER_ID))
                .map(Object::toString)
                .orElse(null)));

        String ip = Optional.ofNullable(deviceInfo.get(SecurityConstants.IP))
                .map(Object::toString)
                .orElse(null);
        String location = Optional.ofNullable(deviceInfo.get(SecurityConstants.LOCATION))
                .map(Object::toString)
                .orElse(null);

        String refreshTokenId = Optional.ofNullable(deviceInfo.get(SecurityConstants.REFRESH_TOKEN_ID))
                .map(Object::toString)
                .orElse(null);

        Date loginDate = null;
        Object loginObj = deviceInfo.get(SecurityConstants.LOGIN_TIME);
        long timestamp;
        if (loginObj instanceof Number) {
            timestamp = ((Number) loginObj).longValue();
        } else if (loginObj instanceof String) {
            try {
                timestamp = Long.parseLong((String) loginObj);
            } catch (NumberFormatException e) {
                timestamp = 0L;
            }
        } else {
            timestamp = (score != null) ? score.longValue() : 0L;
        }
        if (timestamp > 0) {
            loginDate = new Date(timestamp);
        }

        // 3. 构建 SessionDevice
        return SessionDevice.builder()
                .deviceType(deviceType)
                .username(username)
                .refreshTokenId(refreshTokenId)
                .userId(userId)
                .deviceName(deviceName)
                .ip(ip)
                .location(location)
                .loginTime(loginDate)
                .build();
    }


    /**
     * 通过用户名删除全部设备信息,注意!删除设备信息后,用户将无法登录系统
     *
     * @param username 用户名
     * @return 操作结果
     */
    public boolean deleteDeviceByUsername(String username) {
        Assert.isTrue(!username.isBlank(), "用户名不能为空!");
        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        Set<String> refreshTokenIds = new HashSet<>();
        Set<ZSetOperations.TypedTuple<String>> allWithScore = redisZSetCache.getAllWithScore(deviceIndexRedisKey);
        allWithScore.forEach(tuple -> {
            refreshTokenIds.add(tuple.getValue());
        });
        // 删除设备信息和访问令牌和刷新令牌信息
        refreshTokenIds.forEach(refreshTokenId -> {
            String deviceInfoRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
            redisTokenStore.deleteRefreshTokenAndAccessToken(refreshTokenId);
            redisCache.deleteObject(deviceInfoRedisKey);
        });
        //删除设备索引
        redisCache.deleteObject(deviceIndexRedisKey);
        return true;
    }

    /**
     * 通过用户名和设备类型删除设备,删除设备之后也会删除会话
     *
     * @param username   用户名
     * @param deviceType 设备信息
     * @return 操作结果
     */
    public boolean deleteDeviceByUsername(String username, DeviceType deviceType) {
        Assert.isTrue(!username.isBlank(), "用户名不能为空!");
        Assert.notNull(deviceType, "设备类型不能为空!");

        String deviceIndexRedisKey = RedisConstants.Auth.SESSIONS_INDEX_KEY + username;
        Set<String> refreshTokenIds = new HashSet<>();
        Set<ZSetOperations.TypedTuple<String>> allWithScore = redisZSetCache.getAllWithScore(deviceIndexRedisKey);

        // 筛选出指定设备类型的会话
        allWithScore.forEach(tuple -> {
            String refreshTokenId = tuple.getValue();
            String deviceInfoRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
            Map<String, String> deviceInfo = redisHashCache.hGetAll(deviceInfoRedisKey);

            if (!deviceInfo.isEmpty()) {
                String loginDeviceType = deviceInfo.get(SecurityConstants.DEVICE_TYPE);
                if (deviceType.getValue().equals(loginDeviceType)) {
                    refreshTokenIds.add(refreshTokenId);
                }
            }
        });

        // 删除指定设备类型的设备信息和令牌
        refreshTokenIds.forEach(refreshTokenId -> {
            String deviceInfoRedisKey = RedisConstants.Auth.SESSIONS_DEVICE_KEY + refreshTokenId;
            // 删除刷新令牌和访问令牌
            redisTokenStore.deleteRefreshTokenAndAccessToken(refreshTokenId);
            // 删除设备信息
            redisCache.deleteObject(deviceInfoRedisKey);
            // 从索引中移除该设备
            redisZSetCache.zRemove(deviceIndexRedisKey, refreshTokenId);
        });

        return !refreshTokenIds.isEmpty();
    }
}
