package cn.zhangchuangla.system.monitor.config;

import cn.zhangchuangla.system.monitor.service.JvmMonitorService;
import cn.zhangchuangla.system.monitor.service.RedisMonitorService;
import cn.zhangchuangla.system.monitor.service.SpringMonitorService;
import cn.zhangchuangla.system.monitor.service.SystemMonitorService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;

/**
 * 监控自动配置类
 *
 * @author Chuang
 * created on 2025/7/28
 */
@Slf4j
@AutoConfiguration(after = MetricsAutoConfiguration.class)
@EnableConfigurationProperties(MonitoringProperties.class)
@ConditionalOnProperty(prefix = "monitoring", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "cn.zhangchuangla.system.monitor")
public class MonitorAutoConfiguration {

    /**
     * 系统监控服务
     */
    @Bean
    @ConditionalOnMissingBean
    public SystemMonitorService systemMonitorService() {
        log.info("初始化系统监控服务");
        return new SystemMonitorService();
    }

    /**
     * JVM监控服务
     */
    @Bean
    @ConditionalOnMissingBean
    public JvmMonitorService jvmMonitorService() {
        log.info("初始化JVM监控服务");
        return new JvmMonitorService();
    }

    /**
     * Redis监控服务
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass({RedisTemplate.class, RedisConnectionFactory.class})
    public RedisMonitorService redisMonitorService(RedisTemplate<Object, Object> redisTemplate,
                                                   RedisConnectionFactory redisConnectionFactory) {
        log.info("初始化Redis监控服务");
        return new RedisMonitorService(redisTemplate, redisConnectionFactory);
    }

    /**
     * Spring监控服务
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass({MeterRegistry.class, MetricsEndpoint.class})
    public SpringMonitorService springMonitorService(Environment environment,
                                                     MeterRegistry meterRegistry,
                                                     MetricsEndpoint metricsEndpoint,
                                                     DataSource dataSource) {
        log.info("初始化Spring监控服务");
        return new SpringMonitorService(environment, meterRegistry, metricsEndpoint, dataSource);
    }

    /**
     * 监控配置属性
     */
    @Bean
    @ConditionalOnMissingBean
    public MonitoringProperties monitoringProperties() {
        return new MonitoringProperties();
    }
}
