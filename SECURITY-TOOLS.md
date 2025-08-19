# 安全工具使用说明

## 概述

本文档介绍了 EchoPro 项目中新增的安全工具和验证功能，帮助开发者提升应用程序的安全性。

## 新增安全工具

### 1. SecurityValidationUtils - 安全验证工具类

位置：`chuang-common-core/src/main/java/cn/zhangchuangla/common/core/utils/SecurityValidationUtils.java`

#### 功能特性

- **密码强度检查**：评估密码安全等级
- **JWT密钥验证**：检查JWT密钥是否符合安全要求  
- **安全随机数生成**：生成安全的密码和密钥
- **弱密码检测**：识别常见的弱密码

#### 使用示例

```java
// 检查密码强度
PasswordStrength strength = SecurityValidationUtils.checkPasswordStrength("MyPassword123!");
if (strength == PasswordStrength.WEAK) {
    // 提示用户使用更强的密码
}

// 验证JWT密钥
boolean isSecure = SecurityValidationUtils.isSecureJwtSecret(jwtSecret);
if (!isSecure) {
    // 警告：JWT密钥不安全
}

// 生成安全的JWT密钥（32位）
String secureSecret = SecurityValidationUtils.generateSecureJwtSecret(32);

// 生成安全密码
String password = SecurityValidationUtils.generateSecurePassword(12, true);

// 检查是否为常见弱密码
boolean isWeak = SecurityValidationUtils.isCommonWeakPassword("123456");
```

### 2. SecurityStartupValidator - 安全配置验证器

位置：`chuang-framework/src/main/java/cn/zhangchuangla/framework/config/SecurityStartupValidator.java`

#### 功能特性

- **自动安全检查**：应用启动时自动检查配置
- **多层面验证**：JWT、数据库、Redis、密码策略等
- **风险分级**：区分错误和警告级别
- **详细报告**：提供具体的安全建议

#### 检查项目

1. **JWT密钥安全性**
   - 密钥长度检查（至少32位）
   - 复杂度验证（熵值计算）
   - 重复模式检测

2. **数据库配置**
   - 密码强度验证
   - SSL连接检查

3. **Redis配置**
   - 密码设置检查
   - 弱密码检测

4. **密码策略**
   - 重试次数限制
   - 锁定时间配置

5. **Druid监控台**
   - 访问控制验证
   - 凭据强度检查

6. **HTTPS配置**
   - SSL启用检查
   - Cookie安全标志

### 3. 增强的Quartz任务安全

#### 改进内容

**严格的白名单控制**：
```java
// 旧配置（过于宽泛）
public static final String[] JOB_WHITELIST_STR = {"cn.zhangchuangla"};

// 新配置（精确控制）
public static final String[] JOB_WHITELIST_STR = {
    "cn.zhangchuangla.quartz.task",
    "cn.zhangchuangla.system.job", 
    "cn.zhangchuangla.system.task"
};
```

**扩展的危险字符检查**：
- 网络访问类（URL、Socket等）
- 文件系统操作类
- 系统调用类（Runtime、ProcessBuilder）
- 反射操作类
- 脚本引擎类

**增强的类名验证**：
- Java类名格式验证
- 包名白名单检查
- 长度限制（防止过长类名攻击）
- 连续字符检查

## 配置安全化

### 1. 生产环境配置模板

使用提供的安全配置模板：
```bash
cp chuang-api/src/main/resources/application-prod-template.yml application-prod.yml
```

### 2. 环境变量配置

#### 必需的环境变量

```bash
# JWT密钥（至少32位）
export JWT_SECRET="your-secure-jwt-secret-key-at-least-32-characters"

# 数据库凭据
export DATABASE_PASSWORD="your-secure-database-password"

# Redis密码
export REDIS_PASSWORD="your-secure-redis-password"

# Druid监控台凭据
export DRUID_USERNAME="your-admin-username"
export DRUID_PASSWORD="your-secure-admin-password"
```

#### 推荐的环境变量

```bash
# 网络配置
export SERVER_PORT=8080
export SSL_ENABLED=true

# 安全策略
export PASSWORD_MAX_RETRY=5
export PASSWORD_LOCK_TIME=300

# 监控配置
export DRUID_MONITOR_ENABLED=false  # 生产环境建议关闭
export API_DOCS_ENABLED=false       # 生产环境建议关闭
```

### 3. Docker部署安全

#### Dockerfile安全配置

```dockerfile
# 使用非root用户
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 设置文件权限
COPY --chown=appuser:appgroup target/app.jar app.jar

# 切换到非特权用户
USER appuser

# 暴露端口
EXPOSE 8080
```

#### Docker Compose环境变量

```yaml
version: '3.8'
services:
  echo-pro:
    image: echo-pro:latest
    environment:
      - JWT_SECRET=${JWT_SECRET}
      - DATABASE_PASSWORD=${DATABASE_PASSWORD}
      - REDIS_PASSWORD=${REDIS_PASSWORD}
    ports:
      - "8080:8080"
    user: "1001:1001"  # 非root用户运行
```

## 安全最佳实践

### 1. 密钥管理

```java
// ❌ 错误：硬编码密钥
String secret = "hardcoded-secret";

// ✅ 正确：从环境变量读取
String secret = System.getenv("JWT_SECRET");
if (!SecurityValidationUtils.isSecureJwtSecret(secret)) {
    throw new IllegalArgumentException("JWT密钥不符合安全要求");
}
```

### 2. 密码验证

```java
// 用户注册时验证密码强度
public void validatePassword(String password) {
    PasswordStrength strength = SecurityValidationUtils.checkPasswordStrength(password);
    
    if (strength == PasswordStrength.WEAK) {
        throw new ValidationException("密码强度太弱，请使用更复杂的密码");
    }
    
    if (SecurityValidationUtils.isCommonWeakPassword(password)) {
        throw new ValidationException("不能使用常见的弱密码");
    }
}
```

### 3. 定时任务安全

```java
// 在添加定时任务前验证
public void addJob(SysJob job) {
    String invokeTarget = job.getInvokeTarget();
    
    // 验证任务目标是否安全
    if (JobInvokeUtil.whiteList(invokeTarget)) {
        throw new SecurityException("任务目标不在安全白名单中");
    }
    
    // 其他业务逻辑...
}
```

## 监控和日志

### 1. 安全事件监控

系统会自动记录以下安全事件：
- 密码重试超限
- 登录频率异常
- JWT令牌验证失败
- 权限检查失败
- 定时任务执行异常

### 2. 日志配置

```yaml
logging:
  level:
    cn.zhangchuangla.framework.security: DEBUG
    cn.zhangchuangla.framework.config.SecurityStartupValidator: INFO
```

## 常见问题

### Q: 应用启动时出现安全警告怎么办？

A: 根据启动日志中的具体警告信息，参考 `SECURITY-ASSESSMENT.md` 文档中的建议进行配置调整。

### Q: 如何生成安全的JWT密钥？

A: 使用工具类生成：
```java
String secureSecret = SecurityValidationUtils.generateSecureJwtSecret(64);
System.out.println("JWT密钥: " + secureSecret);
```

### Q: 定时任务白名单如何配置？

A: 在 `QuartzConstants.java` 中修改 `JOB_WHITELIST_STR` 数组，只包含需要的具体包名。

### Q: 如何在生产环境禁用不必要的功能？

A: 使用环境变量控制：
```bash
export API_DOCS_ENABLED=false
export SWAGGER_UI_ENABLED=false
export DRUID_MONITOR_ENABLED=false
```

## 支持与反馈

如果在使用过程中遇到问题或有安全建议，请：
1. 查看 `SECURITY-ASSESSMENT.md` 详细文档
2. 提交Issue到项目仓库
3. 联系项目维护团队

---

*定期审查和更新安全配置是保障系统安全的重要措施*