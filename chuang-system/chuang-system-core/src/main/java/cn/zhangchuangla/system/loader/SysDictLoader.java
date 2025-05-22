package cn.zhangchuangla.system.loader;

import cn.zhangchuangla.common.core.core.service.DataLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 字典加载器，在启动项目的时候统一将字典数据加载到缓存中
 * 本次操作为异步，所以不会影响启动速度
 *
 * @author Chuang
 * <p>
 * created on 2025/5/22 16:22
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SysDictLoader implements DataLoader {

    @Override
    public String getName() {
        return "系统字典加载器";
    }

    @Override
    public int getOrder() {
        return 20; // 优先级适中
    }

    @Override
    public void load() {
        try {
            log.info("开始加载系统字典数据到缓存...");
            // TODO: 从数据库加载字典数据
            // 1. 查询所有字典类型
            // 2. 查询所有字典数据
            // 3. 将数据放入缓存

            Thread.sleep(500); // 模拟加载耗时
            log.info("系统字典数据加载完成");
        } catch (Exception e) {
            log.error("加载系统字典数据失败", e);
            throw new RuntimeException("系统字典加载失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAsync() {
        return true; // 支持异步加载
    }
}
