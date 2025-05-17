package cn.zhangchuangla.generator.config;

import cn.zhangchuangla.generator.util.VelocityUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * Velocity引擎初始化配置
 *
 * @author Chuang
 */
@Slf4j
@Configuration
public class VelocityInitializer {

    @PostConstruct
    public void init() {
        try {
            VelocityUtils.initVelocity();
            log.info("Velocity引擎初始化成功。");
        } catch (Exception e) {
            log.error("Velocity引擎初始化失败！", e);
        }
    }
}