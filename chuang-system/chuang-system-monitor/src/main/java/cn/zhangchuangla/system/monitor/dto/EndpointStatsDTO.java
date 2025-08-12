package cn.zhangchuangla.system.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 端点统计DTO
 *
 * @author Chuang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "端点统计信息")
public class EndpointStatsDTO {

    /**
     * 端点路径（方法 + URI）
     */
    @Schema(description = "端点路径", example = "GET /api/users")
    private String endpoint;

    /**
     * HTTP方法
     */
    @Schema(description = "HTTP方法", example = "GET")
    private String method;

    /**
     * URI路径
     */
    @Schema(description = "URI路径", example = "/api/users")
    private String uri;

    /**
     * 请求总数
     */
    @Schema(description = "请求总数")
    private Long requestCount;

    /**
     * 平均响应时间（毫秒）
     */
    @Schema(description = "平均响应时间（毫秒）")
    private Double averageResponseTime;

    /**
     * 最大响应时间（毫秒）
     */
    @Schema(description = "最大响应时间（毫秒）")
    private Double maxResponseTime;

    /**
     * 成功请求数（2xx状态码）
     */
    @Schema(description = "成功请求数")
    private Long successCount;

    /**
     * 客户端错误请求数（4xx状态码）
     */
    @Schema(description = "客户端错误请求数")
    private Long clientErrorCount;

    /**
     * 服务器错误请求数（5xx状态码）
     */
    @Schema(description = "服务器错误请求数")
    private Long serverErrorCount;

    /**
     * 成功率（百分比）
     */
    @Schema(description = "成功率（百分比）")
    private Double successRate;

    /**
     * 每秒请求数（QPS）
     */
    @Schema(description = "每秒请求数")
    private Double qps;

    /**
     * 统计时间
     */
    @Schema(description = "统计时间")
    private LocalDateTime timestamp;
}
