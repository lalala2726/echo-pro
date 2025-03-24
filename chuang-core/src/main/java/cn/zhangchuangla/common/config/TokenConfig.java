package cn.zhangchuangla.common.config;

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
@ConfigurationProperties(prefix = "token")
public class TokenConfig {


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

}
