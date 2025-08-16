# EchoPro 企业级后台管理系统

<div align="center">

<img src="https://img.shields.io/badge/EchoPro-v1.0.0-blue.svg" alt="EchoPro Version">
<img src="https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg" alt="Spring Boot">
<img src="https://img.shields.io/badge/Java-17+-orange.svg" alt="Java 17">
<img src="https://img.shields.io/badge/License-Apache%202.0-green.svg" alt="License">
<img src="https://img.shields.io/badge/Build-Passing-success.svg" alt="Build Status">

**基于 Spring Boot 3 的现代化企业后台管理系统**

</div>

> 系统目前还处于快速开发阶段，仍存在较多 Bug。核心功能预计在 **十月中旬** 完成，部分模块尚未经过充分测试，接口和结构也可能会有调整。


## 目录

- [在线体验](#在线体验)
- [项目截图](#项目截图)
- [项目简介](#项目简介)
  - [核心特性](#核心特性)
  - [主要功能](#主要功能)
- [系统架构](#系统架构)
  - [技术栈](#技术栈)
  - [模块结构](#模块结构)
- [快速开始](#快速开始)
  - [环境要求](#环境要求)
  - [安装部署](#安装部署)
  - [访问系统](#访问系统)
- [详细功能说明](#详细功能说明)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [开发指南](#开发指南)
- [贡献指南](#贡献指南)
- [许可证](#许可证)
- [致谢](#致谢)
- [联系我](#联系我)
- [Star History](#star-history)

---

## 在线体验

[在线演示](https://echo.zhangchuangla.cn) ｜ [前端项目仓库](https://github.com/lalala2726/echoPro-front-end)

**测试账号**：`demo`  
**测试密码**：`admin123`

---

## 项目截图

| ![截图1](https://oss.zhangchuangla.cn/blog/1755331364411.png) | ![截图2](https://oss.zhangchuangla.cn/blog/1755331368228.png) |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![截图3](https://oss.zhangchuangla.cn/blog/1755331357294.png) | ![截图4](https://oss.zhangchuangla.cn/blog/1755331344075.png) |
| ![截图5](https://oss.zhangchuangla.cn/blog/1755331349664.png) | ![截图6](https://oss.zhangchuangla.cn/blog/1755331396660.png) |
| ![截图7](https://oss.zhangchuangla.cn/blog/1755331371779.png) | ![截图8](https://oss.zhangchuangla.cn/blog/1755331379649.png) |
| ![截图9](https://oss.zhangchuangla.cn/blog/1755331375845.png) | ![截图10](https://oss.zhangchuangla.cn/blog/1755331406584.png) |
| ![截图11](https://oss.zhangchuangla.cn/blog/1755331410145.png) | ![截图12](https://oss.zhangchuangla.cn/blog/1755331419025.png) |
| ![截图13](https://oss.zhangchuangla.cn/blog/1755331433209.png) | ![截图14](https://oss.zhangchuangla.cn/blog/1755331455539.png) |

---

## 项目简介

EchoPro 是一套面向企业级场景的现代化后台管理系统，采用前后端分离与模块化架构，聚焦 安全 / 可扩展 / 可维护 / 可观测 四大能力。内置权限体系、统一认证、文件存储、消息通知、任务调度、系统监控等通用基础能力。

### 核心特性

- 模块化架构：API / Framework / System / Quartz / Common 解耦，便于扩展裁剪
- 认证鉴权：Spring Security 6 + JWT，无状态与多端会话支持
- 多存储适配：本地 / MinIO / 阿里 OSS / 腾讯 COS / AWS S3 统一抽象
- 监控与可观测：Actuator + Micrometer + Prometheus 指标采集与健康检查
- 高性能：Redis 缓存、异步化、连接池优化
- 开发友好：统一响应、异常规范、工具与注解
- 分布式调度：Quartz 集群任务调度与执行监控
- 消息能力：RabbitMQ + WebSocket 实时推送

### 主要功能

| 功能模块 | 描述 |
| -------- | ---- |
| 用户管理 | 用户 CRUD、状态控制、角色绑定、组织归属 |
| 权限管理 | 角色权限、菜单/按钮/数据权限 |
| 组织架构 | 部门、岗位、树形结构 |
| 菜单管理 | 动态菜单、前端路由元数据、图标权限 |
| 字典管理 | 字典项维护、缓存加速 |
| 文件管理 | 多存储源、上传下载、预览、回收站、安全校验 |
| 消息通知 | 站内消息、公告、分组推送、WebSocket 实时 |
| 任务调度 | Cron/简单/一次性任务、执行日志、集群支持 |
| 系统监控 | 服务器/JVM/接口性能/业务指标 |
| 日志管理 | 操作、登录、异常、安全审计 |

---

## 系统架构

### 技术栈

| 分类     | 技术                   | 版本    |
| -------- | ---------------------- | ------- |
| 后端框架 | Spring Boot            | 3.4.3   |
| 安全     | Spring Security        | 6.4.4   |
| 认证     | JWT                    | -       |
| ORM      | MyBatis-Plus           | 3.5.10  |
| 数据库   | MySQL                  | 8.0+    |
| 缓存     | Redis                  | 6.0+    |
| 连接池   | Druid                  | 1.2.24  |
| 调度     | Quartz                 | 2.5.0   |
| 文档     | SpringDoc OpenAPI      | 2.8.3   |
| 监控     | Actuator + Micrometer  | -       |
| 消息队列 | RabbitMQ               | 3.8+    |
| 对象存储 | MinIO / OSS / COS / S3 | -       |

### 模块结构

| 模块                      | 说明 |
| ------------------------- | ---- |
| `chuang-api`              | 应用入口（Controller / 配置 / 启动） |
| `chuang-framework`        | 框架核心（安全、拦截器、注解） |
| `chuang-system-core`      | 业务核心（实体 / 服务 / Mapper / 模型） |
| `chuang-system-storage`   | 文件存储模块 |
| `chuang-system-message`   | 消息通知模块 |
| `chuang-system-monitor`   | 系统监控模块 |
| `chuang-common-core`      | 公共工具与基础封装 |
| `chuang-common-redis`     | Redis 操作与缓存封装 |
| `chuang-common-excel`     | Excel 导入导出 |
| `chuang-common-mq`        | MQ 封装（RabbitMQ） |
| `chuang-common-websocket` | WebSocket 封装 |
| `chuang-quartz`           | 任务调度层（调度器 / 执行器 / 配置） |

---

## 快速开始

### 环境要求

| 组件     | 版本要求 | 必需 | 说明 |
| -------- | -------- | ---- | ---- |
| JDK      | 17+      | 是   | 运行环境 |
| Maven    | 3.6+     | 是   | 构建工具 |
| MySQL    | 8.0+     | 是   | 关系数据库 |
| Redis    | 6.0+     | 是   | 缓存与分布式锁 |
| RabbitMQ | 3.8+     | 否   | 消息通知（不开启需调整） |
| MinIO    | Latest   | 否   | 对象存储测试 |

> 如果不使用 RabbitMQ，请在消息模块中屏蔽相关依赖与发送逻辑。

### 安装部署

#### 1. 克隆代码

```bash
git clone https://github.com/lalala2726/echo-pro.git
cd echo-pro
```

#### 2. 初始化数据库

```sql
CREATE DATABASE echo_pro DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- source docs/sql/echo_pro.sql
```

#### 3. 修改配置

`chuang-api/src/main/resources/application.yml`

```yaml
spring:
  data:
    redis:
      host: 192.168.10.110
      port: 6379
      database: 0
      password: your_redis_password
```

`application-druid.yml`

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
mvn clean package -DskipTests
java -jar chuang-api/target/chuang-api.jar
```

#### 5. 访问系统

| 功能       | 地址                                  | 描述          |
| ---------- | ------------------------------------- | ------------- |
| API 文档   | http://localhost:8080/swagger-ui.html | Swagger UI    |
| 监控入口   | http://localhost:8080/actuator        | Actuator 总览 |
| 数据库监控 | http://localhost:8080/druid           | Druid 监控    |
| 健康检查   | http://localhost:8080/actuator/health | Health 状态   |

---

## 详细功能说明

### 安全认证（chuang-framework）

- JWT 访问/刷新令牌
- 多终端会话策略（可配置是否互斥）
- 注解式权限：`@PreAuthorize` / `@Anonymous`
- 登录重试与锁定策略
- 安全日志与异常捕获

主要组件：
- `TokenService`
- `AuthService`
- `PermissionAuth`
- `SecurityConfig`
- `TokenAuthenticationFilter`

### 用户权限（chuang-system-core）

- 用户 / 角色 / 部门 / 菜单 / 字典
- 按钮级与数据级权限
- 缓存优化与批量操作

### 文件存储（chuang-system-storage）

- 多存储策略实现
- 上传 / 下载 / 预览 / 回收站 / 类型校验
- 支持 MinIO / OSS / COS / S3 / Local

示例：

```yaml
storage:
  active-type: minio
  minio:
    endpoint: http://127.0.0.1:9000
    access-key: minioadmin
    secret-key: minioadmin
    bucket-name: echo-pro
```

### 消息通知（chuang-system-message）

- 站内信 / 公告 / 广播
- 用户 / 角色 / 部门 / 全员推送
- WebSocket 实时
- 已读 / 未读 / 删除状态

### 任务调度（chuang-quartz）

- Cron / 固定频率 / 一次性任务
- 集群模式
- 执行日志与失败策略
- 任务启停 / 暂停 / 立即执行

### 系统监控（chuang-system-monitor）

- 服务器资源
- JVM 指标
- 接口耗时统计
- Prometheus 指标导出
- 健康检查

### 公共工具（chuang-common-*）

- 统一响应：`AjaxResult`
- 基础实体：`BaseEntity` / `BaseVo`
- 数据脱敏：`@DataMasking`
- Redis 缓存 & 分布式锁
- Excel 导入导出：`@Excel`
- MQ 封装
- WebSocket 统一会话管理

---

## 配置说明

### 多 Profile 激活

```yaml
spring:
  profiles:
    active: druid,rabbitmq,quartz,monitor,storage,cors
```

### 主要配置文件

| 文件                       | 说明         |
| -------------------------- | ------------ |
| `application.yml`          | 主配置       |
| `application-druid.yml`    | 数据源配置   |
| `application-rabbitmq.yml` | 消息队列配置 |
| `application-quartz.yml`   | 调度配置     |
| `application-monitor.yml`  | 监控配置     |
| `application-storage.yml`  | 存储配置     |
| `application-cors.yml`     | 跨域配置     |

### 安全配置示例

```yaml
security:
  secret: your_jwt_secret_key
  header: Authorization
  password-config:
    lock-time: 3
    max-retry-count: 5
  session:
    access-token-expire-time: 7200000
    refresh-token-expire-time: 2592000
    multi-device: true
```

### Quartz 配置

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

---

## API 文档

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

| 模块   | 路径前缀              | 描述           |
| ------ | --------------------- | -------------- |
| 认证   | `/auth/**`            | 登录 / 注册 / 刷新 |
| 用户   | `/system/user/**`     | 用户管理       |
| 角色   | `/system/role/**`     | 权限授权       |
| 部门   | `/system/dept/**`     | 组织结构       |
| 菜单   | `/system/menu/**`     | 菜单权限       |
| 文件   | `/system/storage/**`  | 文件处理       |
| 消息   | `/system/message/**`  | 消息通知       |
| 调度   | `/system/job/**`      | 定时任务       |
| 监控   | `/monitor/**`         | 运行指标       |

---

## 开发指南

### IDE 推荐

- IntelliJ IDEA
- 建议插件：MyBatis X、Mybatis Log Free、Alibaba Java Coding Guidelines

### 代码规范

- 编码：UTF-8
- 缩进：4 空格
- 类名：UpperCamelCase
- 方法名：lowerCamelCase
- 常量：UPPER_SNAKE_CASE
- 提交信息：遵循 Conventional Commits

### 分支策略

```
master  # 生产
dev     # 开发主干
```

### 新增业务模块示例

目录结构：

```
chuang-system-new/
├── src/main/java/cn/zhangchuangla/system/new/
│   ├── entity/
│   ├── mapper/
│   ├── service/
│   ├── model/
│   └── config/
├── src/main/resources/mapper/
└── pom.xml
```

依赖示例：

```xml
<dependency>
  <groupId>cn.zhangchuangla</groupId>
  <artifactId>chuang-common-core</artifactId>
</dependency>
```

聚合：

```xml
<modules>
  <module>chuang-system-new</module>
</modules>
```

---

## 贡献指南

流程：

1. Fork 仓库
2. 创建分支：`git checkout -b feature/xxx`
3. 提交代码：`git commit -m "feat: 描述"`
4. 推送：`git push origin feature/xxx`
5. 发起 Pull Request

提交格式示例：

```
feat: 添加用户管理功能
fix: 修复登录验证码失效问题
docs: 更新 API 文档
refactor: 重构权限校验逻辑
perf: 优化数据库查询性能
test: 补充用户服务单测
```

---

## 许可证

本项目使用 [Apache License 2.0](LICENSE) 协议。

---

## 致谢

- [vue-vben-admin](https://github.com/vbenjs/vue-vben-admin)
- [RuoYi](https://www.ruoyi.vip/)

---

## 联系我

- 作者：Chuang
- 邮箱：`chuang@zhangchuangla.cn`
- 后端仓库：<https://github.com/lalala2726/echo-pro>
- 前端仓库：<https://github.com/lalala2726/echoPro-front-end>
- 问题反馈：提交 [Issues](https://github.com/lalala2726/echo-pro/issues)

---

## Star History

如果本项目对你有帮助，欢迎 Star 支持。

---
