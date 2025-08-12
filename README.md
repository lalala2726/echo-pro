# Echo‑Pro 企业级后台管理系统

Echo‑Pro 是一套基于 Spring Boot 3 的企业后台管理系统，聚焦「安全、可扩展、可维护」。系统提供统一鉴权、文件存储、任务调度、系统监控等常用能力，适配多种企业与 SaaS 场景。

### 简要介绍

Echo‑Pro 面向企业管理后台的高频通用需求，内置用户权限、统一鉴权、文件中心、任务调度与可观测能力，提供清晰的模块化架构与生产可用的默认安全策略，支持按需裁剪与扩展。

### 核心特点（项目级）

- **模块化与可插拔**：`API / Framework / System / Common / Quartz` 分层清晰、依赖解耦，按需组合。
- **无状态鉴权**：Spring Security + JWT，`@Anonymous` 自动白名单，多端/会话策略可控。
- **多存储抽象**：本地、MinIO、OSS、COS、S3 一致接口，`active-type` 一键切换，支持回退与“真实删除/回收站”策略。
- **可观测性开箱**：Actuator + Micrometer + Prometheus 指标，内置“端点统计”API，便于运维与容量规划。
- **生产级安全默认**：CORS 外部化、CSP/HSTS/安全头增强、统一异常处理，Druid 控制台可加固/关闭。
- **配置外部化与多 Profile**：`druid,rabbitmq,quartz,monitor,storage` 按需激活，敏感信息支持环境变量注入。
- **开发效率工具箱**：Excel 导入导出、Redis 封装、WebSocket、RabbitMQ、IP/UA 解析等通用能力。

### 核心功能

- **用户与权限**：用户、角色、部门、菜单、字典；角色/权限集合查询；登录/操作/异常日志。
- **认证中心**：登录、注册、刷新令牌、登出；用户信息、角色与权限获取。
- **文件中心**：多源存储、上传/预览、图片压缩（Thumbnailator）、类型识别（Tika）、域名映射。
- **系统监控**：健康检查、运行指标、Redis/Spring 组件指标、端点访问统计（匿名可查）。
- **任务调度**：Quartz JDBC 持久化与集群，任务并发控制与反射调用工具。
- **消息通知**：站内信与系统通知（可对接 MQ）。

### 模块特点与相较传统项目的改进

- **chuang-api（入口/控制器）**：多 Profile 激活清晰，Swagger 统一暴露；对比传统“入口与配置混杂”。
- **chuang-framework（安全/日志/拦截器）**：`@Anonymous` 自动白名单、CORS 外部化、CSP/HSTS 预置；对比传统“白名单硬编码、安全头缺失”。
- **chuang-system-core（核心域）**：MyBatis‑Plus 统一（逻辑删/别名/分页）、DTO/VO 分离；对比传统“实体/DAO 散乱”。
- **chuang-system-storage（存储）**：多云适配层、`active-type` 切换与降级、`file-domain` 映射、`real-delete` 可控；对比传统“单一存储实现”。
- **chuang-system-message（消息）**：站内信/系统消息扩展清晰，可与 MQ 集成；对比传统“耦合业务难拓展”。
- **chuang-system-monitor（监控）**：Actuator + Micrometer + Prometheus，`/monitor/endpoints/*` 端点统计；对比传统“仅日志/健康检查”。
- **chuang-common-*（通用能力）**：统一 `AjaxResult`、异常层级、常量/工具、Excel/Redis/WebSocket/MQ 支撑；对比传统“工具分散风格不一”。
- **chuang-quartz（调度）**：JDBC Store + 集群、表前缀 `QRTZ_`、首次自动建表、禁止并发支持；对比传统“内存/单机调度”。

### 与传统项目的对比要点

- **安全**：Session → JWT 无状态；路径白名单硬编码 → 注解自动收集；统一 CSP/HSTS/Headers。
- **存储**：单实现 → 多云可切换与降级；域名映射与删除策略治理。
- **可观测**：仅日志/health → 指标体系 + Prometheus 抓取 + 端点访问统计。
- **配置**：硬编码 → Profile 分层 + 环境变量注入。
- **扩展性**：散装工具 → 模块化可插拔；横切关注点 AOP 化。
- **调度**：内存/单机 → JDBC 持久化 + 集群 + 并发治理。

### 模块概览

```
echoPro/
├── chuang-api/                 # 应用入口与 REST 控制器
├── chuang-framework/           # 安全、鉴权、日志、拦截器等框架增强
├── chuang-system/              # 业务域聚合
│   ├── chuang-system-core/     # 用户/角色/部门/菜单/字典等核心域
│   ├── chuang-system-storage/  # 文件上传与多存储源适配
│   ├── chuang-system-message/  # 站内信与通知
│   └── chuang-system-monitor/  # 指标采集与监控（Actuator/Micrometer）
├── chuang-common/              # 公共工具与基础设施
│   ├── chuang-common-core/
│   ├── chuang-common-redis/
│   ├── chuang-common-excel/
│   ├── chuang-common-mq/
│   └── chuang-common-websocket/
└── chuang-quartz/              # Quartz 任务调度
```

### 技术栈

- **核心**：Spring Boot 3.4.x、Spring Security 6、JWT、MyBatis‑Plus
- **数据**：MySQL 8、Redis 6、Druid 连接池
- **存储**：本地/MinIO/阿里云 OSS/腾讯云 COS/AWS S3
- **调度**：Quartz（JDBC Store/集群）
- **接口**：OpenAPI/Swagger、Spring Validation
- **监控**：Actuator、Micrometer、Prometheus（可抓取）

---

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8+、Redis 6+
- 可选：RabbitMQ、MinIO/OSS/COS/S3

### 克隆与构建

```bash
git clone https://github.com/lalala2726/echo-pro.git
cd echoPro
mvn -v    # 确认 Maven 版本
mvn clean package -DskipTests
```

### 运行

```bash
java -jar chuang-api/target/chuang-api.jar
```

启动成功后默认服务地址为 [http://localhost:8080](`http://localhost:8080`)。

### 访问入口

- **Swagger UI**： [swagger-ui.html](`http://localhost:8080/swagger-ui.html`)
- **OpenAPI JSON**： [/v3/api-docs](`http://localhost:8080/v3/api-docs`)
- **Actuator**： [/actuator](`http://localhost:8080/actuator`)（默认暴露 `health,info,metrics,prometheus`）
- **Druid 监控**： [/druid/](`http://localhost:8080/druid/`)（默认账号/密码见 `application-druid.yml`，生产务必关闭或加固）
- **端点统计（匿名）**：
  - `/monitor/endpoints/list`
  - `/monitor/endpoints/overview`

---

## 配置说明

系统采用多配置文件激活机制，默认激活：`druid,rabbitmq,quartz,monitor,storage`。

根配置文件：`chuang-api/src/main/resources/application.yml`

关键项（示例，建议通过环境变量或外部化配置覆盖敏感信息）：

```yaml
server:
  port: 8080

spring:
  profiles:
    active: druid,rabbitmq,quartz,monitor,storage
  data:
    redis:
      host: ${REDIS_HOST:127.0.0.1}
      port: ${REDIS_PORT:6379}
      database: 0
      password: ${REDIS_PASSWORD:}

springdoc:
  swagger-ui:
    path: /swagger-ui.html

security:
  header: Authorization
  session:
    access-token-expire-time: 7200000   # 2h（毫秒）
    refresh-token-expire-time: 2592000  # 30d（秒）
```

分模块配置：

- `application-druid.yml`：数据库与 Druid 控制台
- `application-rabbitmq.yml`：RabbitMQ（可按需关闭）
- `application-quartz.yml`：Quartz（JDBC Store、集群、表前缀 `QRTZ_`，并自动初始化 Quartz 表）
- `application-monitor.yml`：Actuator 暴露与监控采集
- `application-storage.yml`：文件存储（见下文）

### 文件存储

支持 `local|minio|aliyun-oss|tencent-cos|amazon-s3`，可通过 `storage.active-type` 切换。示例：

```yaml
storage:
  active-type: local
  local:
    uploadPath: ${FILE_UPLOAD_PATH:/data/upload}
    file-domain: http://localhost:8080/
  minio:
    endpoint: ${MINIO_ENDPOINT:http://127.0.0.1:9000}
    access-key: ${MINIO_ACCESS_KEY:}
    secret-key: ${MINIO_SECRET_KEY:}
    bucket-name: ${MINIO_BUCKET:develop}
```

生产环境务必将秘钥改为环境变量，并限制公网访问。

### CORS

`chuang-framework` 中的 `CorsConfig` 支持通过 `app.cors.*` 外部化配置，默认放开常见本地调试域名：

```yaml
app:
  cors:
    allowed-origin-patterns: ["http://*:*", "https://*:*"]
    allowed-methods: [GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD]
    allow-credentials: true
```

---

## 认证与鉴权

- 认证方式：JWT（无状态，会话策略 `STATELESS`），头部：`Authorization: Bearer <token>`
- 开放白名单：Swagger/静态资源/标注 `@Anonymous` 的接口

常用接口：

```http
POST /auth/login            # 登录，返回访问令牌与刷新令牌
POST /auth/refresh          # 刷新令牌
GET  /auth/getUserInfo      # 获取当前用户信息
GET  /auth/permission       # 获取权限集合
GET  /auth/roles            # 获取角色集合
DELETE /auth/logout         # 退出登录
```

请求示例：

```json
POST /auth/login
{
  "username": "admin",
  "password": "yourPassword"
}
```

---

## 任务调度（Quartz）

- 存储：JDBC 持久化，表前缀 `QRTZ_`
- 集群：开启 `isClustered=true`
- 表初始化：`spring.quartz.jdbc.initialize-schema=always`（首次启动自动创建 Quartz 表）

---

## 数据库初始化

- Quartz 表将按上述配置自动初始化。
- 业务库初始化脚本（用户/角色/菜单等）暂未随仓库提供，后续将补充。可按实体与 `mapper/system/*.xml` 自行建表或导入你现有库结构。

---

## 常见问题（FAQ）

- 启动报数据库连接失败：检查 `application-druid.yml` 与数据库权限/IP 白名单。
- Redis 连接失败：确认 Redis 地址/密码与网络连通性。
- Swagger 无法访问：确认端口与 `springdoc` 配置；检查安全白名单与网关代理。
- Druid 控制台安全：生产务必关闭或改强密码/IP 白名单。

---

## 开发规范

- 遵循阿里巴巴 Java 开发手册
- 统一异常与日志规范
- 注释与文档齐全
- 分支：`master`（主）/`dev`（开发）/`feature/*`（特性）/`hotfix/*`（修复）

## 贡献

1. Fork 本仓库
2. 创建分支：`git checkout -b feature/xxx`
3. 提交变更：`git commit -m "feat: xxx"`
4. 推送分支并提交 PR

## 许可证

开源协议：MIT

## 联系

- 作者：Chuang  
- 邮箱：chuang@zhangchuangla.cn  
- 项目地址：[GitHub - echo-pro](`https://github.com/lalala2726/echo-pro/`)

