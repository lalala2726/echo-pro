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

    /**
     * 密码管理配置
     */
    public PasswordConfig passwordConfig;

    /**
     * 会话管理配置
     */
    public SessionConfig session;


    /**
     * token标识符
     */
    private String header = "Authorization";

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间
     */
    private Integer expire;


    @Data
    public static class PasswordConfig {

        /**
         * 最大尝试次数，-1不限制
         */
        @Min(-1)
        private Integer maxRetryCount = 3;

        /**
         * 锁定时间
         */
        @Min(60)
        private Integer lockTime = 120;
    }

    @Data
    public static class SessionConfig {

        /**
         * 访问令牌有效期（单位：秒）
         * 默认值：7200秒（2小时）
         */
        @Min(-1)
        private Integer accessTokenExpireTime = 7200;

        /**
         * 刷新令牌有效期（单位：秒）
         * 默认值：2592000秒（30天）
         */
        @Min(-1)
        private Integer refreshTokenExpireTime = 2592000;

        /**
         * 是否允许多设备同时登录
         * <p>true - 允许同一账户多设备登录（默认）</p>
         * <p>false - 新登录会使旧令牌失效</p>
         */
        private Boolean singleLogin = true;

    }


}
