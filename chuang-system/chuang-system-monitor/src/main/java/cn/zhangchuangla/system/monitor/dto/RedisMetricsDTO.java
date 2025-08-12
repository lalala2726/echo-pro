package cn.zhangchuangla.system.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Redis监控指标DTO
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Data
@Schema(description = "Redis监控指标")
public class RedisMetricsDTO {

    /**
     * 采集时间
     */
    @Schema(description = "采集时间", type = "string", example = "2025-07-28T10:00:00")
    private LocalDateTime timestamp;

    /**
     * Redis基本信息
     */
    @Schema(description = "Redis基本信息", type = "object")
    private RedisInfo info;

    /**
     * 内存指标
     */
    @Schema(description = "内存指标", type = "object")
    private MemoryMetrics memory;

    /**
     * 连接指标
     */
    @Schema(description = "连接指标", type = "object")
    private ConnectionMetrics connections;

    /**
     * 命令统计
     */
    @Schema(description = "命令统计", type = "object")
    private CommandStats commandStats;

    /**
     * 键空间统计
     */
    @Schema(description = "键空间统计", type = "object")
    private Map<String, KeyspaceStats> keyspace;

    /**
     * 性能指标
     */
    @Schema(description = "性能指标", type = "object")
    private PerformanceMetrics performance;

    /**
     * Redis基本信息
     */
    @Data
    @Schema(description = "Redis基本信息", type = "object")
    public static class RedisInfo {
        /**
         * Redis版本
         */
        @Schema(description = "Redis版本", type = "string", example = "6.2.6")
        private String version;

        /**
         * Redis模式
         */
        @Schema(description = "Redis模式", type = "string", example = "standalone")
        private String mode;

        /**
         * 运行时间（秒）
         */
        @Schema(description = "运行时间（秒）", type = "integer", example = "86400")
        private long uptimeInSeconds;

        /**
         * 运行时间（天）
         */
        @Schema(description = "运行时间（天）", type = "integer", example = "1")
        private long uptimeInDays;

        /**
         * 服务器时间
         */
        @Schema(description = "服务器时间", type = "string", example = "2025-07-28T10:00:00")
        private LocalDateTime serverTime;

        /**
         * 进程ID
         */
        @Schema(description = "进程ID", type = "integer", example = "12345")
        private long processId;

        /**
         * TCP端口
         */
        @Schema(description = "TCP端口", type = "integer", example = "6379")
        private int tcpPort;

        /**
         * 配置文件路径
         */
        @Schema(description = "配置文件路径", type = "string", example = "/etc/redis/redis.conf")
        private String configFile;
    }

    /**
     * 内存指标
     */
    @Data
    @Schema(description = "内存指标", type = "object")
    public static class MemoryMetrics {
        /**
         * 已使用内存（字节）
         */
        @Schema(description = "已使用内存（字节）", type = "integer", example = "1048576")
        private long usedMemory;

        /**
         * 已使用内存（人类可读）
         */
        @Schema(description = "已使用内存（人类可读）", type = "string", example = "1.00M")
        private String usedMemoryHuman;

        /**
         * RSS内存（字节）
         */
        @Schema(description = "RSS内存（字节）", type = "integer", example = "2097152")
        private long usedMemoryRss;

        /**
         * 峰值内存（字节）
         */
        @Schema(description = "峰值内存（字节）", type = "integer", example = "3145728")
        private long usedMemoryPeak;

        /**
         * 峰值内存（人类可读）
         */
        @Schema(description = "峰值内存（人类可读）", type = "string", example = "3.00M")
        private String usedMemoryPeakHuman;

        /**
         * Lua脚本内存（字节）
         */
        @Schema(description = "Lua脚本内存（字节）", type = "integer", example = "37888")
        private long usedMemoryLua;

        /**
         * 内存碎片率
         */
        @Schema(description = "内存碎片率", type = "number", example = "1.25")
        private double memFragmentationRatio;

        /**
         * 最大内存（字节）
         */
        @Schema(description = "最大内存（字节）", type = "integer", example = "1073741824")
        private long maxMemory;

        /**
         * 最大内存策略
         */
        @Schema(description = "最大内存策略", type = "string", example = "allkeys-lru")
        private String maxMemoryPolicy;
    }

    /**
     * 连接指标
     */
    @Data
    @Schema(description = "连接指标", type = "object")
    public static class ConnectionMetrics {
        /**
         * 当前连接数
         */
        @Schema(description = "当前连接数", type = "integer", example = "10")
        private int connectedClients;

        /**
         * 最大连接数
         */
        @Schema(description = "最大连接数", type = "integer", example = "10000")
        private int maxClients;

        /**
         * 阻塞的客户端数
         */
        @Schema(description = "阻塞的客户端数", type = "integer", example = "0")
        private int blockedClients;

        /**
         * 总连接数
         */
        @Schema(description = "总连接数", type = "integer", example = "100")
        private long totalConnectionsReceived;

        /**
         * 拒绝的连接数
         */
        @Schema(description = "拒绝的连接数", type = "integer", example = "0")
        private long rejectedConnections;
    }

    /**
     * 命令统计
     */
    @Data
    @Schema(description = "命令统计", type = "object")
    public static class CommandStats {
        /**
         * 总命令数
         */
        @Schema(description = "总命令数", type = "integer", example = "10000")
        private long totalCommandsProcessed;

        /**
         * 每秒命令数
         */
        @Schema(description = "每秒命令数", type = "number", example = "100.5")
        private double instantaneousOpsPerSec;

        /**
         * 命令详细统计
         */
        @Schema(description = "命令详细统计", type = "object")
        private Map<String, CommandStat> commands;

        @Data
        @Schema(description = "单个命令统计", type = "object")
        public static class CommandStat {
            /**
             * 调用次数
             */
            @Schema(description = "调用次数", type = "integer", example = "500")
            private long calls;

            /**
             * 总耗时（微秒）
             */
            @Schema(description = "总耗时（微秒）", type = "integer", example = "10000")
            private long usec;

            /**
             * 平均耗时（微秒）
             */
            @Schema(description = "平均耗时（微秒）", type = "number", example = "20.0")
            private double usecPerCall;
        }
    }

    /**
     * 键空间统计
     */
    @Data
    @Schema(description = "键空间统计", type = "object")
    public static class KeyspaceStats {
        /**
         * 键数量
         */
        @Schema(description = "键数量", type = "integer", example = "1000")
        private long keys;

        /**
         * 过期键数量
         */
        @Schema(description = "过期键数量", type = "integer", example = "100")
        private long expires;

        /**
         * 平均TTL
         */
        @Schema(description = "平均TTL", type = "integer", example = "3600000")
        private long avgTtl;
    }

    /**
     * 性能指标
     */
    @Data
    @Schema(description = "性能指标", type = "object")
    public static class PerformanceMetrics {
        /**
         * 键命中次数
         */
        @Schema(description = "键命中次数", type = "integer", example = "5000")
        private long keyspaceHits;

        /**
         * 键未命中次数
         */
        @Schema(description = "键未命中次数", type = "integer", example = "100")
        private long keyspaceMisses;

        /**
         * 命中率（百分比）
         */
        @Schema(description = "命中率（百分比）", type = "number", example = "98.0")
        private double hitRate;

        /**
         * 过期键数量
         */
        @Schema(description = "过期键数量", type = "integer", example = "50")
        private long expiredKeys;

        /**
         * 淘汰键数量
         */
        @Schema(description = "淘汰键数量", type = "integer", example = "20")
        private long evictedKeys;

        /**
         * 网络输入字节数
         */
        @Schema(description = "网络输入字节数", type = "integer", example = "1048576")
        private long totalNetInputBytes;

        /**
         * 网络输出字节数
         */
        @Schema(description = "网络输出字节数", type = "integer", example = "2097152")
        private long totalNetOutputBytes;

        /**
         * 每秒网络输入字节数
         */
        @Schema(description = "每秒网络输入字节数", type = "number", example = "1024.5")
        private double instantaneousInputKbps;

        /**
         * 每秒网络输出字节数
         */
        @Schema(description = "每秒网络输出字节数", type = "number", example = "2048.0")
        private double instantaneousOutputKbps;
    }
}
