package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.base.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.system.monitor.dto.JvmMetricsDTO;
import cn.zhangchuangla.system.monitor.dto.RedisMetricsDTO;
import cn.zhangchuangla.system.monitor.dto.SpringOverviewDTO;
import cn.zhangchuangla.system.monitor.dto.SystemMetricsDTO;
import cn.zhangchuangla.system.monitor.service.JvmMonitorService;
import cn.zhangchuangla.system.monitor.service.RedisMonitorService;
import cn.zhangchuangla.system.monitor.service.SpringOverviewService;
import cn.zhangchuangla.system.monitor.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SpringOverviewService springOverviewService;

    /**
     * 获取系统监控指标
     *
     * @return 系统监控指标
     */
    @GetMapping("/system")
    @Operation(summary = "获取系统监控指标", description = "获取CPU、内存、磁盘等系统监控指标")
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
     * 获取所有监控指标概览
     *
     * @return 所有监控指标概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取监控概览", description = "获取所有监控指标的概览信息")
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
            SpringOverviewDTO springOverview = springOverviewService.getOverview();
            overview.put("springOverview", springOverview);

            return success(overview);
        } catch (Exception e) {
            log.error("获取监控概览失败", e);
            return error("获取监控概览失败: " + e.getMessage());
        }
    }
}
