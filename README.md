# Echo-Pro 后台管理系统

## 项目概述

Echo-Pro 是一套基于 Spring Boot 3.x 构建的企业级后台管理系统，采用前后端分离架构，聚焦于高可扩展性、高可维护性和高安全性。系统集成了完善的权限管理、灵活的文件存储、丰富的业务模块和高效的开发工具，适用于各类企业信息化、SaaS 平台、管理后台等场景。

## 技术栈

### 后端技术

- **核心框架**：Spring Boot 3.x
- **安全框架**：Spring Security + JWT
- **ORM 框架**：MyBatis Plus
- **数据库**：MySQL 8.0+
- **缓存**：Redis 6.0+
- **消息队列**：RabbitMQ（可选）
- **文件存储**：本地、MinIO、阿里云 OSS、腾讯云 COS
- **定时任务**：Quartz
- **数据校验**：Spring Validation
- **日志管理**：SLF4J
- **API 文档**：Swagger / OpenAPI
- **工具类**：Lombok、Apache commons 等

### 前端技术

- Vue 3.x
- Ant Design Pro
- Pinia
- Axios

## 项目结构

```
echoPro/
├── chuang-api/                # 接口层，RESTful 控制器、入口主程序
│   └── controller/            # 各业务模块 Controller
├── chuang-common/             # 公共模块，常量、工具类、通用配置
│   ├── chuang-common-core/    # 基础工具、异常、结果封装等
│   ├── chuang-common-excel/   # Excel 导入导出工具
│   ├── chuang-common-mq/      # 消息队列封装
│   ├── chuang-common-redis/   # Redis 工具与封装
│   └── chuang-common-websocket/# WebSocket 支持
├── chuang-framework/          # 框架扩展，安全、权限、日志、拦截器等
├── chuang-system/             # 业务核心模块聚合
│   ├── chuang-system-core/    # 用户、角色、部门、菜单、字典等
│   ├── chuang-system-storage/ # 文件上传、存储、图片处理
│   └── chuang-system-message/ # 站内信、消息通知
├── chuang-quartz/             # 定时任务调度模块
```

> 各模块均采用分层架构（Controller-Service-Mapper-Entity），便于维护和扩展。

## 主要功能

### 用户与权限管理

- 用户、角色、部门、菜单、字典等基础数据管理
- 动态权限分配，支持细粒度接口权限
- JWT 无状态认证，支持多端登录
- 登录、操作、异常日志记录

### 文件管理系统

- 支持本地、MinIO、阿里云 OSS、腾讯云 COS 多存储源
- 文件元数据管理、图片压缩与预览
- 存储策略可动态切换

### 消息与通知

- 站内信、系统通知、管理员消息推送
- 消息队列支持（RabbitMQ，可选）

### 系统配置与监控

- 参数配置、字典管理
- 在线用户、缓存、服务监控
- 定时任务管理（Quartz）

## 安装部署

### 环境要求

- JDK 17 及以上
- MySQL 8.0 及以上
- Redis 6.0 及以上
- Maven 3.6 及以上
- (可选) MinIO/阿里云 OSS/腾讯云 COS

### 快速启动

1. **克隆项目**
   ```bash
   git clone https://github.com/lalala2726/echo-pro.git
   cd echoPro
   ```

2. **初始化数据库**
   - 创建数据库，导入 `docs/sql` 目录下的初始化 SQL 脚本
   - 修改 `chuang-api/src/main/resources/application.yml` 数据库配置

3. **配置文件存储**
   - 本地存储：配置上传路径
   - MinIO/OSS/COS：配置服务地址、密钥等参数

4. **构建与运行**
   ```bash
   mvn clean package -DskipTests
   java -jar chuang-api/target/chuang-api.jar
   ```

5. **访问系统**
   ```
   http://localhost:8080
   默认账号：admin
   默认密码：admin123
   ```

## 开发规范

- 遵循阿里巴巴 Java 开发手册
- 统一异常处理与日志规范
- 代码注释齐全，类/方法注释规范
- 分支管理：`master`（主分支）、`dev`（开发分支）、`feature/*`（新功能）、`hotfix/*`（紧急修复）

## 贡献指南

1. Fork 本项目
2. 新建分支 (`git checkout -b feature/xxx`)
3. 提交代码 (`git commit -am 'Add feature xxx'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 提交 Pull Request

## 许可证

本项目基于 [MIT License](LICENSE) 开源。

## 联系方式

- 作者：Chuang
- 邮箱：chuang@zhangchuangla.cn
- 项目地址：https://github.com/lalala2726/echo-pro/

---
