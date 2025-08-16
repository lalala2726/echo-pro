package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.SpringOverviewDTO;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 提供 Spring 应用概述信息
 *
 * @author ang
 */
@Service
public class SpringOverviewService {

    private final Environment environment;

    public SpringOverviewService(Environment environment) {
        this.environment = environment;
    }

    public SpringOverviewDTO getOverview() {
        SpringOverviewDTO dto = new SpringOverviewDTO();
        dto.setName(environment.getProperty("spring.application.name", "Unknown"));
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        dto.setStartTime(LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(startTime), ZoneId.systemDefault()));
        dto.setUptime(ManagementFactory.getRuntimeMXBean().getUptime());
        dto.setActiveProfiles(environment.getActiveProfiles());
        return dto;
    }
}
