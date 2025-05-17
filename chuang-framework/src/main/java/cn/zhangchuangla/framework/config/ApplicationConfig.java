package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.common.utils.SpringUtils;
import cn.zhangchuangla.framework.manager.AsyncManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 应用配置类
 *
 * @author Chuang
 */
@Configuration
@Slf4j
@Import(SpringUtils.class)
public class ApplicationConfig {

    /**
     * 注册应用关闭时的钩子
     */
    @PostConstruct
    public void init() {
        log.info("初始化异步任务池");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("应用关闭钩子执行中...");
            AsyncManager.me().shutdown();
        }));
    }

    /**
     * Spring容器关闭前执行，确保异步任务池正确关闭
     */
    @PreDestroy
    public void preDestroy() {
        log.info("Spring容器关闭前执行，关闭异步任务池...");
        AsyncManager.me().shutdown();
    }
}
