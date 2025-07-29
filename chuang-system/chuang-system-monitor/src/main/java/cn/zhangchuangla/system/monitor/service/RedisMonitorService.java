package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.RedisMetricsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Redis监控服务
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisMonitorService {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 获取Redis监控指标
     *
     * @return Redis监控指标
     */
    public RedisMetricsDTO getRedisMetrics() {
        RedisMetricsDTO metrics = new RedisMetricsDTO();
        metrics.setTimestamp(LocalDateTime.now());

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            // 获取Redis信息
            Properties info = connection.serverCommands().info();

            if (info != null) {
                // 获取Redis基本信息
                metrics.setInfo(getRedisInfo(info));

                // 获取内存指标
                metrics.setMemory(getMemoryMetrics(info));

                // 获取连接指标
                metrics.setConnections(getConnectionMetrics(info));

                // 获取命令统计
                metrics.setCommandStats(getCommandStats(info));

                // 获取键空间统计
                metrics.setKeyspace(getKeyspaceStats(info));

                // 获取性能指标
                metrics.setPerformance(getPerformanceMetrics(info));
            }

        } catch (Exception e) {
            log.error("获取Redis监控指标失败", e);
        }

        return metrics;
    }

    /**
     * 获取Redis基本信息
     */
    private RedisMetricsDTO.RedisInfo getRedisInfo(Properties info) {
        RedisMetricsDTO.RedisInfo redisInfo = new RedisMetricsDTO.RedisInfo();

        redisInfo.setVersion(info.getProperty("redis_version"));
        redisInfo.setMode(info.getProperty("redis_mode", "standalone"));

        String uptimeInSeconds = info.getProperty("uptime_in_seconds");
        if (uptimeInSeconds != null) {
            long uptime = Long.parseLong(uptimeInSeconds);
            redisInfo.setUptimeInSeconds(uptime);
            redisInfo.setUptimeInDays(uptime / 86400);
        }

        redisInfo.setServerTime(LocalDateTime.now());

        String processId = info.getProperty("process_id");
        if (processId != null) {
            redisInfo.setProcessId(Long.parseLong(processId));
        }

        String tcpPort = info.getProperty("tcp_port");
        if (tcpPort != null) {
            redisInfo.setTcpPort(Integer.parseInt(tcpPort));
        }

        redisInfo.setConfigFile(info.getProperty("config_file", ""));

        return redisInfo;
    }

    /**
     * 获取内存指标
     */
    private RedisMetricsDTO.MemoryMetrics getMemoryMetrics(Properties info) {
        RedisMetricsDTO.MemoryMetrics memoryMetrics = new RedisMetricsDTO.MemoryMetrics();

        String usedMemory = info.getProperty("used_memory");
        if (usedMemory != null) {
            memoryMetrics.setUsedMemory(Long.parseLong(usedMemory));
        }

        memoryMetrics.setUsedMemoryHuman(info.getProperty("used_memory_human"));

        String usedMemoryRss = info.getProperty("used_memory_rss");
        if (usedMemoryRss != null) {
            memoryMetrics.setUsedMemoryRss(Long.parseLong(usedMemoryRss));
        }

        String usedMemoryPeak = info.getProperty("used_memory_peak");
        if (usedMemoryPeak != null) {
            memoryMetrics.setUsedMemoryPeak(Long.parseLong(usedMemoryPeak));
        }

        memoryMetrics.setUsedMemoryPeakHuman(info.getProperty("used_memory_peak_human"));

        String usedMemoryLua = info.getProperty("used_memory_lua");
        if (usedMemoryLua != null) {
            memoryMetrics.setUsedMemoryLua(Long.parseLong(usedMemoryLua));
        }

        String memFragmentationRatio = info.getProperty("mem_fragmentation_ratio");
        if (memFragmentationRatio != null) {
            memoryMetrics.setMemFragmentationRatio(Double.parseDouble(memFragmentationRatio));
        }

        String maxMemory = info.getProperty("maxmemory");
        if (maxMemory != null) {
            memoryMetrics.setMaxMemory(Long.parseLong(maxMemory));
        }

        memoryMetrics.setMaxMemoryPolicy(info.getProperty("maxmemory_policy"));

        return memoryMetrics;
    }

    /**
     * 获取连接指标
     */
    private RedisMetricsDTO.ConnectionMetrics getConnectionMetrics(Properties info) {
        RedisMetricsDTO.ConnectionMetrics connectionMetrics = new RedisMetricsDTO.ConnectionMetrics();

        String connectedClients = info.getProperty("connected_clients");
        if (connectedClients != null) {
            connectionMetrics.setConnectedClients(Integer.parseInt(connectedClients));
        }

        String maxClients = info.getProperty("maxclients");
        if (maxClients != null) {
            connectionMetrics.setMaxClients(Integer.parseInt(maxClients));
        }

        String blockedClients = info.getProperty("blocked_clients");
        if (blockedClients != null) {
            connectionMetrics.setBlockedClients(Integer.parseInt(blockedClients));
        }

        String totalConnectionsReceived = info.getProperty("total_connections_received");
        if (totalConnectionsReceived != null) {
            connectionMetrics.setTotalConnectionsReceived(Long.parseLong(totalConnectionsReceived));
        }

        String rejectedConnections = info.getProperty("rejected_connections");
        if (rejectedConnections != null) {
            connectionMetrics.setRejectedConnections(Long.parseLong(rejectedConnections));
        }

        return connectionMetrics;
    }

    /**
     * 获取命令统计
     */
    private RedisMetricsDTO.CommandStats getCommandStats(Properties info) {
        RedisMetricsDTO.CommandStats commandStats = new RedisMetricsDTO.CommandStats();

        String totalCommandsProcessed = info.getProperty("total_commands_processed");
        if (totalCommandsProcessed != null) {
            commandStats.setTotalCommandsProcessed(Long.parseLong(totalCommandsProcessed));
        }

        String instantaneousOpsPerSec = info.getProperty("instantaneous_ops_per_sec");
        if (instantaneousOpsPerSec != null) {
            commandStats.setInstantaneousOpsPerSec(Double.parseDouble(instantaneousOpsPerSec));
        }

        // 解析命令详细统计
        Map<String, RedisMetricsDTO.CommandStats.CommandStat> commands = new HashMap<>();
        Pattern commandPattern = Pattern.compile("cmdstat_([^:]+):calls=(\\d+),usec=(\\d+),usec_per_call=([\\d.]+)");

        for (String key : info.stringPropertyNames()) {
            if (key.startsWith("cmdstat_")) {
                String value = info.getProperty(key);
                Matcher matcher = commandPattern.matcher(key + ":" + value);
                if (matcher.matches()) {
                    String commandName = matcher.group(1);
                    RedisMetricsDTO.CommandStats.CommandStat stat = new RedisMetricsDTO.CommandStats.CommandStat();
                    stat.setCalls(Long.parseLong(matcher.group(2)));
                    stat.setUsec(Long.parseLong(matcher.group(3)));
                    stat.setUsecPerCall(Double.parseDouble(matcher.group(4)));
                    commands.put(commandName, stat);
                }
            }
        }
        commandStats.setCommands(commands);

        return commandStats;
    }

    /**
     * 获取键空间统计
     */
    private Map<String, RedisMetricsDTO.KeyspaceStats> getKeyspaceStats(Properties info) {
        Map<String, RedisMetricsDTO.KeyspaceStats> keyspaceStats = new HashMap<>();

        Pattern keyspacePattern = Pattern.compile("db(\\d+):keys=(\\d+),expires=(\\d+),avg_ttl=(\\d+)");

        for (String key : info.stringPropertyNames()) {
            if (key.startsWith("db")) {
                String value = info.getProperty(key);
                Matcher matcher = keyspacePattern.matcher(key + ":" + value);
                if (matcher.matches()) {
                    String dbName = "db" + matcher.group(1);
                    RedisMetricsDTO.KeyspaceStats stats = new RedisMetricsDTO.KeyspaceStats();
                    stats.setKeys(Long.parseLong(matcher.group(2)));
                    stats.setExpires(Long.parseLong(matcher.group(3)));
                    stats.setAvgTtl(Long.parseLong(matcher.group(4)));
                    keyspaceStats.put(dbName, stats);
                }
            }
        }

        return keyspaceStats;
    }

    /**
     * 获取性能指标
     */
    private RedisMetricsDTO.PerformanceMetrics getPerformanceMetrics(Properties info) {
        RedisMetricsDTO.PerformanceMetrics performanceMetrics = new RedisMetricsDTO.PerformanceMetrics();

        String keyspaceHits = info.getProperty("keyspace_hits");
        if (keyspaceHits != null) {
            performanceMetrics.setKeyspaceHits(Long.parseLong(keyspaceHits));
        }

        String keyspaceMisses = info.getProperty("keyspace_misses");
        if (keyspaceMisses != null) {
            performanceMetrics.setKeyspaceMisses(Long.parseLong(keyspaceMisses));
        }

        // 计算命中率
        long hits = performanceMetrics.getKeyspaceHits();
        long misses = performanceMetrics.getKeyspaceMisses();
        if (hits + misses > 0) {
            double hitRate = (double) hits / (hits + misses) * 100;
            performanceMetrics.setHitRate(Math.round(hitRate * 100.0) / 100.0);
        } else {
            performanceMetrics.setHitRate(0.0);
        }

        String expiredKeys = info.getProperty("expired_keys");
        if (expiredKeys != null) {
            performanceMetrics.setExpiredKeys(Long.parseLong(expiredKeys));
        }

        String evictedKeys = info.getProperty("evicted_keys");
        if (evictedKeys != null) {
            performanceMetrics.setEvictedKeys(Long.parseLong(evictedKeys));
        }

        String totalNetInputBytes = info.getProperty("total_net_input_bytes");
        if (totalNetInputBytes != null) {
            performanceMetrics.setTotalNetInputBytes(Long.parseLong(totalNetInputBytes));
        }

        String totalNetOutputBytes = info.getProperty("total_net_output_bytes");
        if (totalNetOutputBytes != null) {
            performanceMetrics.setTotalNetOutputBytes(Long.parseLong(totalNetOutputBytes));
        }

        String instantaneousInputKbps = info.getProperty("instantaneous_input_kbps");
        if (instantaneousInputKbps != null) {
            performanceMetrics.setInstantaneousInputKbps(Double.parseDouble(instantaneousInputKbps));
        }

        String instantaneousOutputKbps = info.getProperty("instantaneous_output_kbps");
        if (instantaneousOutputKbps != null) {
            performanceMetrics.setInstantaneousOutputKbps(Double.parseDouble(instantaneousOutputKbps));
        }

        return performanceMetrics;
    }
}
