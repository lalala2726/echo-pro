package cn.zhangchuangla.infrastructure.model.entity;

import cn.zhangchuangla.infrastructure.model.entity.server.*;
import cn.zhangchuangla.infrastructure.model.entity.server.System;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务器信息实体类
 *
 * @author Chuang
 */
@Data
@Slf4j
public class Server {
    /**
     * CPU信息
     */
    private CPU cpu;

    /**
     * 内存信息
     */
    private Memory memory;

    /**
     * JVM信息
     */
    private JVM jvm;

    /**
     * 系统信息
     */
    private System system;

    /**
     * 磁盘信息
     */
    private List<Disk> disks;

    /**
     * 采集服务器信息
     */
    public void copyInfo() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();

        // 设置CPU信息
        try {
            this.cpu = getCpuInfo(hardware.getProcessor());
        } catch (InterruptedException e) {
            log.warn("获取CPU信息失败:", e);
        }

        // 设置内存信息
        this.memory = getMemoryInfo(hardware.getMemory());

        // 设置JVM信息
        this.jvm = getJvmInfo();

        // 设置系统信息
        this.system = getSystemInfo(os);

        // 设置磁盘信息
        this.disks = getDiskInfo(os);
    }

    /**
     * 获取CPU信息
     */
    private CPU getCpuInfo(CentralProcessor processor) throws InterruptedException {
        CPU cpu = new CPU();
        // 获取CPU名称
        cpu.setCpuName(processor.getProcessorIdentifier().getName());
        // 获取CPU核数
        cpu.setCpuCore(processor.getLogicalProcessorCount() + "");
        // 获取物理CPU数量
        cpu.setPhysicalPackageCount(processor.getPhysicalPackageCount() + "");
        // 获取物理核心数
        cpu.setPhysicalProcessorCount(processor.getPhysicalProcessorCount() + "");
        // 获取CPU厂商
        cpu.setCpuVendor(processor.getProcessorIdentifier().getVendor());
        // 获取CPU型号
        cpu.setCpuModel(processor.getProcessorIdentifier().getModel());
        // 获取CPU系列
        cpu.setCpuFamily(processor.getProcessorIdentifier().getFamily());
        // 获取CPU步进
        cpu.setCpuStepping(processor.getProcessorIdentifier().getStepping());
        // 获取CPU标识符
        cpu.setCpuIdentifier(processor.getProcessorIdentifier().getIdentifier());
        // 获取CPU频率
        cpu.setCpuFrequency(FormatUtil.formatHertz(processor.getProcessorIdentifier().getVendorFreq()));
        // 获取最大频率
        cpu.setMaxFreq(FormatUtil.formatHertz(processor.getMaxFreq()));

        // 获取CPU使用率（需要两次采样计算）
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 获取处理器的前一次滴答计数
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        Thread.sleep(500);

        // 计算系统CPU使用率
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        cpu.setCpuUsage(String.format("%.2f%%", cpuLoad));

        // 每个逻辑处理器的利用率
        long[][] procTicks = processor.getProcessorCpuLoadTicks();
        List<String> coreUsages = new ArrayList<>();

        // 计算每个处理器核心的使用率
        for (int i = 0; i < processor.getLogicalProcessorCount(); i++) {
            double coreLoad = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks)[i] * 100;
            coreUsages.add(String.format("%.2f%%", coreLoad));
        }
        cpu.setCoreUsages(coreUsages);

        return cpu;
    }

    /**
     * 获取内存信息
     */
    private Memory getMemoryInfo(GlobalMemory memory) {
        Memory mem = new Memory();
        // 转换为GB并保留两位小数
        double totalGB = memory.getTotal() / 1024.0 / 1024.0 / 1024.0;
        double usedGB = (memory.getTotal() - memory.getAvailable()) / 1024.0 / 1024.0 / 1024.0;
        double freeGB = memory.getAvailable() / 1024.0 / 1024.0 / 1024.0;

        mem.setTotal(Math.round(totalGB * 100) / 100.0);
        mem.setUsed(Math.round(usedGB * 100) / 100.0);
        mem.setFree(Math.round(freeGB * 100) / 100.0);

        // 计算内存使用率
        double usageRate = usedGB / totalGB * 100;
        mem.setUsage(String.format("%.2f%%", usageRate));

        // 添加交换内存信息
        double swapTotalGB = memory.getVirtualMemory().getSwapTotal() / 1024.0 / 1024.0 / 1024.0;
        double swapUsedGB = memory.getVirtualMemory().getSwapUsed() / 1024.0 / 1024.0 / 1024.0;

        mem.setSwapTotal(Math.round(swapTotalGB * 100) / 100.0);
        mem.setSwapUsed(Math.round(swapUsedGB * 100) / 100.0);
        mem.setSwapFree(Math.round((swapTotalGB - swapUsedGB) * 100) / 100.0);

        // 计算交换内存使用率
        double swapUsageRate = swapTotalGB > 0 ? (swapUsedGB / swapTotalGB * 100) : 0;
        mem.setSwapUsage(String.format("%.2f%%", swapUsageRate));

        return mem;
    }

    /**
     * 获取JVM信息
     */
    private JVM getJvmInfo() {
        JVM jvm = new JVM();
        // 使用完全限定名调用java.lang.System
        jvm.setVersion(java.lang.System.getProperty("java.version"));
        jvm.setName(java.lang.System.getProperty("java.vm.name"));
        jvm.setHome(java.lang.System.getProperty("java.home"));

        // JVM启动时间
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        jvm.setStartTime(new Date(startTime).toString());

        // JVM运行时间
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        jvm.setRunTime(formatUptime(uptime));

        // JVM内存信息
        Runtime runtime = Runtime.getRuntime();
        jvm.setMaxMemory(FormatUtil.formatBytes(runtime.maxMemory()));
        jvm.setTotalMemory(FormatUtil.formatBytes(runtime.totalMemory()));
        jvm.setFreeMemory(FormatUtil.formatBytes(runtime.freeMemory()));
        jvm.setUsedMemory(FormatUtil.formatBytes(runtime.totalMemory() - runtime.freeMemory()));

        // JVM使用率
        double usage = ((double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory()) * 100;
        jvm.setUsage(String.format("%.2f%%", usage));

        return jvm;
    }

    /**
     * 获取系统信息
     */
    private System getSystemInfo(OperatingSystem os) {
        System system = new System();

        try {
            InetAddress address = InetAddress.getLocalHost();
            system.setComputerName(address.getHostName());
            system.setComputerIp(address.getHostAddress());
        } catch (UnknownHostException e) {
            system.setComputerName("未知");
            system.setComputerIp("未知");
        }

        // 使用完全限定名调用java.lang.System
        system.setUserDir(java.lang.System.getProperty("user.dir"));
        system.setOsName(os.getFamily() + " " + os.getVersionInfo().getVersion());
        system.setOsArch(java.lang.System.getProperty("os.arch"));

        // 添加更多系统信息
        system.setOsVersion(os.getVersionInfo().toString());
        system.setOsManufacturer(os.getManufacturer());
        system.setOsBit(os.getBitness() + "位");
        system.setProcessCount(os.getProcessCount());
        system.setThreadCount(os.getThreadCount());

        // 设置系统启动时间
        system.setBootTime(new Date(os.getSystemBootTime() * 1000L).toString());

        // 设置系统运行时间
        long uptimeSeconds = os.getSystemUptime();
        system.setUptime(formatUptime(uptimeSeconds * 1000));

        return system;
    }

    /**
     * 获取磁盘信息
     */
    private List<Disk> getDiskInfo(OperatingSystem os) {
        List<Disk> diskList = new ArrayList<>();
        FileSystem fileSystem = os.getFileSystem();
        for (OSFileStore store : fileSystem.getFileStores()) {
            Disk disk = new Disk();
            disk.setName(store.getName());
            disk.setTotal(FormatUtil.formatBytes(store.getTotalSpace()));
            disk.setFree(FormatUtil.formatBytes(store.getUsableSpace()));
            disk.setUsed(FormatUtil.formatBytes(store.getTotalSpace() - store.getUsableSpace()));

            // 计算使用率
            double usage = ((double) (store.getTotalSpace() - store.getUsableSpace()) / store.getTotalSpace()) * 100;
            disk.setUsage(String.format("%.2f%%", usage));

            diskList.add(disk);
        }

        return diskList;
    }

    /**
     * 格式化运行时间
     */
    private String formatUptime(long uptime) {
        long days = uptime / (1000 * 60 * 60 * 24);
        long hours = (uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (uptime % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (uptime % (1000 * 60)) / 1000;

        return String.format("%d天%d小时%d分钟%d秒", days, hours, minutes, seconds);
    }
}
