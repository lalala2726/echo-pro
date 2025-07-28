package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.system.monitor.dto.JvmMetricsDTO;
import cn.zhangchuangla.system.monitor.dto.RedisMetricsDTO;
import cn.zhangchuangla.system.monitor.dto.SpringMetricsDTO;
import cn.zhangchuangla.system.monitor.dto.SystemMetricsDTO;
import cn.zhangchuangla.system.monitor.service.JvmMonitorService;
import cn.zhangchuangla.system.monitor.service.RedisMonitorService;
import cn.zhangchuangla.system.monitor.service.SpringMonitorService;
import cn.zhangchuangla.system.monitor.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统监控Controller
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Slf4j
@RestController
@RequestMapping("/monitor/metrics")
@Tag(name = "系统监控", description = "系统监控相关接口")
@RequiredArgsConstructor
public class MonitorController extends BaseController {

    private final SystemMonitorService systemMonitorService;
    private final JvmMonitorService jvmMonitorService;
    private final RedisMonitorService redisMonitorService;
    private final SpringMonitorService springMonitorService;

    /**
     * 获取系统监控指标
     *
     * @return 系统监控指标
     */
    @GetMapping("/system")
    @Operation(summary = "获取系统监控指标", description = "获取CPU、内存、磁盘等系统监控指标")
    @PreAuthorize("@ss.hasPermission('monitor:system:list')")
    public AjaxResult<SystemMetricsDTO> getSystemMetrics() {
        try {
            SystemMetricsDTO metrics = systemMonitorService.getSystemMetrics();
            return success(metrics);
        } catch (Exception e) {
            log.error("获取系统监控指标失败", e);
            return error("获取系统监控指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取JVM监控指标
     *
     * @return JVM监控指标
     */
    @GetMapping("/jvm")
    @Operation(summary = "获取JVM监控指标", description = "获取JVM内存、垃圾回收、线程等监控指标")
    @PreAuthorize("@ss.hasPermission('monitor:jvm:list')")
    public AjaxResult<JvmMetricsDTO> getJvmMetrics() {
        try {
            JvmMetricsDTO metrics = jvmMonitorService.getJvmMetrics();
            return success(metrics);
        } catch (Exception e) {
            log.error("获取JVM监控指标失败", e);
            return error("获取JVM监控指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取Redis监控指标
     *
     * @return Redis监控指标
     */
    @GetMapping("/redis")
    @Operation(summary = "获取Redis监控指标", description = "获取Redis内存、连接、命令统计等监控指标")
    @PreAuthorize("@ss.hasPermission('monitor:redis:list')")
    public AjaxResult<RedisMetricsDTO> getRedisMetrics() {
        try {
            RedisMetricsDTO metrics = redisMonitorService.getRedisMetrics();
            return success(metrics);
        } catch (Exception e) {
            log.error("获取Redis监控指标失败", e);
            return error("获取Redis监控指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取Spring监控指标
     *
     * @return Spring监控指标
     */
    @GetMapping("/spring")
    @Operation(summary = "获取Spring监控指标", description = "获取Spring应用、HTTP、数据源、线程池等监控指标")
    @PreAuthorize("@ss.hasPermission('monitor:spring:list')")
    public AjaxResult<SpringMetricsDTO> getSpringMetrics() {
        try {
            SpringMetricsDTO metrics = springMonitorService.getSpringMetrics();
            return success(metrics);
        } catch (Exception e) {
            log.error("获取Spring监控指标失败", e);
            return error("获取Spring监控指标失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有监控指标概览
     *
     * @return 所有监控指标概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取监控概览", description = "获取所有监控指标的概览信息")
    @PreAuthorize("@ss.hasPermission('monitor:overview:list')")
    public AjaxResult<Map<String, Object>> getMonitorOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();

            // 系统指标概览
            SystemMetricsDTO systemMetrics = systemMonitorService.getSystemMetrics();
            Map<String, Object> systemOverview = new HashMap<>();
            systemOverview.put("cpuUsage", systemMetrics.getCpu().getUsage());
            systemOverview.put("memoryUsage", systemMetrics.getMemory().getUsage());
            systemOverview.put("diskCount", systemMetrics.getDisks().size());
            systemOverview.put("timestamp", systemMetrics.getTimestamp());
            overview.put("system", systemOverview);

            // JVM指标概览
            JvmMetricsDTO jvmMetrics = jvmMonitorService.getJvmMetrics();
            Map<String, Object> jvmOverview = new HashMap<>();
            jvmOverview.put("heapUsage", jvmMetrics.getMemory().getHeap().getUsage());
            jvmOverview.put("nonHeapUsage", jvmMetrics.getMemory().getNonHeap().getUsage());
            jvmOverview.put("threadCount", jvmMetrics.getThreads().getThreadCount());
            jvmOverview.put("gcCount", jvmMetrics.getGc().stream()
                    .mapToLong(JvmMetricsDTO.GcMetrics::getCollectionCount).sum());
            jvmOverview.put("timestamp", jvmMetrics.getTimestamp());
            overview.put("jvm", jvmOverview);

            // Redis指标概览
            RedisMetricsDTO redisMetrics = redisMonitorService.getRedisMetrics();
            Map<String, Object> redisOverview = new HashMap<>();
            redisOverview.put("connectedClients", redisMetrics.getConnections().getConnectedClients());
            redisOverview.put("usedMemory", redisMetrics.getMemory().getUsedMemoryHuman());
            redisOverview.put("hitRate", redisMetrics.getPerformance().getHitRate());
            redisOverview.put("opsPerSec", redisMetrics.getCommandStats().getInstantaneousOpsPerSec());
            redisOverview.put("timestamp", redisMetrics.getTimestamp());
            overview.put("redis", redisOverview);

            // Spring指标概览
            SpringMetricsDTO springMetrics = springMonitorService.getSpringMetrics();
            Map<String, Object> springOverview = new HashMap<>();
            springOverview.put("totalRequests", springMetrics.getHttp().getTotalRequests());
            springOverview.put("averageResponseTime", springMetrics.getHttp().getAverageResponseTime());
            springOverview.put("dataSourceUsage", springMetrics.getDataSource().getUsage());
            springOverview.put("uptime", springMetrics.getApplication().getUptime());
            springOverview.put("timestamp", springMetrics.getTimestamp());
            overview.put("spring", springOverview);

            return success(overview);
        } catch (Exception e) {
            log.error("获取监控概览失败", e);
            return error("获取监控概览失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统健康状态
     *
     * @return 系统健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "获取系统健康状态", description = "获取系统各组件的健康状态")
    @PreAuthorize("@ss.hasPermission('monitor:health:list')")
    public AjaxResult<Map<String, Object>> getHealthStatus() {
        try {
            Map<String, Object> health = new HashMap<>();

            // 系统健康状态
            SystemMetricsDTO systemMetrics = systemMonitorService.getSystemMetrics();
            Map<String, Object> systemHealth = new HashMap<>();
            systemHealth.put("status", systemMetrics.getCpu().getUsage() < 80 &&
                    systemMetrics.getMemory().getUsage() < 85 ? "UP" : "DOWN");
            systemHealth.put("cpuUsage", systemMetrics.getCpu().getUsage());
            systemHealth.put("memoryUsage", systemMetrics.getMemory().getUsage());
            health.put("system", systemHealth);

            // JVM健康状态
            JvmMetricsDTO jvmMetrics = jvmMonitorService.getJvmMetrics();
            Map<String, Object> jvmHealth = new HashMap<>();
            jvmHealth.put("status", jvmMetrics.getMemory().getHeap().getUsage() < 85 ? "UP" : "DOWN");
            jvmHealth.put("heapUsage", jvmMetrics.getMemory().getHeap().getUsage());
            jvmHealth.put("threadCount", jvmMetrics.getThreads().getThreadCount());
            health.put("jvm", jvmHealth);

            // Redis健康状态
            try {
                RedisMetricsDTO redisMetrics = redisMonitorService.getRedisMetrics();
                Map<String, Object> redisHealth = new HashMap<>();
                redisHealth.put("status", "UP");
                redisHealth.put("connectedClients", redisMetrics.getConnections().getConnectedClients());
                redisHealth.put("hitRate", redisMetrics.getPerformance().getHitRate());
                health.put("redis", redisHealth);
            } catch (Exception e) {
                Map<String, Object> redisHealth = new HashMap<>();
                redisHealth.put("status", "DOWN");
                redisHealth.put("error", e.getMessage());
                health.put("redis", redisHealth);
            }

            // Spring健康状态
            SpringMetricsDTO springMetrics = springMonitorService.getSpringMetrics();
            Map<String, Object> springHealth = new HashMap<>();
            springHealth.put("status", "UP");
            springHealth.put("uptime", springMetrics.getApplication().getUptime());
            springHealth.put("dataSourceUsage", springMetrics.getDataSource().getUsage());
            health.put("spring", springHealth);

            // 整体健康状态
            boolean allUp = health.values().stream()
                    .allMatch(component -> {
                        if (component instanceof Map) {
                            return "UP".equals(((Map<?, ?>) component).get("status"));
                        }
                        return true;
                    });
            health.put("status", allUp ? "UP" : "DOWN");

            return success(health);
        } catch (Exception e) {
            log.error("获取系统健康状态失败", e);
            return error("获取系统健康状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取监控配置信息
     *
     * @return 监控配置信息
     */
    @GetMapping("/config")
    @Operation(summary = "获取监控配置", description = "获取当前监控系统的配置信息")
    @PreAuthorize("@ss.hasPermission('monitor:config:list')")
    public AjaxResult<Map<String, Object>> getMonitorConfig() {
        try {
            Map<String, Object> config = new HashMap<>();

            // 这里可以添加监控配置信息
            config.put("enabled", true);
            config.put("version", "1.0.0");
            config.put("features", new String[]{"system", "jvm", "redis", "spring"});
            config.put("updateTime", java.time.LocalDateTime.now());

            return success(config);
        } catch (Exception e) {
            log.error("获取监控配置失败", e);
            return error("获取监控配置失败: " + e.getMessage());
        }
    }
}
