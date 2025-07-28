package cn.zhangchuangla.common.core.config.property;

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
        @Min(1)
        private Integer lockTime = 120;
    }

    @Data
    public static class SessionConfig {

        /**
         * 每个客户端允许的最大会话数
         */
        public MaxSessionsPerClient maxSessionsPerClient;

        /**
         * 访问令牌有效期（单位：秒），-1 表示永不过期
         * 默认值：1800 秒（30分钟）
         */
        @Min(-1)
        private long accessTokenExpireTime = 1800;

        /**
         * 刷新令牌有效期（单位：秒），-1 表示永不过期
         * 默认值：2592000 秒（30天）
         */
        @Min(-1)
        private long refreshTokenExpireTime = 2592000;

        /**
         * 是否允许多设备同时登录
         * true  - 多设备（默认）
         * false - 单设备登录（新登录挤掉旧会话）
         */
        private boolean multiDevice = true;

        /**
         * 令牌前缀 系统默认会给前缀和token之间拼接一个空格如: Bearer xxxxxxxx
         */
        private String tokenPrefix = "Bearer";

        /**
         * 登录频次限制：每用户每小时最多尝试登录次数，-1 表示不限制
         */
        @Min(-1)
        private int maxLoginPerHour = 10;

        /**
         * 登录频次限制：每用户每天最多尝试登录次数，-1 表示不限制
         */
        @Min(-1)
        private int maxLoginPerDay = 50;

        /**
         * 每个客户端允许的最大会话数
         */
        @Data
        public static class MaxSessionsPerClient {

            /**
             * 网页端最大会话数 -1 表示不限制，默认值：-1
             */
            @Min(-1)
            private long web = -1;

            /**
             * PC 端最大会话数 -1 表示不限制，默认值：-1
             */
            @Min(-1)
            private long pc = -1;

            /**
             * 移动端最大会话数 -1 表示不限制，默认值：-1
             */
            @Min(-1)
            private long mobile = -1;

            /**
             * 小程序最大会话数 -1 表示不限制，默认值：-1
             */
            @Min(-1)
            private long miniProgram = -1;

            /**
             * 未知端最大会话数 -1 表示不限制，默认值：-1
             */
            @Min(-1)
            private long unknown = -1;
        }

    }


}
