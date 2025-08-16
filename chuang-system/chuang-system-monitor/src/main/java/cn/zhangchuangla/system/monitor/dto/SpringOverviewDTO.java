package cn.zhangchuangla.system.monitor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Spring 应用概述 DTO
 *
 * @author Chuang
 */
@Data
@Schema(description = "Spring应用概述")
public class SpringOverviewDTO {

    /**
     * 应用名称
     */
    @Schema(description = "应用名称", example = "person-app-backend")
    private String name;

    /**
     * 应用版本
     */
    @Schema(description = "启动时间", example = "2025-08-16T09:00:00")
    private LocalDateTime startTime;

    /**
     * 运行时间
     */
    @Schema(description = "运行时间(毫秒)", example = "3600000")
    private long uptime;

    /**
     * 活跃配置文件
     */
    @Schema(description = "活跃配置文件", example = "[dev]")
    private String[] activeProfiles;
}
