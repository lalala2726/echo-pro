package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.JvmMetricsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * JVM监控服务
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Slf4j
@Service
public class JvmMonitorService {

    private final RuntimeMXBean runtimeMXBean;
    private final MemoryMXBean memoryMXBean;
    private final List<GarbageCollectorMXBean> gcMXBeans;
    private final ThreadMXBean threadMXBean;
    private final ClassLoadingMXBean classLoadingMXBean;
    private final List<MemoryPoolMXBean> memoryPoolMXBeans;

    public JvmMonitorService() {
        this.runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
        this.gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        this.memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
    }

    /**
     * 获取JVM监控指标
     *
     * @return JVM监控指标
     */
    public JvmMetricsDTO getJvmMetrics() {
        JvmMetricsDTO metrics = new JvmMetricsDTO();
        metrics.setTimestamp(LocalDateTime.now());

        try {
            // 获取JVM基本信息
            metrics.setJvmInfo(getJvmInfo());

            // 获取内存指标
            metrics.setMemory(getMemoryMetrics());

            // 获取GC指标
            metrics.setGc(getGcMetrics());

            // 获取线程指标
            metrics.setThreads(getThreadMetrics());

            // 获取类加载指标
            metrics.setClassLoading(getClassLoadingMetrics());

        } catch (Exception e) {
            log.error("获取JVM监控指标失败", e);
        }

        return metrics;
    }

    /**
     * 获取JVM基本信息
     */
    private JvmMetricsDTO.JvmInfo getJvmInfo() {
        JvmMetricsDTO.JvmInfo jvmInfo = new JvmMetricsDTO.JvmInfo();

        jvmInfo.setName(runtimeMXBean.getVmName());
        jvmInfo.setVersion(runtimeMXBean.getVmVersion());
        jvmInfo.setVendor(runtimeMXBean.getVmVendor());
        jvmInfo.setJavaVersion(System.getProperty("java.version"));
        jvmInfo.setJavaHome(System.getProperty("java.home"));

        // JVM启动时间
        long startTime = runtimeMXBean.getStartTime();
        jvmInfo.setStartTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault()));

        // JVM运行时间
        jvmInfo.setUptime(runtimeMXBean.getUptime());

        // JVM参数
        jvmInfo.setInputArguments(runtimeMXBean.getInputArguments());

        return jvmInfo;
    }

    /**
     * 获取内存指标
     */
    private JvmMetricsDTO.MemoryMetrics getMemoryMetrics() {
        JvmMetricsDTO.MemoryMetrics memoryMetrics = new JvmMetricsDTO.MemoryMetrics();

        // 堆内存
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        memoryMetrics.setHeap(createMemoryPool(heapMemoryUsage));

        // 非堆内存
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        memoryMetrics.setNonHeap(createMemoryPool(nonHeapMemoryUsage));

        // 内存池详情
        Map<String, JvmMetricsDTO.MemoryMetrics.MemoryPool> memoryPools = new HashMap<>();
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            MemoryUsage usage = poolMXBean.getUsage();
            if (usage != null) {
                memoryPools.put(poolMXBean.getName(), createMemoryPool(usage));
            }
        }
        memoryMetrics.setMemoryPools(memoryPools);

        // 直接内存（通过反射获取）
        memoryMetrics.setDirectMemory(getDirectMemoryMetrics());

        return memoryMetrics;
    }

    /**
     * 创建内存池对象
     */
    private JvmMetricsDTO.MemoryMetrics.MemoryPool createMemoryPool(MemoryUsage usage) {
        JvmMetricsDTO.MemoryMetrics.MemoryPool pool = new JvmMetricsDTO.MemoryMetrics.MemoryPool();
        pool.setUsed(usage.getUsed());
        pool.setCommitted(usage.getCommitted());
        pool.setMax(usage.getMax());
        pool.setInit(usage.getInit());

        // 计算使用率
        if (usage.getMax() > 0) {
            pool.setUsage(Math.round((double) usage.getUsed() / usage.getMax() * 100 * 100.0) / 100.0);
        } else if (usage.getCommitted() > 0) {
            pool.setUsage(Math.round((double) usage.getUsed() / usage.getCommitted() * 100 * 100.0) / 100.0);
        } else {
            pool.setUsage(0.0);
        }

        return pool;
    }

    /**
     * 获取直接内存指标
     */
    private JvmMetricsDTO.MemoryMetrics.DirectMemory getDirectMemoryMetrics() {
        JvmMetricsDTO.MemoryMetrics.DirectMemory directMemory = new JvmMetricsDTO.MemoryMetrics.DirectMemory();

        try {
            // 通过反射获取直接内存信息
            Class<?> vmClass = Class.forName("sun.misc.VM");
            Object maxDirectMemory = vmClass.getMethod("maxDirectMemory").invoke(null);

            // 获取已使用的直接内存（这里使用一个近似值）
            long maxDirect = (Long) maxDirectMemory;
            long usedDirect = getUsedDirectMemory();

            directMemory.setMax(maxDirect);
            directMemory.setUsed(usedDirect);

            if (maxDirect > 0) {
                directMemory.setUsage(Math.round((double) usedDirect / maxDirect * 100 * 100.0) / 100.0);
            } else {
                directMemory.setUsage(0.0);
            }

        } catch (Exception e) {
            log.debug("无法获取直接内存信息", e);
            directMemory.setMax(0);
            directMemory.setUsed(0);
            directMemory.setUsage(0.0);
        }

        return directMemory;
    }

    /**
     * 获取已使用的直接内存（近似值）
     */
    private long getUsedDirectMemory() {
        try {
            Class<?> bitsClass = Class.forName("java.nio.Bits");
            Object reservedMemory = bitsClass.getDeclaredField("reservedMemory").get(null);
            return (Long) reservedMemory;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取GC指标
     */
    private List<JvmMetricsDTO.GcMetrics> getGcMetrics() {
        List<JvmMetricsDTO.GcMetrics> gcMetricsList = new ArrayList<>();

        for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
            JvmMetricsDTO.GcMetrics gcMetrics = new JvmMetricsDTO.GcMetrics();

            gcMetrics.setName(gcMXBean.getName());
            gcMetrics.setCollectionCount(gcMXBean.getCollectionCount());
            gcMetrics.setCollectionTime(gcMXBean.getCollectionTime());

            // 计算平均GC时间
            if (gcMetrics.getCollectionCount() > 0) {
                gcMetrics.setAverageCollectionTime(
                        Math.round((double) gcMetrics.getCollectionTime() / gcMetrics.getCollectionCount() * 100.0) / 100.0
                );
            } else {
                gcMetrics.setAverageCollectionTime(0.0);
            }

            // 内存池名称
            gcMetrics.setMemoryPoolNames(Arrays.asList(gcMXBean.getMemoryPoolNames()));

            gcMetricsList.add(gcMetrics);
        }

        return gcMetricsList;
    }

    /**
     * 获取线程指标
     */
    private JvmMetricsDTO.ThreadMetrics getThreadMetrics() {
        JvmMetricsDTO.ThreadMetrics threadMetrics = new JvmMetricsDTO.ThreadMetrics();

        threadMetrics.setThreadCount(threadMXBean.getThreadCount());
        threadMetrics.setDaemonThreadCount(threadMXBean.getDaemonThreadCount());
        threadMetrics.setPeakThreadCount(threadMXBean.getPeakThreadCount());
        threadMetrics.setTotalStartedThreadCount(threadMXBean.getTotalStartedThreadCount());

        // 死锁检测
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        threadMetrics.setDeadlockedThreadCount(deadlockedThreads != null ? deadlockedThreads.length : 0);

        // 线程状态统计
        Map<String, Integer> threadStates = new HashMap<>();
        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds());
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null) {
                String state = threadInfo.getThreadState().name();
                threadStates.put(state, threadStates.getOrDefault(state, 0) + 1);
            }
        }
        threadMetrics.setThreadStates(threadStates);

        return threadMetrics;
    }

    /**
     * 获取类加载指标
     */
    private JvmMetricsDTO.ClassLoadingMetrics getClassLoadingMetrics() {
        JvmMetricsDTO.ClassLoadingMetrics classLoadingMetrics = new JvmMetricsDTO.ClassLoadingMetrics();

        classLoadingMetrics.setLoadedClassCount(classLoadingMXBean.getLoadedClassCount());
        classLoadingMetrics.setTotalLoadedClassCount(classLoadingMXBean.getTotalLoadedClassCount());
        classLoadingMetrics.setUnloadedClassCount(classLoadingMXBean.getUnloadedClassCount());

        return classLoadingMetrics;
    }
}
