package cn.zhangchuangla.framework.config;

import cn.zhangchuangla.common.core.utils.SecurityValidationUtils;
import cn.zhangchuangla.framework.security.property.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全配置启动验证器
 * 在应用启动时检查关键安全配置，发现潜在的安全问题
 *
 * @author Chuang
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityStartupValidator implements ApplicationListener<ApplicationReadyEvent> {

    private final SecurityProperties securityProperties;
    private final Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("开始安全配置检查...");
        
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // 检查JWT密钥
        validateJwtSecret(warnings, errors);
        
        // 检查数据库配置
        validateDatabaseConfig(warnings, errors);
        
        // 检查Redis配置
        validateRedisConfig(warnings, errors);
        
        // 检查密码策略
        validatePasswordPolicy(warnings, errors);
        
        // 检查Druid配置
        validateDruidConfig(warnings, errors);
        
        // 检查HTTPS配置
        validateHttpsConfig(warnings, errors);

        // 输出检查结果
        reportResults(warnings, errors);
    }

    /**
     * 验证JWT密钥安全性
     */
    private void validateJwtSecret(List<String> warnings, List<String> errors) {
        String secret = securityProperties.getSecret();
        
        if (StringUtils.isBlank(secret)) {
            errors.add("JWT密钥未配置，这是严重的安全问题！");
            return;
        }

        if (!SecurityValidationUtils.isSecureJwtSecret(secret)) {
            errors.add("JWT密钥强度不足，建议使用至少32位的复杂随机字符串");
        }

        // 检查是否使用了常见的弱密钥模式
        if (secret.contains("zhangchuang") || secret.contains("admin") || secret.contains("123456")) {
            errors.add("JWT密钥包含可预测的字符串，存在安全风险");
        }
    }

    /**
     * 验证数据库配置
     */
    private void validateDatabaseConfig(List<String> warnings, List<String> errors) {
        String password = environment.getProperty("spring.datasource.password");
        String url = environment.getProperty("spring.datasource.url");

        if (StringUtils.isBlank(password)) {
            errors.add("数据库密码未配置");
        } else if (SecurityValidationUtils.isCommonWeakPassword(password)) {
            errors.add("数据库使用了常见的弱密码");
        }

        if (StringUtils.isNotBlank(url) && url.contains("useSSL=false")) {
            warnings.add("数据库连接未启用SSL加密");
        }
    }

    /**
     * 验证Redis配置
     */
    private void validateRedisConfig(List<String> warnings, List<String> errors) {
        String password = environment.getProperty("spring.data.redis.password");
        
        if (StringUtils.isBlank(password)) {
            warnings.add("Redis未设置密码，建议在生产环境中启用密码认证");
        } else if (SecurityValidationUtils.isCommonWeakPassword(password)) {
            errors.add("Redis使用了常见的弱密码");
        }
    }

    /**
     * 验证密码策略
     */
    private void validatePasswordPolicy(List<String> warnings, List<String> errors) {
        SecurityProperties.PasswordConfig passwordConfig = securityProperties.getPasswordConfig();
        
        if (passwordConfig == null) {
            warnings.add("密码策略配置缺失");
            return;
        }

        Integer maxRetryCount = passwordConfig.getMaxRetryCount();
        if (maxRetryCount == null || maxRetryCount == -1) {
            warnings.add("密码重试次数未限制，建议设置为5次以下");
        } else if (maxRetryCount > 10) {
            warnings.add("密码重试次数过高（" + maxRetryCount + "次），建议设置为5次以下");
        }

        Integer lockTime = passwordConfig.getLockTime();
        if (lockTime == null || lockTime < 60) {
            warnings.add("账户锁定时间过短，建议至少5分钟（300秒）");
        }
    }

    /**
     * 验证Druid配置
     */
    private void validateDruidConfig(List<String> warnings, List<String> errors) {
        String druidUsername = environment.getProperty("spring.datasource.druid.statViewServlet.login-username");
        String druidPassword = environment.getProperty("spring.datasource.druid.statViewServlet.login-password");
        String allowIps = environment.getProperty("spring.datasource.druid.statViewServlet.allow");
        
        if ("admin".equals(druidUsername)) {
            warnings.add("Druid监控台使用默认用户名，建议修改为复杂用户名");
        }

        if (SecurityValidationUtils.isCommonWeakPassword(druidPassword)) {
            errors.add("Druid监控台使用弱密码，存在安全风险");
        }

        if (StringUtils.isBlank(allowIps) || "".equals(allowIps.trim())) {
            errors.add("Druid监控台允许所有IP访问，这是严重的安全风险！");
        }
    }

    /**
     * 验证HTTPS配置
     */
    private void validateHttpsConfig(List<String> warnings, List<String> errors) {
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = false;
        
        for (String profile : activeProfiles) {
            if ("prod".equals(profile) || "production".equals(profile)) {
                isProduction = true;
                break;
            }
        }

        if (isProduction) {
            Boolean sslEnabled = environment.getProperty("server.ssl.enabled", Boolean.class);
            if (sslEnabled == null || !sslEnabled) {
                warnings.add("生产环境未启用HTTPS，建议配置SSL证书");
            }
        }

        // 检查session cookie安全配置
        Boolean cookieSecure = environment.getProperty("server.servlet.session.cookie.secure", Boolean.class);
        if (cookieSecure == null || !cookieSecure) {
            warnings.add("会话Cookie未设置Secure标志");
        }

        Boolean cookieHttpOnly = environment.getProperty("server.servlet.session.cookie.http-only", Boolean.class);
        if (cookieHttpOnly == null || !cookieHttpOnly) {
            warnings.add("会话Cookie未设置HttpOnly标志");
        }
    }

    /**
     * 报告检查结果
     */
    private void reportResults(List<String> warnings, List<String> errors) {
        if (errors.isEmpty() && warnings.isEmpty()) {
            log.info("✅ 安全配置检查通过，未发现安全问题");
            return;
        }

        if (!errors.isEmpty()) {
            log.error("❌ 发现 {} 个严重安全问题：", errors.size());
            for (int i = 0; i < errors.size(); i++) {
                log.error("  {}. {}", i + 1, errors.get(i));
            }
        }

        if (!warnings.isEmpty()) {
            log.warn("⚠️  发现 {} 个安全建议：", warnings.size());
            for (int i = 0; i < warnings.size(); i++) {
                log.warn("  {}. {}", i + 1, warnings.get(i));
            }
        }

        if (!errors.isEmpty()) {
            log.error("❌ 请立即修复上述安全问题后重新启动应用！");
            log.error("💡 参考安全配置模板：application-prod-template.yml");
            log.error("📖 详细安全指南：SECURITY-ASSESSMENT.md");
        }
    }
}