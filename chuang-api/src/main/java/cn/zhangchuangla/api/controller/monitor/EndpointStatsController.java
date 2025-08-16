package cn.zhangchuangla.api.controller.monitor;

import cn.zhangchuangla.common.core.base.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.framework.annotation.Anonymous;
import cn.zhangchuangla.system.monitor.dto.EndpointStatsDTO;
import cn.zhangchuangla.system.monitor.request.EndpointStatsQueryRequest;
import cn.zhangchuangla.system.monitor.service.EndpointStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/29 21:51
 */
@Slf4j
@RestController
@RequestMapping("/monitor/endpoints")
@Tag(name = "系统监控", description = "系统监控相关接口")
@RequiredArgsConstructor
@Anonymous
public class EndpointStatsController extends BaseController {

    private final EndpointStatsService endpointStatsService;

    /**
     * 获取端点统计列表
     *
     * @param request 查询请求
     * @return 端点统计分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "获取端点统计列表", description = "获取HTTP端点的详细统计信息，支持分页和过滤")
    public AjaxResult<TableDataResult> getEndpointStats(EndpointStatsQueryRequest request) {
        PageResult<EndpointStatsDTO> result = endpointStatsService.getEndpointStats(request);
        return getTableData(result);
    }

    /**
     * 获取端点统计概览
     *
     * @return 端点统计概览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取端点统计概览", description = "获取端点统计的汇总信息")
    public AjaxResult<Map<String, Object>> getEndpointStatsOverview() {
        Map<String, Object> overview = endpointStatsService.getEndpointStatsOverview();
        return success(overview);
    }

}
