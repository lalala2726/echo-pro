package cn.zhangchuangla.admin.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DruidProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    // 其他Druid配置属性，可根据需要添加
    private int initialSize = 5;
    private int minIdle = 10;
    private int maxActive = 20; // 提供默认值
    private long maxWait = 60000;

    public DruidDataSource dataSource(DruidDataSource datasource) {
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        // 配置初始化大小、最小、最大
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        // 确保maxActive不为0
        datasource.setMaxActive(maxActive <= 0 ? 20 : maxActive);
        datasource.setMaxWait(maxWait);

        // 可以添加更多配置

        return datasource;
    }

    // Getter and Setter methods
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }
}
