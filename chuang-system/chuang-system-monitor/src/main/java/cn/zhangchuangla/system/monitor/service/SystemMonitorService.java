package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.SystemMetricsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统监控服务
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Slf4j
@Service
public class SystemMonitorService {

    private final HardwareAbstractionLayer hardware;
    private final OperatingSystem os;

    public SystemMonitorService() {
        SystemInfo systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.os = systemInfo.getOperatingSystem();
    }

    /**
     * 获取系统监控指标
     *
     * @return 系统监控指标
     */
    public SystemMetricsDTO getSystemMetrics() {
        SystemMetricsDTO metrics = new SystemMetricsDTO();
        metrics.setTimestamp(LocalDateTime.now());

        try {
            // 获取CPU指标
            metrics.setCpu(getCpuMetrics());

            // 获取内存指标
            metrics.setMemory(getMemoryMetrics());

            // 获取磁盘指标
            metrics.setDisks(getDiskMetrics());

            // 获取系统信息
            metrics.setSystemInfo(getSystemInfo());

        } catch (Exception e) {
            log.error("获取系统监控指标失败", e);
        }

        return metrics;
    }

    /**
     * 获取CPU指标
     */
    private SystemMetricsDTO.CpuMetrics getCpuMetrics() throws InterruptedException {
        CentralProcessor processor = hardware.getProcessor();
        SystemMetricsDTO.CpuMetrics cpuMetrics = new SystemMetricsDTO.CpuMetrics();

        // 基本信息
        cpuMetrics.setName(processor.getProcessorIdentifier().getName());
        cpuMetrics.setLogicalProcessorCount(processor.getLogicalProcessorCount());
        cpuMetrics.setPhysicalProcessorCount(processor.getPhysicalProcessorCount());
        cpuMetrics.setPhysicalPackageCount(processor.getPhysicalPackageCount());
        cpuMetrics.setFrequency(processor.getProcessorIdentifier().getVendorFreq());
        cpuMetrics.setMaxFrequency(processor.getMaxFreq());

        // CPU使用率计算（需要两次采样）
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();

        // 等待500ms
        Thread.sleep(500);

        // 计算系统CPU使用率
        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        cpuMetrics.setUsage(Math.round(cpuUsage * 100.0) / 100.0);

        // 计算各核心使用率
        double[] coreLoads = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        List<Double> coreUsages = new ArrayList<>();
        for (double coreLoad : coreLoads) {
            coreUsages.add(Math.round(coreLoad * 100 * 100.0) / 100.0);
        }
        cpuMetrics.setCoreUsages(coreUsages);

        // 系统负载平均值
        double[] loadAverage = processor.getSystemLoadAverage(3);
        if (loadAverage.length >= 3) {
            cpuMetrics.setLoadAverage1m(loadAverage[0]);
            cpuMetrics.setLoadAverage5m(loadAverage[1]);
            cpuMetrics.setLoadAverage15m(loadAverage[2]);
        }

        return cpuMetrics;
    }

    /**
     * 获取内存指标
     */
    private SystemMetricsDTO.MemoryMetrics getMemoryMetrics() {
        GlobalMemory memory = hardware.getMemory();
        SystemMetricsDTO.MemoryMetrics memoryMetrics = new SystemMetricsDTO.MemoryMetrics();

        // 物理内存
        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        memoryMetrics.setTotal(total);
        memoryMetrics.setUsed(used);
        memoryMetrics.setAvailable(available);
        memoryMetrics.setUsage(Math.round((double) used / total * 100 * 100.0) / 100.0);

        // 交换内存
        VirtualMemory virtualMemory = memory.getVirtualMemory();
        long swapTotal = virtualMemory.getSwapTotal();
        long swapUsed = virtualMemory.getSwapUsed();

        memoryMetrics.setSwapTotal(swapTotal);
        memoryMetrics.setSwapUsed(swapUsed);
        if (swapTotal > 0) {
            memoryMetrics.setSwapUsage(Math.round((double) swapUsed / swapTotal * 100 * 100.0) / 100.0);
        } else {
            memoryMetrics.setSwapUsage(0.0);
        }

        return memoryMetrics;
    }

    /**
     * 获取磁盘指标
     */
    private List<SystemMetricsDTO.DiskMetrics> getDiskMetrics() {
        List<SystemMetricsDTO.DiskMetrics> diskMetricsList = new ArrayList<>();
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        List<HWDiskStore> diskStores = hardware.getDiskStores();

        for (OSFileStore store : fileStores) {
            SystemMetricsDTO.DiskMetrics diskMetrics = new SystemMetricsDTO.DiskMetrics();

            diskMetrics.setName(store.getName());
            diskMetrics.setMountPoint(store.getMount());
            diskMetrics.setFileSystem(store.getType());

            long total = store.getTotalSpace();
            long usable = store.getUsableSpace();
            long used = total - usable;

            diskMetrics.setTotal(total);
            diskMetrics.setUsed(used);
            diskMetrics.setAvailable(usable);

            if (total > 0) {
                diskMetrics.setUsage(Math.round((double) used / total * 100 * 100.0) / 100.0);
            } else {
                diskMetrics.setUsage(0.0);
            }

            // 尝试获取磁盘IO信息
            for (HWDiskStore diskStore : diskStores) {
                if (store.getName().contains(diskStore.getName()) ||
                        diskStore.getName().contains(store.getName())) {
                    diskMetrics.setReadRate(diskStore.getReadBytes());
                    diskMetrics.setWriteRate(diskStore.getWriteBytes());
                    break;
                }
            }

            diskMetricsList.add(diskMetrics);
        }

        return diskMetricsList;
    }

    /**
     * 获取系统信息
     */
    private SystemMetricsDTO.SystemInfo getSystemInfo() {
        SystemMetricsDTO.SystemInfo systemInfo = new SystemMetricsDTO.SystemInfo();

        systemInfo.setOsName(os.getFamily());
        systemInfo.setOsVersion(os.getVersionInfo().getVersion());
        systemInfo.setOsArch(System.getProperty("os.arch"));

        // 系统启动时间
        long bootTime = os.getSystemBootTime();
        systemInfo.setBootTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(bootTime), ZoneId.systemDefault()));

        // 系统运行时间
        long uptime = System.currentTimeMillis() / 1000 - bootTime;
        systemInfo.setUptime(uptime);

        // 进程和线程数量
        systemInfo.setProcessCount(os.getProcessCount());
        systemInfo.setThreadCount(os.getThreadCount());

        return systemInfo;
    }
}
