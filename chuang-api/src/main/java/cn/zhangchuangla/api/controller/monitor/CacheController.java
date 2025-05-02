package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 缓存监控接口
 * 提供Redis缓存相关监控信息
 *
 * @author Chuang
 * created on 2025/3/20 09:55
 */
@RestController
@RequestMapping("/monitor/cache")
@Tag(name = "缓存监控")
@RequiredArgsConstructor
public class CacheController extends BaseController {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 获取 Redis 基本信息
     *
     * @return Redis基本信息、数据库大小、命令统计
     */
    @GetMapping
    @Operation(summary = "Redis基本信息")
    @PreAuthorize("@ss.hasPermission('monitor:cache:list')")
    public AjaxResult<Map<String, Object>> list() {
        // 执行 Redis 命令
        Properties info = getRedisInfo();
        Properties commandStats = getRedisCommandStats();
        Long dbSize = getRedisDbSize();

        // 组装结果
        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);
        result.put("commandStats", buildCommandStats(commandStats));

        return success(result);
    }

    /**
     * 获取 Redis 基本信息
     *
     * @return Properties
     */
    private Properties getRedisInfo() {
        return (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
    }

    /**
     * 获取 Redis 命令统计信息
     *
     * @return Properties
     */
    private Properties getRedisCommandStats() {
        return (Properties) redisTemplate
                .execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
    }

    /**
     * 获取 Redis 数据库大小
     *
     * @return 数据库大小
     */
    private Long getRedisDbSize() {
        return (Long) redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());
    }

    /**
     * 构建 Redis 命令统计数据
     *
     * @param commandStats Redis 命令统计信息
     * @return List<Map < String, String>>
     */
    private List<Map<String, String>> buildCommandStats(Properties commandStats) {
        return commandStats.stringPropertyNames()
                .stream()
                .map(key -> {
                    Map<String, String> data = new HashMap<>(2);
                    String property = commandStats.getProperty(key);
                    data.put("name", StringUtils.removeStart(key, "cmdstat_"));
                    data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
                    return data;
                })
                .collect(Collectors.toList());
    }

}
