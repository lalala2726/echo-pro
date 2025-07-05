package cn.zhangchuangla.common.excel.core;

import cn.zhangchuangla.common.core.core.entity.Option;
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
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签，如果未找到则返回原值
     */
    public String getDictLabel(String dictType, String dictValue) {
        if (StringUtils.isBlank(dictType) || StringUtils.isBlank(dictValue)) {
            return dictValue;
        }

        try {
            // 先从本地缓存获取
            Map<String, String> dictMap = localCache.get(dictType);
            if (dictMap == null) {
                // 从Redis获取字典数据
                dictMap = loadDictFromRedis(dictType);
                if (dictMap != null && !dictMap.isEmpty()) {
                    localCache.put(dictType, dictMap);
                }
            }

            if (dictMap != null && dictMap.containsKey(dictValue)) {
                return dictMap.get(dictValue);
            }

            log.debug("未找到字典项: dictType={}, dictValue={}", dictType, dictValue);
            return dictValue;
        } catch (Exception e) {
            log.error("获取字典标签失败: dictType={}, dictValue={}", dictType, dictValue, e);
            return dictValue;
        }
    }

    /**
     * 从Redis加载字典数据
     *
     * @param dictType 字典类型
     * @return 字典值到标签的映射
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> loadDictFromRedis(String dictType) {
        try {
            String cacheKey = String.format(RedisConstants.DICT_ITEMS_KEY, dictType);
            List<Option<String>> dictItems = redisCache.getCacheObject(cacheKey);

            if (dictItems == null || dictItems.isEmpty()) {
                log.debug("Redis中未找到字典数据: dictType={}", dictType);
                return null;
            }

            Map<String, String> dictMap = new ConcurrentHashMap<>();
            for (Option<String> item : dictItems) {
                if (item != null && item.getValue() != null && item.getLabel() != null) {
                    dictMap.put(item.getValue(), item.getLabel());
                }
            }

            log.debug("从Redis加载字典数据成功: dictType={}, size={}", dictType, dictMap.size());
            return dictMap;
        } catch (Exception e) {
            log.error("从Redis加载字典数据失败: dictType={}", dictType, e);
            return null;
        }
    }

    /**
     * 清除本地缓存
     *
     * @param dictType 字典类型，如果为空则清除所有缓存
     */
    public void clearCache(String dictType) {
        if (StringUtils.isBlank(dictType)) {
            localCache.clear();
            log.debug("清除所有字典本地缓存");
        } else {
            localCache.remove(dictType);
            log.debug("清除字典本地缓存: dictType={}", dictType);
        }
    }

    /**
     * 预加载字典数据到本地缓存
     *
     * @param dictTypes 字典类型列表
     */
    public void preloadDictData(List<String> dictTypes) {
        if (dictTypes == null || dictTypes.isEmpty()) {
            return;
        }

        for (String dictType : dictTypes) {
            if (StringUtils.isNotBlank(dictType) && !localCache.containsKey(dictType)) {
                Map<String, String> dictMap = loadDictFromRedis(dictType);
                if (dictMap != null && !dictMap.isEmpty()) {
                    localCache.put(dictType, dictMap);
                }
            }
        }
    }
}
