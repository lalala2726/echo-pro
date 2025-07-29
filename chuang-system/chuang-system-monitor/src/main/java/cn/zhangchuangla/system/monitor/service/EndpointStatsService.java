package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.system.monitor.dto.EndpointStatsDTO;
import cn.zhangchuangla.system.monitor.request.EndpointStatsQueryRequest;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 端点统计服务
 *
 * @author Chuang
 * @since 2025/1/15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointStatsService {

    private final MeterRegistry meterRegistry;

    /**
     * 获取端点统计列表
     *
     * @param request 查询请求
     * @return 端点统计分页结果
     */
    public PageResult<EndpointStatsDTO> getEndpointStats(EndpointStatsQueryRequest request) {
        // 获取所有端点统计数据
        List<EndpointStatsDTO> allStats = collectEndpointStats();
        return queryEndpointStats(allStats, request);
    }

    /**
     * 查询端点统计
     *
     * @param endpointStats 端点统计列表
     * @param request       查询参数
     * @return 查询结果
     */
    public PageResult<EndpointStatsDTO> queryEndpointStats(List<EndpointStatsDTO> endpointStats, EndpointStatsQueryRequest request) {
        // 关键字段过滤条件
        String methodKeyword = request.getMethod();
        String uriKeyword = request.getUri();
        String endpointKeyword = request.getEndpoint();
        Long minRequestCount = request.getMinRequestCount();
        Double maxAverageResponseTime = request.getMaxAverageResponseTime();
        Double minSuccessRate = request.getMinSuccessRate();

        // 分页参数（-1 表示不分页）
        long pageNum = request.getPageNum();
        long pageSize = request.getPageSize();
        boolean noPaging = pageNum == -1 && pageSize == -1;

        // 构造过滤+排序流
        List<EndpointStatsDTO> filtered = endpointStats.stream()
                // HTTP方法过滤
                .filter(stats -> {
                    if (methodKeyword == null || methodKeyword.isBlank()) {
                        return true;
                    }
                    return methodKeyword.equalsIgnoreCase(stats.getMethod());
                })
                // URI路径模糊查询
                .filter(stats -> {
                    if (uriKeyword == null || uriKeyword.isBlank()) {
                        return true;
                    }
                    String uri = stats.getUri();
                    return uri != null && !uri.isBlank() && uri.toLowerCase().contains(uriKeyword.toLowerCase());
                })
                // 端点路径模糊查询
                .filter(stats -> {
                    if (endpointKeyword == null || endpointKeyword.isBlank()) {
                        return true;
                    }
                    String endpoint = stats.getEndpoint();
                    return endpoint != null && !endpoint.isBlank() && endpoint.toLowerCase().contains(endpointKeyword.toLowerCase());
                })
                // 最小请求数过滤
                .filter(stats -> {
                    if (minRequestCount == null || minRequestCount <= 0) {
                        return true;
                    }
                    return stats.getRequestCount() >= minRequestCount;
                })
                // 最大平均响应时间过滤
                .filter(stats -> {
                    if (maxAverageResponseTime == null || maxAverageResponseTime <= 0) {
                        return true;
                    }
                    return stats.getAverageResponseTime() <= maxAverageResponseTime;
                })
                // 最小成功率过滤
                .filter(stats -> {
                    if (minSuccessRate == null || minSuccessRate < 0) {
                        return true;
                    }
                    return stats.getSuccessRate() >= minSuccessRate;
                })
                // 排序：按指定字段排序
                .sorted(buildComparator(request))
                .toList();

        long total = filtered.size();
        List<EndpointStatsDTO> rows;

        if (noPaging) {
            // 不分页，返回所有
            rows = filtered;
        } else {
            // 正常分页
            long validPageNum = Math.max(pageNum, 1);
            long validPageSize = Math.max(pageSize, 1);
            long skip = (validPageNum - 1) * validPageSize;
            rows = filtered.stream()
                    .skip(skip)
                    .limit(validPageSize)
                    .toList();
        }

        return PageResult.<EndpointStatsDTO>builder()
                .pageNum(noPaging ? -1L : pageNum)
                .pageSize(noPaging ? -1L : pageSize)
                .total(total)
                .rows(rows)
                .build();
    }

    /**
     * 收集端点统计数据
     *
     * @return 端点统计列表
     */
    private List<EndpointStatsDTO> collectEndpointStats() {
        Map<String, EndpointStatsDTO> statsMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

        // 从Timer指标收集基础统计信息
        meterRegistry.find("http.server.requests")
                .timers()
                .forEach(timer -> {
                    String uri = timer.getId().getTag("uri");
                    String method = timer.getId().getTag("method");
                    String status = timer.getId().getTag("status");

                    if (uri != null && method != null) {
                        String endpoint = method + " " + uri;
                        EndpointStatsDTO stats = statsMap.computeIfAbsent(endpoint, k -> {
                            EndpointStatsDTO dto = new EndpointStatsDTO();
                            dto.setEndpoint(k);
                            dto.setMethod(method);
                            dto.setUri(uri);
                            dto.setRequestCount(0L);
                            dto.setSuccessCount(0L);
                            dto.setClientErrorCount(0L);
                            dto.setServerErrorCount(0L);
                            dto.setTimestamp(now);
                            return dto;
                        });

                        // 累加请求数
                        long count = timer.count();
                        stats.setRequestCount(stats.getRequestCount() + count);

                        // 更新响应时间统计
                        if (count > 0) {
                            double avgTime = timer.mean(TimeUnit.MILLISECONDS);
                            double maxTime = timer.max(TimeUnit.MILLISECONDS);

                            // 如果是第一次设置或者需要更新
                            if (stats.getAverageResponseTime() == null) {
                                stats.setAverageResponseTime(avgTime);
                                stats.setMaxResponseTime(maxTime);
                            } else {
                                // 加权平均计算
                                double totalTime = stats.getAverageResponseTime() * (stats.getRequestCount() - count) + avgTime * count;
                                stats.setAverageResponseTime(totalTime / stats.getRequestCount());
                                stats.setMaxResponseTime(Math.max(stats.getMaxResponseTime(), maxTime));
                            }
                        }

                        // 根据状态码分类统计
                        if (status != null) {
                            if (status.startsWith("2")) {
                                stats.setSuccessCount(stats.getSuccessCount() + count);
                            } else if (status.startsWith("4")) {
                                stats.setClientErrorCount(stats.getClientErrorCount() + count);
                            } else if (status.startsWith("5")) {
                                stats.setServerErrorCount(stats.getServerErrorCount() + count);
                            }
                        }
                    }
                });

        // 计算衍生指标
        return statsMap.values().stream()
                .peek(stats -> {
                    // 计算成功率
                    if (stats.getRequestCount() > 0) {
                        double successRate = (double) stats.getSuccessCount() / stats.getRequestCount() * 100;
                        stats.setSuccessRate(Math.round(successRate * 100.0) / 100.0);
                    } else {
                        stats.setSuccessRate(0.0);
                    }

                    // 计算QPS
                    if (uptime > 0 && stats.getRequestCount() > 0) {
                        double qps = (double) stats.getRequestCount() / (uptime / 1000.0);
                        stats.setQps(Math.round(qps * 100.0) / 100.0);
                    } else {
                        stats.setQps(0.0);
                    }

                    // 设置默认值
                    if (stats.getAverageResponseTime() == null) {
                        stats.setAverageResponseTime(0.0);
                    }
                    if (stats.getMaxResponseTime() == null) {
                        stats.setMaxResponseTime(0.0);
                    }

                })
                .collect(Collectors.toList());
    }


    /**
     * 构建排序比较器
     *
     * @param request 查询请求
     * @return 比较器
     */
    private Comparator<EndpointStatsDTO> buildComparator(EndpointStatsQueryRequest request) {
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "requestCount";
        String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
        boolean isAsc = "asc".equalsIgnoreCase(sortDirection);

        Comparator<EndpointStatsDTO> comparator = switch (sortBy.toLowerCase()) {
            case "averageresponsetime" -> Comparator.comparing(EndpointStatsDTO::getAverageResponseTime,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "maxresponsetime" -> Comparator.comparing(EndpointStatsDTO::getMaxResponseTime,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "successrate" -> Comparator.comparing(EndpointStatsDTO::getSuccessRate,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "qps" -> Comparator.comparing(EndpointStatsDTO::getQps,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(EndpointStatsDTO::getRequestCount,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };

        return isAsc ? comparator : comparator.reversed();
    }

    /**
     * 获取端点统计概览
     *
     * @return 统计概览
     */
    public Map<String, Object> getEndpointStatsOverview() {
        try {
            List<EndpointStatsDTO> allStats = collectEndpointStats();
            Map<String, Object> overview = new HashMap<>();

            // 总端点数
            overview.put("totalEndpoints", allStats.size());

            // 总请求数
            long totalRequests = allStats.stream()
                    .mapToLong(EndpointStatsDTO::getRequestCount)
                    .sum();
            overview.put("totalRequests", totalRequests);

            // 平均响应时间
            double avgResponseTime = allStats.stream()
                    .filter(stats -> stats.getRequestCount() > 0)
                    .mapToDouble(stats -> stats.getAverageResponseTime() * stats.getRequestCount())
                    .sum() / Math.max(totalRequests, 1);
            overview.put("averageResponseTime", Math.round(avgResponseTime * 100.0) / 100.0);

            // 最慢的端点
            Optional<EndpointStatsDTO> slowestEndpoint = allStats.stream()
                    .filter(stats -> stats.getRequestCount() > 0)
                    .max(Comparator.comparing(EndpointStatsDTO::getAverageResponseTime));
            overview.put("slowestEndpoint", slowestEndpoint.map(EndpointStatsDTO::getEndpoint).orElse("N/A"));

            // 最热门的端点
            Optional<EndpointStatsDTO> hottestEndpoint = allStats.stream()
                    .max(Comparator.comparing(EndpointStatsDTO::getRequestCount));
            overview.put("hottestEndpoint", hottestEndpoint.map(EndpointStatsDTO::getEndpoint).orElse("N/A"));

            // 整体成功率
            long totalSuccess = allStats.stream()
                    .mapToLong(EndpointStatsDTO::getSuccessCount)
                    .sum();
            double overallSuccessRate = totalRequests > 0 ? (double) totalSuccess / totalRequests * 100 : 0;
            overview.put("overallSuccessRate", Math.round(overallSuccessRate * 100.0) / 100.0);

            overview.put("timestamp", LocalDateTime.now());

            return overview;
        } catch (Exception e) {
            log.error("获取端点统计概览失败", e);
            return Collections.emptyMap();
        }
    }
}
