package cn.zhangchuangla.common.core.config.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
         * 过期会话清理任务的 CRON 表达式
         * 用于定时清除 ZSET 中 score 已过期的 member
         */
        @NotBlank
        private String cleanupCron = "0 0/5 * * * *"; // 每 5 分钟一次

        /**
         * ZSet 中 score 的类型：
         * LOGIN_TIME  - score = 登录时间戳（ms）
         * LAST_ACTIVE - score = 最近活动时间戳（ms）
         */
        private ScoreType scoreType = ScoreType.LOGIN_TIME;

        public enum ScoreType {
            LOGIN_TIME,
            LAST_ACTIVE
        }

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
