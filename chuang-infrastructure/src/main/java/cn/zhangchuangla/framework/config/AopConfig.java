package cn.zhangchuangla.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP配置类
 * 启用AspectJ自动代理，确保切面能正常工作
 *
 * @author Chuang
 * created on 2025/3/25 11:30
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
    // 配置类不需要额外的内容
}
