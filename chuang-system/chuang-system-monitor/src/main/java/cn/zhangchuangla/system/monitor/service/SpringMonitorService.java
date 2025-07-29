package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.SpringMetricsDTO;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring监控服务
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Slf4j
@Service
public class SpringMonitorService {

    private final Environment environment;
    private final MeterRegistry meterRegistry;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    public SpringMonitorService(Environment environment,
                                MeterRegistry meterRegistry) {
        this.environment = environment;
        this.meterRegistry = meterRegistry;
    }

    /**
     * 获取Spring监控指标
     *
     * @return Spring监控指标
     */
    public SpringMetricsDTO getSpringMetrics() {
        SpringMetricsDTO metrics = new SpringMetricsDTO();
        metrics.setTimestamp(LocalDateTime.now());
        // 获取应用信息
        metrics.setApplication(getApplicationInfo());
        // 获取数据源指标
        metrics.setDataSource(getDataSourceMetrics());
        // 获取线程池指标
        metrics.setThreadPools(getThreadPoolMetrics());
        // 获取缓存指标
        metrics.setCaches(getCacheMetrics());
        return metrics;
    }

    /**
     * 获取应用信息
     */
    private SpringMetricsDTO.ApplicationInfo getApplicationInfo() {
        SpringMetricsDTO.ApplicationInfo appInfo = new SpringMetricsDTO.ApplicationInfo();

        // 应用名称和版本
        appInfo.setName(environment.getProperty("spring.application.name", "Unknown"));
        if (buildProperties != null) {
            appInfo.setVersion(buildProperties.getVersion());
        }

        // Spring版本信息
        appInfo.setSpringBootVersion(environment.getProperty("spring.boot.version"));
        appInfo.setSpringVersion(environment.getProperty("spring.version"));

        // 启动时间
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        appInfo.setStartTime(LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(startTime), ZoneId.systemDefault()));

        // 运行时间
        appInfo.setUptime(ManagementFactory.getRuntimeMXBean().getUptime());

        // 活跃配置文件
        appInfo.setActiveProfiles(environment.getActiveProfiles());

        return appInfo;
    }


    /**
     * 获取数据源指标
     */
    private SpringMetricsDTO.DataSourceMetrics getDataSourceMetrics() {
        SpringMetricsDTO.DataSourceMetrics dataSourceMetrics = new SpringMetricsDTO.DataSourceMetrics();

        try {
            // 尝试从Micrometer获取数据源指标
            io.micrometer.core.instrument.Gauge activeGauge = meterRegistry.find("hikaricp.connections.active").gauge();
            if (activeGauge != null) {
                dataSourceMetrics.setActive((int) activeGauge.value());
            }

            io.micrometer.core.instrument.Gauge maxGauge = meterRegistry.find("hikaricp.connections.max").gauge();
            if (maxGauge != null) {
                dataSourceMetrics.setMax((int) maxGauge.value());
            }

            io.micrometer.core.instrument.Gauge minGauge = meterRegistry.find("hikaricp.connections.min").gauge();
            if (minGauge != null) {
                dataSourceMetrics.setMin((int) minGauge.value());
            }

            io.micrometer.core.instrument.Gauge idleGauge = meterRegistry.find("hikaricp.connections.idle").gauge();
            if (idleGauge != null) {
                dataSourceMetrics.setIdle((int) idleGauge.value());
            }

            io.micrometer.core.instrument.Gauge pendingGauge = meterRegistry.find("hikaricp.connections.pending").gauge();
            if (pendingGauge != null) {
                dataSourceMetrics.setWaitingThreads((int) pendingGauge.value());
            }

            // 计算使用率
            if (dataSourceMetrics.getMax() > 0) {
                double usage = (double) dataSourceMetrics.getActive() / dataSourceMetrics.getMax() * 100;
                dataSourceMetrics.setUsage(Math.round(usage * 100.0) / 100.0);
            }

        } catch (Exception e) {
            log.debug("获取数据源指标失败", e);
        }

        return dataSourceMetrics;
    }

    /**
     * 获取线程池指标
     */
    private Map<String, SpringMetricsDTO.ThreadPoolMetrics> getThreadPoolMetrics() {
        Map<String, SpringMetricsDTO.ThreadPoolMetrics> threadPoolMetrics = new HashMap<>();

        try {
            // 从Micrometer获取线程池指标
            meterRegistry.find("executor.pool.size")
                    .gauges()
                    .forEach(gauge -> {
                        String poolName = gauge.getId().getTag("name");
                        if (poolName != null) {
                            SpringMetricsDTO.ThreadPoolMetrics metrics =
                                    threadPoolMetrics.computeIfAbsent(poolName, k -> new SpringMetricsDTO.ThreadPoolMetrics());
                            metrics.setPoolSize((int) gauge.value());
                        }
                    });

            meterRegistry.find("executor.active")
                    .gauges()
                    .forEach(gauge -> {
                        String poolName = gauge.getId().getTag("name");
                        if (poolName != null) {
                            SpringMetricsDTO.ThreadPoolMetrics metrics =
                                    threadPoolMetrics.computeIfAbsent(poolName, k -> new SpringMetricsDTO.ThreadPoolMetrics());
                            metrics.setActiveCount((int) gauge.value());
                        }
                    });

            meterRegistry.find("executor.queue.remaining")
                    .gauges()
                    .forEach(gauge -> {
                        String poolName = gauge.getId().getTag("name");
                        if (poolName != null) {
                            SpringMetricsDTO.ThreadPoolMetrics metrics =
                                    threadPoolMetrics.computeIfAbsent(poolName, k -> new SpringMetricsDTO.ThreadPoolMetrics());
                            metrics.setQueueRemainingCapacity((int) gauge.value());
                        }
                    });

            meterRegistry.find("executor.completed")
                    .counters()
                    .forEach(counter -> {
                        String poolName = counter.getId().getTag("name");
                        if (poolName != null) {
                            SpringMetricsDTO.ThreadPoolMetrics metrics =
                                    threadPoolMetrics.computeIfAbsent(poolName, k -> new SpringMetricsDTO.ThreadPoolMetrics());
                            metrics.setCompletedTaskCount((long) counter.count());
                        }
                    });

            // 计算使用率
            threadPoolMetrics.values().forEach(metrics -> {
                if (metrics.getMaximumPoolSize() > 0) {
                    double usage = (double) metrics.getActiveCount() / metrics.getMaximumPoolSize() * 100;
                    metrics.setUsage(Math.round(usage * 100.0) / 100.0);
                }
            });

        } catch (Exception e) {
            log.debug("获取线程池指标失败", e);
        }

        return threadPoolMetrics;
    }

    /**
     * 获取缓存指标
     */
    private Map<String, SpringMetricsDTO.CacheMetrics> getCacheMetrics() {
        Map<String, SpringMetricsDTO.CacheMetrics> cacheMetrics = new HashMap<>();

        try {
            // 从Micrometer获取缓存指标
            meterRegistry.find("cache.gets")
                    .counters()
                    .forEach(counter -> {
                        String cacheName = counter.getId().getTag("cache");
                        String result = counter.getId().getTag("result");
                        if (cacheName != null && result != null) {
                            SpringMetricsDTO.CacheMetrics metrics =
                                    cacheMetrics.computeIfAbsent(cacheName, k -> {
                                        SpringMetricsDTO.CacheMetrics m = new SpringMetricsDTO.CacheMetrics();
                                        m.setName(k);
                                        return m;
                                    });

                            if ("hit".equals(result)) {
                                metrics.setHitCount((long) counter.count());
                            } else if ("miss".equals(result)) {
                                metrics.setMissCount((long) counter.count());
                            }
                        }
                    });

            meterRegistry.find("cache.size")
                    .gauges()
                    .forEach(gauge -> {
                        String cacheName = gauge.getId().getTag("cache");
                        if (cacheName != null) {
                            SpringMetricsDTO.CacheMetrics metrics =
                                    cacheMetrics.computeIfAbsent(cacheName, k -> {
                                        SpringMetricsDTO.CacheMetrics m = new SpringMetricsDTO.CacheMetrics();
                                        m.setName(k);
                                        return m;
                                    });
                            metrics.setSize((long) gauge.value());
                        }
                    });

            meterRegistry.find("cache.evictions")
                    .counters()
                    .forEach(counter -> {
                        String cacheName = counter.getId().getTag("cache");
                        if (cacheName != null) {
                            SpringMetricsDTO.CacheMetrics metrics =
                                    cacheMetrics.computeIfAbsent(cacheName, k -> {
                                        SpringMetricsDTO.CacheMetrics m = new SpringMetricsDTO.CacheMetrics();
                                        m.setName(k);
                                        return m;
                                    });
                            metrics.setEvictionCount((long) counter.count());
                        }
                    });

            // 计算命中率
            cacheMetrics.values().forEach(metrics -> {
                long totalRequests = metrics.getHitCount() + metrics.getMissCount();
                if (totalRequests > 0) {
                    double hitRate = (double) metrics.getHitCount() / totalRequests * 100;
                    metrics.setHitRate(Math.round(hitRate * 100.0) / 100.0);
                }
            });

        } catch (Exception e) {
            log.debug("获取缓存指标失败", e);
        }

        return cacheMetrics;
    }
}
