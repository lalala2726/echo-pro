package cn.zhangchuangla.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/25 15:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "user.password")
public class PasswordConfig {

    /**
     * 最大尝试次数
     */
    private long maxRetryCount;

    /**
     * 锁定时间
     */
    private long lockTime;

}
