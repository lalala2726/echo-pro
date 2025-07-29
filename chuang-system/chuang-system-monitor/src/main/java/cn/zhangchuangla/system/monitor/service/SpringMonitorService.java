package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.SpringMetricsDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
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
    private final MetricsEndpoint metricsEndpoint;
    private final DataSource dataSource;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    public SpringMonitorService(Environment environment,
                                MeterRegistry meterRegistry,
                                MetricsEndpoint metricsEndpoint,
                                DataSource dataSource) {
        this.environment = environment;
        this.meterRegistry = meterRegistry;
        this.metricsEndpoint = metricsEndpoint;
        this.dataSource = dataSource;
    }

    /**
     * 获取Spring监控指标
     *
     * @return Spring监控指标
     */
    public SpringMetricsDTO getSpringMetrics() {
        SpringMetricsDTO metrics = new SpringMetricsDTO();
        metrics.setTimestamp(LocalDateTime.now());

        try {
            // 获取应用信息
            metrics.setApplication(getApplicationInfo());
            // 获取HTTP指标
            metrics.setHttp(getHttpMetrics());
            // 获取数据源指标
            metrics.setDataSource(getDataSourceMetrics());
            // 获取线程池指标
            metrics.setThreadPools(getThreadPoolMetrics());
            // 获取缓存指标
            metrics.setCaches(getCacheMetrics());
        } catch (Exception e) {
            log.error("获取Spring监控指标失败", e);
        }

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
     * 获取HTTP指标
     */
    private SpringMetricsDTO.HttpMetrics getHttpMetrics() {
        SpringMetricsDTO.HttpMetrics httpMetrics = new SpringMetricsDTO.HttpMetrics();

        try {
            // 从Micrometer获取HTTP指标
            Counter totalRequests = meterRegistry.find("http.server.requests").counter();
            if (totalRequests != null) {
                httpMetrics.setTotalRequests((long) totalRequests.count());
            }

            // 获取各状态码统计
            Map<String, Long> statusCodeStats = new HashMap<>();
            meterRegistry.find("http.server.requests")
                    .counters()
                    .forEach(counter -> {
                        String status = counter.getId().getTag("status");
                        if (status != null) {
                            statusCodeStats.put(status, (long) counter.count());
                        }
                    });
            httpMetrics.setStatusCodeStats(statusCodeStats);

            // 计算成功、客户端错误、服务器错误请求数
            long successRequests = statusCodeStats.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("2"))
                    .mapToLong(Map.Entry::getValue)
                    .sum();
            httpMetrics.setSuccessRequests(successRequests);

            long clientErrorRequests = statusCodeStats.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("4"))
                    .mapToLong(Map.Entry::getValue)
                    .sum();
            httpMetrics.setClientErrorRequests(clientErrorRequests);

            long serverErrorRequests = statusCodeStats.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("5"))
                    .mapToLong(Map.Entry::getValue)
                    .sum();
            httpMetrics.setServerErrorRequests(serverErrorRequests);

            // 获取响应时间指标
            Timer responseTimer = meterRegistry.find("http.server.requests").timer();
            if (responseTimer != null) {
                httpMetrics.setAverageResponseTime(responseTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS));
                httpMetrics.setMaxResponseTime(responseTimer.max(java.util.concurrent.TimeUnit.MILLISECONDS));
            }

            // 计算每秒请求数（基于运行时间）
            long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
            if (uptime > 0) {
                double requestsPerSecond = (double) httpMetrics.getTotalRequests() / (uptime / 1000.0);
                httpMetrics.setRequestsPerSecond(Math.round(requestsPerSecond * 100.0) / 100.0);
            }

            // 获取各端点统计
            Map<String, SpringMetricsDTO.HttpMetrics.EndpointStats> endpointStats = new HashMap<>();
            meterRegistry.find("http.server.requests")
                    .timers()
                    .forEach(timer -> {
                        String uri = timer.getId().getTag("uri");
                        String method = timer.getId().getTag("method");
                        if (uri != null && method != null) {
                            String endpoint = method + " " + uri;
                            SpringMetricsDTO.HttpMetrics.EndpointStats stats =
                                    new SpringMetricsDTO.HttpMetrics.EndpointStats();
                            stats.setCount((long) timer.count());
                            stats.setAverageTime(timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS));
                            stats.setMaxTime(timer.max(java.util.concurrent.TimeUnit.MILLISECONDS));
                            endpointStats.put(endpoint, stats);
                        }
                    });
            httpMetrics.setEndpointStats(endpointStats);

        } catch (Exception e) {
            log.debug("获取HTTP指标失败", e);
        }

        return httpMetrics;
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
