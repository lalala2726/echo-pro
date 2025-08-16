package cn.zhangchuangla.system.monitor.controller;

import cn.zhangchuangla.system.monitor.dto.SpringOverviewDTO;
import cn.zhangchuangla.system.monitor.service.SpringOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring 概述接口
 *
 * @author zhangchuang
 */
@RestController
@RequestMapping("/monitor/overview")
@Tag(name = "Monitor-Overview", description = "Spring 应用概述")
public class OverviewController {

    private final SpringOverviewService overviewService;

    public OverviewController(SpringOverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping
    @Operation(summary = "获取 Spring 应用概述")
    public SpringOverviewDTO overview() {
        return overviewService.getOverview();
    }
}
