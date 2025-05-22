package cn.zhangchuangla.system.loader;

import cn.zhangchuangla.common.core.service.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 系统配置加载器，在项目启动时加载系统配置到缓存中
 *
 * @author Chuang
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SysConfigLoader implements DataLoader {

    @Override
    public String getName() {
        return "系统配置加载器";
    }

    @Override
    public int getOrder() {
        // 最高优先级，在其他加载器之前执行
        return 5;
    }

    @Override
    public void load() {
        log.info("开始加载系统配置...");
        try {
            // 模拟加载过程
            log.info("正在从数据库读取系统参数配置...");
            Thread.sleep(300);

            // TODO: 实际实现中应该从数据库加载系统配置

            log.info("系统配置加载完成");
        } catch (Exception e) {
            log.error("系统配置加载失败", e);
            throw new RuntimeException("系统配置加载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAsync() {
        // 系统配置必须同步加载
        return false;
    }
}
