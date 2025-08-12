package cn.zhangchuangla.system.monitor.request;

import cn.zhangchuangla.common.core.entity.base.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 端点统计查询请求
 *
 * @author Chuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "端点统计查询请求")
public class EndpointStatsQueryRequest extends BasePageRequest {

    /**
     * HTTP方法
     */
    @Schema(description = "HTTP方法", type = "string", example = "GET")
    private String method;

    /**
     * URI路径（支持模糊查询）
     */
    @Schema(description = "URI路径", type = "string", example = "/api/users")
    private String uri;

    /**
     * 端点路径（支持模糊查询）
     */
    @Schema(description = "端点路径", type = "string", example = "GET /api/users")
    private String endpoint;

    /**
     * 最小请求数过滤
     */
    @Schema(description = "最小请求数", type = "integer", format = "int64")
    private Long minRequestCount;

    /**
     * 最大平均响应时间过滤（毫秒）
     */
    @Schema(description = "最大平均响应时间（毫秒）", type = "number", format = "double")
    private Double maxAverageResponseTime;

    /**
     * 最小成功率过滤（百分比）
     */
    @Schema(description = "最小成功率（百分比）", type = "number", format = "double")
    private Double minSuccessRate;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", type = "string", allowableValues = {"requestCount", "averageResponseTime", "maxResponseTime", "successRate", "qps"})
    private String sortBy = "requestCount";

    /**
     * 排序方向
     */
    @Schema(description = "排序方向", type = "string", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";
}
