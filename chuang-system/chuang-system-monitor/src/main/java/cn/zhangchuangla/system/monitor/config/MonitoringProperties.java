package cn.zhangchuangla.system.monitor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 监控配置属性
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "monitoring")
public class MonitoringProperties {

    /**
     * 是否启用监控
     */
    private boolean enabled = true;

    /**
     * 数据收集配置
     */
    private DataCollection dataCollection = new DataCollection();

    /**
     * 服务器监控配置
     */
    private Server server = new Server();

    /**
     * JVM监控配置
     */
    private Jvm jvm = new Jvm();

    /**
     * Redis监控配置
     */
    private Redis redis = new Redis();

    /**
     * Spring监控配置
     */
    private Spring spring = new Spring();

    /**
     * 数据收集配置
     */
    @Data
    public static class DataCollection {
        /**
         * 是否启用历史数据收集
         */
        private boolean enableHistoricalData = true;

        /**
         * 历史数据保留天数
         */
        private int retentionDays = 7;

        /**
         * 数据收集批次大小
         */
        private int batchSize = 100;
    }

    /**
     * 服务器监控配置
     */
    @Data
    public static class Server {
        /**
         * 是否启用服务器监控
         */
        private boolean enabled = true;

        /**
         * 数据收集间隔（秒）
         */
        private int collectionInterval = 30;

        /**
         * CPU监控配置
         */
        private Cpu cpu = new Cpu();

        /**
         * 内存监控配置
         */
        private Memory memory = new Memory();

        /**
         * 磁盘监控配置
         */
        private Disk disk = new Disk();

        @Data
        public static class Cpu {
            /**
             * 是否启用CPU监控
             */
            private boolean enabled = true;

            /**
             * CPU使用率告警阈值（百分比）
             */
            private double alertThreshold = 80.0;
        }

        @Data
        public static class Memory {
            /**
             * 是否启用内存监控
             */
            private boolean enabled = true;

            /**
             * 内存使用率告警阈值（百分比）
             */
            private double alertThreshold = 85.0;
        }

        @Data
        public static class Disk {
            /**
             * 是否启用磁盘监控
             */
            private boolean enabled = true;

            /**
             * 磁盘使用率告警阈值（百分比）
             */
            private double alertThreshold = 90.0;
        }
    }

    /**
     * JVM监控配置
     */
    @Data
    public static class Jvm {
        /**
         * 是否启用JVM监控
         */
        private boolean enabled = true;

        /**
         * 数据收集间隔（秒）
         */
        private int collectionInterval = 30;

        /**
         * 堆内存使用率告警阈值（百分比）
         */
        private double heapMemoryAlertThreshold = 85.0;

        /**
         * 非堆内存使用率告警阈值（百分比）
         */
        private double nonHeapMemoryAlertThreshold = 85.0;

        /**
         * GC时间告警阈值（毫秒）
         */
        private long gcTimeAlertThreshold = 1000;
    }

    /**
     * Redis监控配置
     */
    @Data
    public static class Redis {
        /**
         * 是否启用Redis监控
         */
        private boolean enabled = true;

        /**
         * 数据收集间隔（秒）
         */
        private int collectionInterval = 30;

        /**
         * 内存使用率告警阈值（百分比）
         */
        private double memoryAlertThreshold = 80.0;

        /**
         * 连接数告警阈值
         */
        private int connectionAlertThreshold = 1000;

        /**
         * 命令执行时间告警阈值（毫秒）
         */
        private long commandTimeAlertThreshold = 100;
    }

    /**
     * Spring监控配置
     */
    @Data
    public static class Spring {
        /**
         * 是否启用Spring监控
         */
        private boolean enabled = true;

        /**
         * 数据收集间隔（秒）
         */
        private int collectionInterval = 30;

        /**
         * HTTP请求响应时间告警阈值（毫秒）
         */
        private long httpResponseTimeAlertThreshold = 2000;

        /**
         * 线程池使用率告警阈值（百分比）
         */
        private double threadPoolAlertThreshold = 80.0;
    }
}
