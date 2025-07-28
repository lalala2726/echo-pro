package cn.zhangchuangla.common.redis.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chuang
 * <p>
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {

    /**
     * 使用scan每次扫描的key数量，默认1000
     */
    @Min(value = 1, message = "scanCount不能小于1")
    public int scanCount = 1000;


}
