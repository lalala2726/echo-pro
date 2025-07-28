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
    @Schema(description = "采集时间")
    private LocalDateTime timestamp;

    /**
     * Redis基本信息
     */
    @Schema(description = "Redis基本信息")
    private RedisInfo info;

    /**
     * 内存指标
     */
    @Schema(description = "内存指标")
    private MemoryMetrics memory;

    /**
     * 连接指标
     */
    @Schema(description = "连接指标")
    private ConnectionMetrics connections;

    /**
     * 命令统计
     */
    @Schema(description = "命令统计")
    private CommandStats commandStats;

    /**
     * 键空间统计
     */
    @Schema(description = "键空间统计")
    private Map<String, KeyspaceStats> keyspace;

    /**
     * 性能指标
     */
    @Schema(description = "性能指标")
    private PerformanceMetrics performance;

    /**
     * Redis基本信息
     */
    @Data
    @Schema(description = "Redis基本信息")
    public static class RedisInfo {
        /**
         * Redis版本
         */
        @Schema(description = "Redis版本")
        private String version;

        /**
         * Redis模式
         */
        @Schema(description = "Redis模式")
        private String mode;

        /**
         * 运行时间（秒）
         */
        @Schema(description = "运行时间（秒）")
        private long uptimeInSeconds;

        /**
         * 运行时间（天）
         */
        @Schema(description = "运行时间（天）")
        private long uptimeInDays;

        /**
         * 服务器时间
         */
        @Schema(description = "服务器时间")
        private LocalDateTime serverTime;

        /**
         * 进程ID
         */
        @Schema(description = "进程ID")
        private long processId;

        /**
         * TCP端口
         */
        @Schema(description = "TCP端口")
        private int tcpPort;

        /**
         * 配置文件路径
         */
        @Schema(description = "配置文件路径")
        private String configFile;
    }

    /**
     * 内存指标
     */
    @Data
    @Schema(description = "内存指标")
    public static class MemoryMetrics {
        /**
         * 已使用内存（字节）
         */
        @Schema(description = "已使用内存（字节）")
        private long usedMemory;

        /**
         * 已使用内存（人类可读）
         */
        @Schema(description = "已使用内存（人类可读）")
        private String usedMemoryHuman;

        /**
         * RSS内存（字节）
         */
        @Schema(description = "RSS内存（字节）")
        private long usedMemoryRss;

        /**
         * 峰值内存（字节）
         */
        @Schema(description = "峰值内存（字节）")
        private long usedMemoryPeak;

        /**
         * 峰值内存（人类可读）
         */
        @Schema(description = "峰值内存（人类可读）")
        private String usedMemoryPeakHuman;

        /**
         * Lua脚本内存（字节）
         */
        @Schema(description = "Lua脚本内存（字节）")
        private long usedMemoryLua;

        /**
         * 内存碎片率
         */
        @Schema(description = "内存碎片率")
        private double memFragmentationRatio;

        /**
         * 最大内存（字节）
         */
        @Schema(description = "最大内存（字节）")
        private long maxMemory;

        /**
         * 最大内存策略
         */
        @Schema(description = "最大内存策略")
        private String maxMemoryPolicy;
    }

    /**
     * 连接指标
     */
    @Data
    @Schema(description = "连接指标")
    public static class ConnectionMetrics {
        /**
         * 当前连接数
         */
        @Schema(description = "当前连接数")
        private int connectedClients;

        /**
         * 最大连接数
         */
        @Schema(description = "最大连接数")
        private int maxClients;

        /**
         * 阻塞的客户端数
         */
        @Schema(description = "阻塞的客户端数")
        private int blockedClients;

        /**
         * 总连接数
         */
        @Schema(description = "总连接数")
        private long totalConnectionsReceived;

        /**
         * 拒绝的连接数
         */
        @Schema(description = "拒绝的连接数")
        private long rejectedConnections;
    }

    /**
     * 命令统计
     */
    @Data
    @Schema(description = "命令统计")
    public static class CommandStats {
        /**
         * 总命令数
         */
        @Schema(description = "总命令数")
        private long totalCommandsProcessed;

        /**
         * 每秒命令数
         */
        @Schema(description = "每秒命令数")
        private double instantaneousOpsPerSec;

        /**
         * 命令详细统计
         */
        @Schema(description = "命令详细统计")
        private Map<String, CommandStat> commands;

        @Data
        @Schema(description = "单个命令统计")
        public static class CommandStat {
            /**
             * 调用次数
             */
            @Schema(description = "调用次数")
            private long calls;

            /**
             * 总耗时（微秒）
             */
            @Schema(description = "总耗时（微秒）")
            private long usec;

            /**
             * 平均耗时（微秒）
             */
            @Schema(description = "平均耗时（微秒）")
            private double usecPerCall;
        }
    }

    /**
     * 键空间统计
     */
    @Data
    @Schema(description = "键空间统计")
    public static class KeyspaceStats {
        /**
         * 键数量
         */
        @Schema(description = "键数量")
        private long keys;

        /**
         * 过期键数量
         */
        @Schema(description = "过期键数量")
        private long expires;

        /**
         * 平均TTL
         */
        @Schema(description = "平均TTL")
        private long avgTtl;
    }

    /**
     * 性能指标
     */
    @Data
    @Schema(description = "性能指标")
    public static class PerformanceMetrics {
        /**
         * 键命中次数
         */
        @Schema(description = "键命中次数")
        private long keyspaceHits;

        /**
         * 键未命中次数
         */
        @Schema(description = "键未命中次数")
        private long keyspaceMisses;

        /**
         * 命中率（百分比）
         */
        @Schema(description = "命中率（百分比）")
        private double hitRate;

        /**
         * 过期键数量
         */
        @Schema(description = "过期键数量")
        private long expiredKeys;

        /**
         * 淘汰键数量
         */
        @Schema(description = "淘汰键数量")
        private long evictedKeys;

        /**
         * 网络输入字节数
         */
        @Schema(description = "网络输入字节数")
        private long totalNetInputBytes;

        /**
         * 网络输出字节数
         */
        @Schema(description = "网络输出字节数")
        private long totalNetOutputBytes;

        /**
         * 每秒网络输入字节数
         */
        @Schema(description = "每秒网络输入字节数")
        private double instantaneousInputKbps;

        /**
         * 每秒网络输出字节数
         */
        @Schema(description = "每秒网络输出字节数")
        private double instantaneousOutputKbps;
    }
}
