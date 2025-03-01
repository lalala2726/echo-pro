package cn.zhangchuangla.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/27 22:47
 */
@ConfigurationProperties(prefix = "user.login")
@Configuration
@Data
public class LoginConfig {

    /**
     * 最大登录会话数
     */
    private int maxLoginSession;
}
