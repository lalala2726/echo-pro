package cn.zhangchuangla.system.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统监控指标DTO
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Data
@Schema(description = "系统监控指标")
public class SystemMetricsDTO {

    /**
     * 采集时间
     */
    @Schema(description = "采集时间")
    private LocalDateTime timestamp;

    /**
     * CPU信息
     */
    @Schema(description = "CPU信息")
    private CpuMetrics cpu;

    /**
     * 内存信息
     */
    @Schema(description = "内存信息")
    private MemoryMetrics memory;

    /**
     * 磁盘信息
     */
    @Schema(description = "磁盘信息")
    private List<DiskMetrics> disks;

    /**
     * 系统信息
     */
    @Schema(description = "系统信息")
    private SystemInfo systemInfo;

    /**
     * CPU指标
     */
    @Data
    @Schema(description = "CPU指标")
    public static class CpuMetrics {
        /**
         * CPU名称
         */
        @Schema(description = "CPU名称")
        private String name;

        /**
         * CPU使用率（百分比）
         */
        @Schema(description = "CPU使用率（百分比）")
        private double usage;

        /**
         * 逻辑处理器数量
         */
        @Schema(description = "逻辑处理器数量")
        private int logicalProcessorCount;

        /**
         * 物理处理器数量
         */
        @Schema(description = "物理处理器数量")
        private int physicalProcessorCount;

        /**
         * 物理包数量
         */
        @Schema(description = "物理包数量")
        private int physicalPackageCount;

        /**
         * CPU频率（Hz）
         */
        @Schema(description = "CPU频率（Hz）")
        private long frequency;

        /**
         * 最大频率（Hz）
         */
        @Schema(description = "最大频率（Hz）")
        private long maxFrequency;

        /**
         * 各核心使用率
         */
        @Schema(description = "各核心使用率")
        private List<Double> coreUsages;

        /**
         * 系统负载平均值（1分钟）
         */
        @Schema(description = "系统负载平均值（1分钟）")
        private double loadAverage1m;

        /**
         * 系统负载平均值（5分钟）
         */
        @Schema(description = "系统负载平均值（5分钟）")
        private double loadAverage5m;

        /**
         * 系统负载平均值（15分钟）
         */
        @Schema(description = "系统负载平均值（15分钟）")
        private double loadAverage15m;
    }

    /**
     * 内存指标
     */
    @Data
    @Schema(description = "内存指标")
    public static class MemoryMetrics {
        /**
         * 总内存（字节）
         */
        @Schema(description = "总内存（字节）")
        private long total;

        /**
         * 已用内存（字节）
         */
        @Schema(description = "已用内存（字节）")
        private long used;

        /**
         * 可用内存（字节）
         */
        @Schema(description = "可用内存（字节）")
        private long available;

        /**
         * 内存使用率（百分比）
         */
        @Schema(description = "内存使用率（百分比）")
        private double usage;

        /**
         * 交换内存总量（字节）
         */
        @Schema(description = "交换内存总量（字节）")
        private long swapTotal;

        /**
         * 已用交换内存（字节）
         */
        @Schema(description = "已用交换内存（字节）")
        private long swapUsed;

        /**
         * 交换内存使用率（百分比）
         */
        @Schema(description = "交换内存使用率（百分比）")
        private double swapUsage;
    }

    /**
     * 磁盘指标
     */
    @Data
    @Schema(description = "磁盘指标")
    public static class DiskMetrics {
        /**
         * 磁盘名称
         */
        @Schema(description = "磁盘名称")
        private String name;

        /**
         * 挂载点
         */
        @Schema(description = "挂载点")
        private String mountPoint;

        /**
         * 文件系统类型
         */
        @Schema(description = "文件系统类型")
        private String fileSystem;

        /**
         * 总空间（字节）
         */
        @Schema(description = "总空间（字节）")
        private long total;

        /**
         * 已用空间（字节）
         */
        @Schema(description = "已用空间（字节）")
        private long used;

        /**
         * 可用空间（字节）
         */
        @Schema(description = "可用空间（字节）")
        private long available;

        /**
         * 使用率（百分比）
         */
        @Schema(description = "使用率（百分比）")
        private double usage;

        /**
         * 读取速度（字节/秒）
         */
        @Schema(description = "读取速度（字节/秒）")
        private long readRate;

        /**
         * 写入速度（字节/秒）
         */
        @Schema(description = "写入速度（字节/秒）")
        private long writeRate;
    }

    /**
     * 系统信息
     */
    @Data
    @Schema(description = "系统信息")
    public static class SystemInfo {
        /**
         * 操作系统名称
         */
        @Schema(description = "操作系统名称")
        private String osName;

        /**
         * 操作系统版本
         */
        @Schema(description = "操作系统版本")
        private String osVersion;

        /**
         * 操作系统架构
         */
        @Schema(description = "操作系统架构")
        private String osArch;

        /**
         * 系统启动时间
         */
        @Schema(description = "系统启动时间")
        private LocalDateTime bootTime;

        /**
         * 系统运行时间（秒）
         */
        @Schema(description = "系统运行时间（秒）")
        private long uptime;

        /**
         * 进程数量
         */
        @Schema(description = "进程数量")
        private int processCount;

        /**
         * 线程数量
         */
        @Schema(description = "线程数量")
        private int threadCount;
    }
}
