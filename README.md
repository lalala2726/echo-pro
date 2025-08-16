# EchoPro 企业级后台管理系统

<div align="center">


![EchoPro Logo](https://img.shields.io/badge/EchoPro-v1.0.0-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)
![Build Status](https://img.shields.io/badge/Build-Passing-success.svg)

**基于 Spring Boot 3 的现代化后台管理系统**

注意：本项目还在开发阶段！许多功能尚未完全测试，计划在 **十月中旬** 开发完毕。

## 在线体验

[在线演示](https://echo.zhangchuangla.cn.cn) | [前端项目](https://github.com/lalala2726/echoPro-front-end)  

**测试账号：** `demo`  
**测试密码：** `admin123`


# 项目截图

| ![截图1](https://oss.zhangchuangla.cn/blog/1755331364411.png) | ![截图2](https://oss.zhangchuangla.cn/blog/1755331368228.png) |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![截图3](https://oss.zhangchuangla.cn/blog/1755331357294.png) | ![截图4](https://oss.zhangchuangla.cn/blog/1755331344075.png) |
| ![截图5](https://oss.zhangchuangla.cn/blog/1755331349664.png) | ![截图6](https://oss.zhangchuangla.cn/blog/1755331396660.png) |
| ![截图7](https://oss.zhangchuangla.cn/blog/1755331371779.png) | ![截图8](https://oss.zhangchuangla.cn/blog/1755331379649.png) |
| ![截图9](https://oss.zhangchuangla.cn/blog/1755331375845.png) | ![截图10](https://oss.zhangchuangla.cn/blog/1755331406584.png) |
| ![截图11](https://oss.zhangchuangla.cn/blog/1755331410145.png) | ![截图12](https://oss.zhangchuangla.cn/blog/1755331419025.png) |
| ![截图13](https://oss.zhangchuangla.cn/blog/1755331433209.png) | ![截图14](https://oss.zhangchuangla.cn/blog/1755331455539.png) |

## 项目简介

EchoPro 是一套面向企业级应用的现代化后台管理系统，采用前后端分离架构，聚焦「**安全、可扩展、可维护**」的设计理念。系统提供完整的用户权限管理、统一身份认证、文件存储、任务调度、系统监控等企业级功能，适配多种业务场景和部署环境。

###  核心特性

- **模块化架构**：清晰的分层设计，API/Framework/System/Common/Quartz 模块解耦，支持按需组合
- **安全认证**：基于 Spring Security 6 + JWT 的无状态认证，支持多设备会话管理和权限控制
- **多存储支持**：统一存储接口，支持本地、MinIO、阿里云OSS、腾讯云COS、AWS S3 等多种存储方式
- **系统监控**：集成 Actuator + Micrometer + Prometheus，提供完整的系统监控和指标采集
- **高性能**：Redis 缓存、连接池优化、异步处理，支持高并发场景
- **开发友好**：丰富的工具类、注解支持，提升开发效率
- **任务调度**：基于 Quartz 的分布式任务调度，支持集群部署

### 主要功能

| 功能模块     | 功能描述                                               |
| ------------ | ------------------------------------------------------ |
| **用户管理** | 用户信息管理、用户状态控制、用户角色分配、部门组织架构 |
| **权限管理** | 角色权限分配、菜单权限控制、按钮级权限、数据权限       |
| **组织架构** | 部门管理、岗位管理、组织树形结构                       |
| **菜单管理** | 动态菜单配置、菜单权限控制、菜单图标管理               |
| **字典管理** | 系统字典配置、字典缓存管理、字典数据维护               |
| **文件管理** | 文件上传下载、多存储源支持、文件预览、回收站           |
| **消息通知** | 站内消息、系统通知、消息推送、WebSocket实时通信        |
| **任务调度** | 定时任务管理、任务执行监控、Cron表达式配置             |
| **系统监控** | 服务器监控、JVM监控、接口监控、性能指标                |
| **日志管理** | 操作日志、登录日志、异常日志、安全日志                 |

## 系统架构

### 技术栈

| 技术分类     | 技术选型              | 版本   |
| ------------ | --------------------- | ------ |
| **后端框架** | Spring Boot           | 3.4.3  |
| **安全框架** | Spring Security       | 6.4.4  |
| **认证方案** | JWT                   | -      |
| **ORM框架**  | MyBatis-Plus          | 3.5.10 |
| **数据库**   | MySQL                 | 8.0+   |
| **缓存**     | Redis                 | 6.0+   |
| **连接池**   | Druid                 | 1.2.24 |
| **任务调度** | Quartz                | 2.5.0  |
| **文档工具** | SpringDoc OpenAPI     | 2.8.3  |
| **监控工具** | Actuator + Micrometer | -      |
| **消息队列** | RabbitMQ              | 3.8+   |
| **对象存储** | MinIO/OSS/COS/S3      | -      |

### 模块结构

| 模块                       | 说明         |
| -------------------------- | ------------ |
| `chuang-api`               | 应用入口层，包含 REST 控制器、配置、启动类 |
| `chuang-framework`         | 框架核心层，含安全认证、拦截器、自定义注解 |
| `chuang-system-core`       | 业务核心模块，实体、服务、数据访问、模型   |
| `chuang-system-storage`    | 文件存储模块 |
| `chuang-system-message`    | 消息通知模块 |
| `chuang-system-monitor`    | 系统监控模块 |
| `chuang-common-core`       | 公共核心工具 |
| `chuang-common-redis`      | Redis 工具   |
| `chuang-common-excel`      | Excel 工具   |
| `chuang-common-mq`         | 消息队列工具 |
| `chuang-common-websocket`  | WebSocket 工具 |
| `chuang-quartz`            | 任务调度层，含执行器、调度服务、配置 |
## 快速开始

### 环境要求

| 软件     | 版本要求 | 说明 |
| -------- | -------- | ---- |
| JDK      | 17+      | 必需 |
| Maven    | 3.6+     | 必需 |
| MySQL    | 8.0+     | 必需 |
| Redis    | 6.0+     | 必需 |
| RabbitMQ | 3.8+     | 可选 |
| MinIO    | Latest   | 可选 |

**注:如果不选择RabbitMQ请在系统消息模块给指定用户发送消息调整相关的代码**

### 安装部署

#### 1. 克隆项目

```bash
git clone https://github.com/lalala2726/echo-pro.git
cd echoPro
```

#### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE echo_pro DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入初始化脚本（请根据实际情况调整）
-- source docs/sql/echo_pro.sql
```

#### 3. 配置文件

修改 `chuang-api/src/main/resources/application.yml` 中的数据库和Redis配置：

```yaml
spring:
  data:
    redis:
      host: 192.168.10.110
      port: 6379
      database: 0
      password: your_redis_password
```

修改 `application-druid.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    druid:
      url: jdbc:mysql://localhost:3306/echo_pro?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
      username: your_username
      password: your_password
```

#### 4. 编译运行

```bash
# 编译项目
mvn clean package -DskipTests

# 运行项目
java -jar chuang-api/target/chuang-api.jar
```

#### 5. 访问系统

启动成功后，可通过以下地址访问：

| 服务           | 地址                                  | 说明                 |
| -------------- | ------------------------------------- | -------------------- |
| **API文档**    | http://localhost:8080/swagger-ui.html | Swagger UI           |
| **监控面板**   | http://localhost:8080/actuator        | Spring Boot Actuator |
| **数据库监控** | http://localhost:8080/druid           | Druid监控面板        |
| **健康检查**   | http://localhost:8080/actuator/health | 系统健康状态         |

## 详细功能说明

### 安全认证模块 (chuang-framework)

**核心功能：**

- JWT无状态认证，支持访问令牌和刷新令牌
- 多设备会话管理，支持PC、移动端、小程序等
- 基于注解的权限控制 (`@PreAuthorize`, `@Anonymous`)
- 密码重试限制和登录频率限制
- 安全日志记录和异常监控

**主要组件：**

- `TokenService`: JWT令牌管理服务
- `AuthService`: 用户认证服务
- `PermissionAuth`: 权限验证组件
- `SecurityConfig`: Spring Security配置
- `TokenAuthenticationFilter`: JWT认证过滤器

### 用户权限模块 (chuang-system-core)

**核心功能：**

- 用户信息管理（增删改查、状态控制）
- 角色权限分配（角色创建、权限绑定）
- 部门组织架构（树形结构、层级管理）
- 菜单权限控制（动态菜单、按钮权限）
- 字典数据管理（系统配置、缓存优化）

### 文件存储模块 (chuang-system-storage)

**核心功能：**

- 多存储源支持（本地、MinIO、阿里云OSS、腾讯云COS、AWS S3）
- 统一存储接口，一键切换存储方式
- 文件上传下载、预览、压缩
- 回收站机制，支持文件恢复
- 文件类型检测和安全校验

**存储配置示例：**

```yaml
storage:
  active-type: minio  # local|minio|aliyun_oss|tencent_cos|amazon_s3
  minio:
    endpoint: http://127.0.0.1:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: echo-pro
```

### 消息通知模块 (chuang-system-message)

**核心功能：**

- 站内消息系统（用户消息、系统通知）
- 消息分类管理（个人消息、角色消息、部门消息、全员消息）
- WebSocket实时推送
- 消息状态管理（已读、未读、删除）
- 消息模板和批量发送

### 任务调度模块 (chuang-quartz)

**核心功能：**

- 基于Quartz的分布式任务调度
- 支持Cron表达式、固定频率、固定延迟、一次性执行
- 任务执行监控和日志记录
- 集群部署支持，任务负载均衡
- 任务执行时间同步和状态管理

**任务配置示例：**

```java
@Component
public class DataBackupTask {
    public void execute() {
        // 任务执行逻辑
        log.info("执行数据备份任务");
    }
}
```

### 系统监控模块 (chuang-system-monitor)

**核心功能：**

- 系统资源监控（CPU、内存、磁盘）
- JVM监控（堆内存、GC、线程）
- 接口访问统计和性能监控
- Prometheus指标导出
- 健康检查和告警机制

### 公共工具模块 (chuang-common)

**chuang-common-core:**

- 统一响应结果封装 (`AjaxResult`)
- 基础实体类 (`BaseEntity`, `BaseVo`)
- 常用工具类（字符串、日期、加密等）
- 数据脱敏注解 (`@DataMasking`)
- 统一异常处理

**chuang-common-redis:**

- Redis缓存操作封装 (`RedisCache`)
- Redis Set/ZSet 操作工具
- 分布式锁实现
- 缓存注解支持

**chuang-common-excel:**

- Excel导入导出功能
- 自定义注解配置 (`@Excel`)
- 数据验证和格式化
- 大数据量处理优化

**chuang-common-mq:**

- RabbitMQ消息队列封装
- 消息发送和接收工具
- 死信队列处理
- 消息重试机制

**chuang-common-websocket:**

- WebSocket连接管理
- 实时消息推送
- 用户会话管理
- 心跳检测机制

##  配置说明

### 多环境配置

系统采用Spring Boot的Profile机制，支持多环境配置：

```yaml
spring:
  profiles:
    active: druid,rabbitmq,quartz,monitor,storage,cors
```

### 配置文件说明

| 配置文件                   | 说明             |
| -------------------------- | ---------------- |
| `application.yml`          | 主配置文件       |
| `application-druid.yml`    | 数据库连接池配置 |
| `application-rabbitmq.yml` | 消息队列配置     |
| `application-quartz.yml`   | 任务调度配置     |
| `application-monitor.yml`  | 监控配置         |
| `application-storage.yml`  | 文件存储配置     |
| `application-cors.yml`     | 跨域配置         |

### 安全配置

```yaml
security:
  secret: your_jwt_secret_key  # JWT密钥
  header: Authorization        # 认证头
  password-config:
    lock-time: 3              # 密码错误锁定时间(分钟)
    max-retry-count: 5        # 最大重试次数
  session:
    access-token-expire-time: 7200000   # 访问令牌过期时间(毫秒)
    refresh-token-expire-time: 2592000  # 刷新令牌过期时间(秒)
    multi-device: true                  # 是否支持多设备登录
```

### 存储配置

```yaml
storage:
  active-type: local  # 存储类型: local|minio|aliyun_oss|tencent_cos|amazon_s3
  local:
    upload-path: /data/upload
    file-domain: http://localhost:8080/
  minio:
    endpoint: http://127.0.0.1:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: echo-pro
  aliyun-oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    access-key: your_access_key
    secret-key: your_secret_key
    bucket-name: your_bucket
```

### 任务调度配置

```yaml
spring:
  quartz:
    job-store-type: jdbc
    wait-for-jobs-to-complete-on-shutdown: false
    overwrite-existing-jobs: true
    auto-startup: true
    properties:
      org:
        quartz:
          scheduler:
            instanceName: QuartzScheduler
            instanceId: AUTO
          jobStore:
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            tablePrefix: QRTZ_
            isClustered: true
            clusterCheckinInterval: 15000
```

## API文档

### Swagger UI

系统集成了SpringDoc OpenAPI，提供完整的API文档：

- **访问地址**: http://localhost:8080/swagger-ui.html
- **API JSON**: http://localhost:8080/v3/api-docs

### 主要API接口

| 模块         | 接口路径             | 说明                 |
| ------------ | -------------------- | -------------------- |
| **认证模块** | `/auth/**`           | 登录、注册、令牌刷新 |
| **用户管理** | `/system/user/**`    | 用户CRUD操作         |
| **角色管理** | `/system/role/**`    | 角色权限管理         |
| **部门管理** | `/system/dept/**`    | 组织架构管理         |
| **菜单管理** | `/system/menu/**`    | 菜单权限配置         |
| **文件管理** | `/system/storage/**` | 文件上传下载         |
| **消息通知** | `/system/message/**` | 站内消息管理         |
| **任务调度** | `/system/job/**`     | 定时任务管理         |
| **系统监控** | `/monitor/**`        | 系统监控指标         |

##  开发指南

### 开发环境搭建

#### 1. IDE配置

推荐使用IntelliJ IDEA，安装以下插件：

- MyBatis X
- Mybatis Log Free
- Alibaba Java Coding Guidelines

#### 2. 代码规范

项目遵循阿里巴巴Java开发手册：

- 使用UTF-8编码
- 缩进使用4个空格
- 类名使用UpperCamelCase
- 方法名使用lowerCamelCase
- 常量使用UPPER_SNAKE_CASE

#### 3. 分支管理

```bash
# 主分支
master    # 生产环境分支
dev   # 开发环境分支
```

### 新增模块开发

#### 1. 创建模块结构

```
chuang-system-new/
├── src/main/java/cn/zhangchuangla/system/new/
│   ├── entity/           # 实体类
│   ├── mapper/           # 数据访问层
│   ├── service/          # 业务逻辑层
│   ├── model/            # 数据传输对象
│   └── config/           # 模块配置
├── src/main/resources/
│   └── mapper/           # MyBatis映射文件
└── pom.xml               # 模块依赖
```

#### 2. 添加依赖

```xml
<dependency>
    <groupId>cn.zhangchuangla</groupId>
    <artifactId>chuang-common-core</artifactId>
</dependency>
```

#### 3. 注册模块

在主模块的pom.xml中添加：

```xml
<modules>
    <module>chuang-system-new</module>
</modules>
```

## 贡献指南

### 如何贡献

1. **Fork项目**: 点击右上角Fork按钮
2. **创建分支**: `git checkout -b feature/your-feature`
3. **提交代码**: `git commit -m "feat: add your feature"`
4. **推送分支**: `git push origin feature/your-feature`
5. **创建PR**: 在GitHub上创建Pull Request

### 提交规范

使用Conventional Commits规范：

```bash
# 功能开发
git commit -m "feat: 添加用户管理功能"

# 问题修复
git commit -m "fix: 修复登录验证码问题"

# 文档更新
git commit -m "docs: 更新API文档"

# 代码重构
git commit -m "refactor: 重构权限验证逻辑"

# 性能优化
git commit -m "perf: 优化数据库查询性能"

# 测试相关
git commit -m "test: 添加用户服务单元测试"
```

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 开源协议。

## 致谢

感谢以下开源项目的支持：

- [vue-vben-admin](https://github.com/vbenjs/vue-vben-admin)
- [ruoyi](https://www.ruoyi.vip/) 

##  联系我

- **作者**: Chuang
- **邮箱**: chuang@zhangchuangla.cn
- **项目地址**: [GitHub - EchoPro](https://github.com/lalala2726/echo-pro)
- **问题反馈**: [Issues](https://github.com/lalala2726/echo-pro/issues)

## Star History

如果这个项目对您有帮助，请给我们一个Star ⭐
