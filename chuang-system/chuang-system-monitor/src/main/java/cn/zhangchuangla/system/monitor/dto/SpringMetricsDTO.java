package cn.zhangchuangla.system.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Spring监控指标DTO
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Data
@Schema(description = "Spring监控指标")
public class SpringMetricsDTO {

    /**
     * 采集时间
     */
    @Schema(description = "采集时间")
    private LocalDateTime timestamp;

    /**
     * 应用信息
     */
    @Schema(description = "应用信息")
    private ApplicationInfo application;


    /**
     * 数据源指标
     */
    @Schema(description = "数据源指标")
    private DataSourceMetrics dataSource;

    /**
     * 线程池指标
     */
    @Schema(description = "线程池指标")
    private Map<String, ThreadPoolMetrics> threadPools;

    /**
     * 缓存指标
     */
    @Schema(description = "缓存指标")
    private Map<String, CacheMetrics> caches;

    /**
     * 应用信息
     */
    @Data
    @Schema(description = "应用信息")
    public static class ApplicationInfo {
        /**
         * 应用名称
         */
        @Schema(description = "应用名称")
        private String name;

        /**
         * 应用版本
         */
        @Schema(description = "应用版本")
        private String version;

        /**
         * Spring Boot版本
         */
        @Schema(description = "Spring Boot版本")
        private String springBootVersion;

        /**
         * Spring版本
         */
        @Schema(description = "Spring版本")
        private String springVersion;

        /**
         * 启动时间
         */
        @Schema(description = "启动时间")
        private LocalDateTime startTime;

        /**
         * 运行时间（毫秒）
         */
        @Schema(description = "运行时间（毫秒）")
        private long uptime;

        /**
         * 活跃配置文件
         */
        @Schema(description = "活跃配置文件")
        private String[] activeProfiles;
    }



    /**
     * 数据源指标
     */
    @Data
    @Schema(description = "数据源指标")
    public static class DataSourceMetrics {
        /**
         * 活跃连接数
         */
        @Schema(description = "活跃连接数")
        private int active;

        /**
         * 最大连接数
         */
        @Schema(description = "最大连接数")
        private int max;

        /**
         * 最小连接数
         */
        @Schema(description = "最小连接数")
        private int min;

        /**
         * 空闲连接数
         */
        @Schema(description = "空闲连接数")
        private int idle;

        /**
         * 连接池使用率（百分比）
         */
        @Schema(description = "连接池使用率（百分比）")
        private double usage;

        /**
         * 等待连接的线程数
         */
        @Schema(description = "等待连接的线程数")
        private int waitingThreads;
    }

    /**
     * 线程池指标
     */
    @Data
    @Schema(description = "线程池指标")
    public static class ThreadPoolMetrics {
        /**
         * 核心线程数
         */
        @Schema(description = "核心线程数")
        private int corePoolSize;

        /**
         * 最大线程数
         */
        @Schema(description = "最大线程数")
        private int maximumPoolSize;

        /**
         * 当前线程数
         */
        @Schema(description = "当前线程数")
        private int poolSize;

        /**
         * 活跃线程数
         */
        @Schema(description = "活跃线程数")
        private int activeCount;

        /**
         * 队列大小
         */
        @Schema(description = "队列大小")
        private int queueSize;

        /**
         * 队列剩余容量
         */
        @Schema(description = "队列剩余容量")
        private int queueRemainingCapacity;

        /**
         * 已完成任务数
         */
        @Schema(description = "已完成任务数")
        private long completedTaskCount;

        /**
         * 总任务数
         */
        @Schema(description = "总任务数")
        private long taskCount;

        /**
         * 线程池使用率（百分比）
         */
        @Schema(description = "线程池使用率（百分比）")
        private double usage;
    }

    /**
     * 缓存指标
     */
    @Data
    @Schema(description = "缓存指标")
    public static class CacheMetrics {
        /**
         * 缓存名称
         */
        @Schema(description = "缓存名称")
        private String name;

        /**
         * 缓存大小
         */
        @Schema(description = "缓存大小")
        private long size;

        /**
         * 命中次数
         */
        @Schema(description = "命中次数")
        private long hitCount;

        /**
         * 未命中次数
         */
        @Schema(description = "未命中次数")
        private long missCount;

        /**
         * 命中率（百分比）
         */
        @Schema(description = "命中率（百分比）")
        private double hitRate;

        /**
         * 加载次数
         */
        @Schema(description = "加载次数")
        private long loadCount;

        /**
         * 平均加载时间（毫秒）
         */
        @Schema(description = "平均加载时间（毫秒）")
        private double averageLoadTime;

        /**
         * 淘汰次数
         */
        @Schema(description = "淘汰次数")
        private long evictionCount;
    }
}
