package cn.zhangchuangla.system.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * JVM监控指标DTO
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Data
@Schema(description = "JVM监控指标")
public class JvmMetricsDTO {

    /**
     * 采集时间
     */
    @Schema(description = "采集时间", type = "string", example = "2025-07-28T10:00:00")
    private LocalDateTime timestamp;

    /**
     * JVM基本信息
     */
    @Schema(description = "JVM基本信息", type = "object")
    private JvmInfo jvmInfo;

    /**
     * 内存指标
     */
    @Schema(description = "内存指标", type = "object")
    private MemoryMetrics memory;

    /**
     * 垃圾回收指标
     */
    @Schema(description = "垃圾回收指标", type = "array", example = "[]")
    private List<GcMetrics> gc;

    /**
     * 线程指标
     */
    @Schema(description = "线程指标", type = "object")
    private ThreadMetrics threads;

    /**
     * 类加载指标
     */
    @Schema(description = "类加载指标", type = "object")
    private ClassLoadingMetrics classLoading;

    /**
     * JVM基本信息
     */
    @Data
    @Schema(description = "JVM基本信息", type = "object")
    public static class JvmInfo {
        /**
         * JVM名称
         */
        @Schema(description = "JVM名称", type = "string", example = "OpenJDK 64-Bit Server VM")
        private String name;

        /**
         * JVM版本
         */
        @Schema(description = "JVM版本", type = "string", example = "17.0.1")
        private String version;

        /**
         * JVM供应商
         */
        @Schema(description = "JVM供应商", type = "string", example = "Oracle Corporation")
        private String vendor;

        /**
         * Java版本
         */
        @Schema(description = "Java版本", type = "string", example = "17.0.1")
        private String javaVersion;

        /**
         * Java主目录
         */
        @Schema(description = "Java主目录", type = "string", example = "/usr/lib/jvm/java-17-openjdk")
        private String javaHome;

        /**
         * JVM启动时间
         */
        @Schema(description = "JVM启动时间", type = "string", example = "2025-07-28T09:00:00")
        private LocalDateTime startTime;

        /**
         * JVM运行时间（毫秒）
         */
        @Schema(description = "JVM运行时间（毫秒）", type = "integer", example = "3600000")
        private long uptime;

        /**
         * JVM参数
         */
        @Schema(description = "JVM参数", type = "array", example = "[\"-Xmx1g\", \"-Xms512m\"]")
        private List<String> inputArguments;
    }

    /**
     * 内存指标
     */
    @Data
    @Schema(description = "内存指标", type = "object")
    public static class MemoryMetrics {
        /**
         * 堆内存
         */
        @Schema(description = "堆内存", type = "object")
        private MemoryPool heap;

        /**
         * 非堆内存
         */
        @Schema(description = "非堆内存", type = "object")
        private MemoryPool nonHeap;

        /**
         * 内存池详情
         */
        @Schema(description = "内存池详情", type = "object")
        private Map<String, MemoryPool> memoryPools;

        /**
         * 直接内存
         */
        @Schema(description = "直接内存", type = "object")
        private DirectMemory directMemory;

        @Data
        @Schema(description = "内存池", type = "object")
        public static class MemoryPool {
            /**
             * 已使用内存（字节）
             */
            @Schema(description = "已使用内存（字节）", type = "integer", example = "104857600")
            private long used;

            /**
             * 已提交内存（字节）
             */
            @Schema(description = "已提交内存（字节）", type = "integer", example = "134217728")
            private long committed;

            /**
             * 最大内存（字节）
             */
            @Schema(description = "最大内存（字节）", type = "integer", example = "2147483648")
            private long max;

            /**
             * 初始内存（字节）
             */
            @Schema(description = "初始内存（字节）", type = "integer", example = "134217728")
            private long init;

            /**
             * 使用率（百分比）
             */
            @Schema(description = "使用率（百分比）", type = "number", example = "4.88")
            private double usage;
        }

        @Data
        @Schema(description = "直接内存", type = "object")
        public static class DirectMemory {
            /**
             * 已使用直接内存（字节）
             */
            @Schema(description = "已使用直接内存（字节）", type = "integer", example = "16777216")
            private long used;

            /**
             * 最大直接内存（字节）
             */
            @Schema(description = "最大直接内存（字节）", type = "integer", example = "134217728")
            private long max;

            /**
             * 使用率（百分比）
             */
            @Schema(description = "使用率（百分比）", type = "number", example = "12.5")
            private double usage;
        }
    }

    /**
     * 垃圾回收指标
     */
    @Data
    @Schema(description = "垃圾回收指标", type = "object")
    public static class GcMetrics {
        /**
         * GC名称
         */
        @Schema(description = "GC名称", type = "string", example = "G1 Young Generation")
        private String name;

        /**
         * GC次数
         */
        @Schema(description = "GC次数", type = "integer", example = "10")
        private long collectionCount;

        /**
         * GC总时间（毫秒）
         */
        @Schema(description = "GC总时间（毫秒）", type = "integer", example = "500")
        private long collectionTime;

        /**
         * 平均GC时间（毫秒）
         */
        @Schema(description = "平均GC时间（毫秒）", type = "number", example = "50.0")
        private double averageCollectionTime;

        /**
         * 内存池名称
         */
        @Schema(description = "内存池名称", type = "array", example = "[\"G1 Eden Space\", \"G1 Survivor Space\"]")
        private List<String> memoryPoolNames;
    }

    /**
     * 线程指标
     */
    @Data
    @Schema(description = "线程指标", type = "object")
    public static class ThreadMetrics {
        /**
         * 当前线程数
         */
        @Schema(description = "当前线程数", type = "integer", example = "20")
        private int threadCount;

        /**
         * 守护线程数
         */
        @Schema(description = "守护线程数", type = "integer", example = "15")
        private int daemonThreadCount;

        /**
         * 峰值线程数
         */
        @Schema(description = "峰值线程数", type = "integer", example = "25")
        private int peakThreadCount;

        /**
         * 总启动线程数
         */
        @Schema(description = "总启动线程数", type = "integer", example = "100")
        private long totalStartedThreadCount;

        /**
         * 死锁线程数
         */
        @Schema(description = "死锁线程数", type = "integer", example = "0")
        private int deadlockedThreadCount;

        /**
         * 线程状态统计
         */
        @Schema(description = "线程状态统计", type = "object", example = "{\"RUNNABLE\": 10, \"BLOCKED\": 0}")
        private Map<String, Integer> threadStates;
    }

    /**
     * 类加载指标
     */
    @Data
    @Schema(description = "类加载指标", type = "object")
    public static class ClassLoadingMetrics {
        /**
         * 当前加载的类数量
         */
        @Schema(description = "当前加载的类数量", type = "integer", example = "5000")
        private int loadedClassCount;

        /**
         * 总加载的类数量
         */
        @Schema(description = "总加载的类数量", type = "integer", example = "10000")
        private long totalLoadedClassCount;

        /**
         * 卸载的类数量
         */
        @Schema(description = "卸载的类数量", type = "integer", example = "100")
        private long unloadedClassCount;
    }
}
