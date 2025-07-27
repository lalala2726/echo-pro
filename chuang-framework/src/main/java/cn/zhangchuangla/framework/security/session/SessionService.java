package cn.zhangchuangla.framework.security.session;

import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisZSetCache;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 登录设备管理（查询、删除、踢下线）
 *
 * @author Chuang
 * <p>
 * created on 2025/7/24 22:06
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final RedisZSetCache redisZSetCache;


    public List<SessionDevice> listDevice(SessionDeviceQueryRequest request) {
        // 分批扫描数量
        final int batchScanQuantity = 1000;
        String deviceKey = RedisConstants.Auth.SESSIONS_INDEX_KEY;
        List<SessionDevice> result = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                redisZSetCache.scanZSet(deviceKey, "*", batchScanQuantity);
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            String value = tuple.getValue();
            Double score = tuple.getScore();
            if (value != null) {
                SessionDevice device = convertToSessionDevice(value, score);
                if (device != null) {
                    result.add(device);
                }
            }
        }
        return result;
    }

    /**
     * 根据字符串和值转换为SessionDevice对象。示例方法，具体实现需按你的业务补全字段解析。
     *
     * @param value redis存储的设备信息（如JSON或ID字符串）
     * @param score 分值（如需使用可扩展，部分场景可忽略）
     * @return SessionDevice对象或null
     */
    private SessionDevice convertToSessionDevice(String value, Double score) {
        // TODO: 根据你的实际业务结构，例如JSON字符串转对象，或用ID查详情
        // 示例假设value为JSON，可用FastJSON/Gson等反序列化
        // return JSON.parseObject(value, SessionDeviceQueryRequest.class);
        return null;
    }


}
