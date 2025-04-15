package cn.zhangchuangla.common.config.property;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 17:51
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    public PasswordConfig passwordConfig;


    /**
     * token标识符
     */
    private String header;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间
     */
    private Long expire;


    @Data
    public static class PasswordConfig {

        /**
         * 最大尝试次数，-1不限制
         */
        @Min(-1)
        private long maxRetryCount;

        /**
         * 锁定时间
         */
        @Min(60)
        private long lockTime;

    }

}
