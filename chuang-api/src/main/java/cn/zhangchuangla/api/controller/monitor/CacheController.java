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

import java.util.*;
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
    public AjaxResult<Map<String, Object>> getRedisInfo() {
        Map<String, Object> result = new HashMap<>(4);
        try {
            Properties info = getRedisBaseInfo();
            Properties commandStats = getCommandStats();
            Long dbSize = getDbSize();

            result.put("info", info);
            result.put("dbSize", dbSize);
            result.put("commandStats", parseCommandStats(commandStats));

            return success(result);
        } catch (Exception e) {
            return error("获取Redis信息失败: " + e.getMessage());
        }
    }

/**
 * 获取 Redis 基础信息
 */
private Properties getRedisBaseInfo() {
    return redisTemplate.execute((RedisCallback<Properties>) connection ->
            Optional.ofNullable(connection.serverCommands().info())
                    .orElseGet(Properties::new));
}

/**
 * 获取 Redis 命令统计
 */
private Properties getCommandStats() {
    return redisTemplate.execute((RedisCallback<Properties>) connection ->
            Optional.ofNullable(connection.serverCommands().info("commandstats"))
                    .orElseGet(Properties::new));
}

    /**
     * 获取数据库大小
     */
    private Long getDbSize() {
        return redisTemplate.execute((RedisCallback<Long>) connection ->
                Optional.ofNullable(connection.serverCommands().dbSize())
                        .orElse(0L));
    }

    /**
     * 解析命令统计信息
     */
    private List<Map<String, String>> parseCommandStats(Properties commandStats) {
        return Optional.of(commandStats)
                .map(stats -> stats.stringPropertyNames().stream()
                        .map(key -> {
                            Map<String, String> data = new HashMap<>(2);
                            String property = stats.getProperty(key);
                            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
                            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
                            return data;
                        })
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}