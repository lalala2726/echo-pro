package cn.zhangchuangla.common.excel.core;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典数据处理器
 * 用于处理Excel导出时的字典值转换
 *
 * @author Chuang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DictDataHandler {

    private final RedisCache redisCache;

    /**
     * 本地缓存，避免频繁访问Redis
     */
    private final Map<String, Map<String, String>> localCache = new ConcurrentHashMap<>();


    /**
     * 清除本地缓存（全量）
     */
    public void clearAllLocalCache() {
        localCache.clear();
    }

    /**
     * 清除指定字典键的本地缓存
     */
    public void clearLocalCache(String dictKey) {
        if (StringUtils.isNotBlank(dictKey)) {
            localCache.remove(dictKey);
        }
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictKey   字典类型
     * @param dictValue 字典值
     * @return 字典标签，如果未找到则返回原值
     */
    public String getDictLabel(String dictKey, String dictValue) {
        if (StringUtils.isBlank(dictKey) || StringUtils.isBlank(dictValue)) {
            return dictValue;
        }

        try {
            // 先从本地缓存获取
            Map<String, String> dictMap = localCache.get(dictKey);
            if (dictMap == null) {
                // 从Redis获取字典数据
                dictMap = loadDictFromRedis(dictKey);
                if (dictMap != null && !dictMap.isEmpty()) {
                    localCache.put(dictKey, dictMap);
                }
            }

            if (dictMap != null && dictMap.containsKey(dictValue)) {
                return dictMap.get(dictValue);
            }

            log.debug("未找到字典数据: dictType={}, dictValue={}", dictKey, dictValue);
            return dictValue;
        } catch (Exception e) {
            log.error("获取字典标签失败: dictType={}, dictValue={}", dictKey, dictValue, e);
            return dictValue;
        }
    }

    /**
     * 从Redis加载字典数据
     *
     * @param dictKey 字典类型
     * @return 字典值到标签的映射
     */
    private Map<String, String> loadDictFromRedis(String dictKey) {
        try {
            String cacheKey = String.format(RedisConstants.Dict.DICT_DATA_KEY, dictKey);
            List<Option<String>> dictValues = redisCache.getCacheObject(cacheKey);

            if (dictValues == null || dictValues.isEmpty()) {
                log.debug("Redis中未找到字典数据: dictType={}", dictKey);
                return null;
            }

            Map<String, String> dictMap = new ConcurrentHashMap<>();
            for (Option<String> item : dictValues) {
                if (item != null && item.getValue() != null && item.getLabel() != null) {
                    dictMap.put(item.getValue(), item.getLabel());
                }
            }

            log.debug("从Redis加载字典数据成功: dictType={}, size={}", dictKey, dictMap.size());
            return dictMap;
        } catch (Exception e) {
            log.error("从Redis加载字典数据失败: dictType={}", dictKey, e);
            return null;
        }
    }

    /**
     * 预加载字典数据到本地缓存
     *
     * @param dictKeys 字典类型列表
     */
    public void preloadDictData(List<String> dictKeys) {
        if (dictKeys == null || dictKeys.isEmpty()) {
            return;
        }

        for (String dictKey : dictKeys) {
            if (StringUtils.isNotBlank(dictKey) && !localCache.containsKey(dictKey)) {
                Map<String, String> dictMap = loadDictFromRedis(dictKey);
                if (dictMap != null && !dictMap.isEmpty()) {
                    localCache.put(dictKey, dictMap);
                }
            }
        }
    }
}
