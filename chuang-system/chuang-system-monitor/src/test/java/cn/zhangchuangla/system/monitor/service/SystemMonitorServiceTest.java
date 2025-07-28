package cn.zhangchuangla.system.monitor.service;

import cn.zhangchuangla.system.monitor.dto.SystemMetricsDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 系统监控服务测试
 *
 * @author Chuang
 * created on 2025/7/28
 */
class SystemMonitorServiceTest {

    @Test
    void testGetSystemMetrics() {
        SystemMonitorService service = new SystemMonitorService();
        SystemMetricsDTO metrics = service.getSystemMetrics();

        assertNotNull(metrics);
        assertNotNull(metrics.getTimestamp());
        assertNotNull(metrics.getCpu());
        assertNotNull(metrics.getMemory());
        assertNotNull(metrics.getDisks());
        assertNotNull(metrics.getSystemInfo());

        // 验证CPU指标
        SystemMetricsDTO.CpuMetrics cpu = metrics.getCpu();
        assertNotNull(cpu.getName());
        assertTrue(cpu.getUsage() >= 0 && cpu.getUsage() <= 100);
        assertTrue(cpu.getLogicalProcessorCount() > 0);
        assertTrue(cpu.getPhysicalProcessorCount() > 0);

        // 验证内存指标
        SystemMetricsDTO.MemoryMetrics memory = metrics.getMemory();
        assertTrue(memory.getTotal() > 0);
        assertTrue(memory.getUsed() >= 0);
        assertTrue(memory.getAvailable() >= 0);
        assertTrue(memory.getUsage() >= 0 && memory.getUsage() <= 100);

        // 验证磁盘指标
        assertFalse(metrics.getDisks().isEmpty());
        for (SystemMetricsDTO.DiskMetrics disk : metrics.getDisks()) {
            assertNotNull(disk.getName());
            assertTrue(disk.getTotal() >= 0);
            assertTrue(disk.getUsed() >= 0);
            assertTrue(disk.getAvailable() >= 0);
        }

        // 验证系统信息
        SystemMetricsDTO.SystemInfo systemInfo = metrics.getSystemInfo();
        assertNotNull(systemInfo.getOsName());
        assertNotNull(systemInfo.getOsVersion());
        assertNotNull(systemInfo.getOsArch());
        assertNotNull(systemInfo.getBootTime());
        assertTrue(systemInfo.getUptime() > 0);
        assertTrue(systemInfo.getProcessCount() > 0);

        System.out.println("系统监控测试通过！");
        System.out.println("CPU使用率: " + cpu.getUsage() + "%");
        System.out.println("内存使用率: " + memory.getUsage() + "%");
        System.out.println("磁盘数量: " + metrics.getDisks().size());
    }
}
