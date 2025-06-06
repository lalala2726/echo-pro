# echo-pro 后台管理系统

## 项目概述

echo-pro 是一个基于 Spring Boot 的企业级后台管理系统，采用前后端分离架构设计，提供了完善的权限管理、文件存储、数据处理等核心功能。系统设计灵活，支持多种文件存储方式，高度可配置，适合各类企业级应用开发。

## 技术栈

### 后端技术

- **核心框架**：Spring Boot 3.x
- **安全框架**：Spring Security + JWT
- **持久层**：MyBatis Plus + MySQL
- **缓存技术**：Redis
- **文件存储**：本地存储 + MinIO + 阿里云OSS + 腾讯云COS
- **数据校验**：Spring Validation
- **日志管理**：SLF4J
- **API文档**：Swagger / OpenAPI
- **其他工具**：Lombok

### 前端技术

- Vue.js
- Axios
- Element Plus
- Pinia

## 系统架构

### 模块结构

```
echo-pro/
├── chuang-api/               # 接口模块，包含控制器等入口层代码
├── chuang-common/            # 公共模块，存放常量、工具类、通用配置等
├── chuang-framework/         # 框架模块，封装安全、权限等框架扩展功能
│   └── security/             # 安全框架相关配置与扩展实现
├── chuang-system/            # 系统模块，核心业务模块聚合层
│   ├── chuang-system-core/   # 用户管理、系统配置等核心业务逻辑
│   ├── chuang-system-storage/# 文件上传、文件管理等存储相关逻辑
│   └── chuang-system-message/# 站内信、用于系统通知或管理员通知
├── chuang-generator/         # 代码生成模块，生成项目代码
```

### 架构设计

1. **分层设计**
   - 控制层 (Controller)：处理HTTP请求，参数验证，调用服务层
   - 服务层 (Service)：实现业务逻辑，事务管理
   - 数据访问层 (Mapper)：数据库交互
   - 实体层 (Entity)：数据模型与映射

2. **安全架构**
   - 基于JWT的无状态认证
   - 细粒度的权限控制
   - 防XSS, CSRF攻击

3. **缓存设计**
   - 使用Redis缓存配置项和常用数据
   - 多级缓存策略

4. **文件存储架构**
   - 多存储源支持（本地、MinIO、阿里云OSS，腾讯云）
   - 文件管理与元数据记录
   - 图片处理与压缩
   - 动态切换文件存储

## 核心功能

### 用户与权限管理

- 用户管理：创建、编辑、查询用户信息
- 角色管理：角色创建、权限分配
- 部门管理：组织架构维护
- 菜单管理：动态菜单配置

### 文件管理系统

- 多存储源支持：本地存储、MinIO对象存储、阿里云OSS
- 图片智能处理：自动压缩、预览图生成
- 文件元数据管理：记录文件信息、来源、使用情况

### 系统配置

- 参数配置：系统运行参数维护
- 字典管理：系统字典数据维护
- 存储配置：文件存储方式与参数配置

### 日志与监控

- 操作日志：记录用户操作
- 登录日志：记录登录尝试
- 异常日志：系统异常记录与告警

## 安装部署

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+
- (可选) MinIO/阿里云OSS

### 快速开始

1. **克隆项目**
   ```bash
   git clone https://github.com/username/app-backend.git
   cd app-backend
   ```

2. **配置数据库**
   - 创建数据库并导入初始SQL
   - 修改 `application.yml` 中的数据库配置

3. **配置文件存储**
   - 本地存储：配置上传路径
   - MinIO：配置服务地址、访问密钥
   - 阿里云OSS：配置服务地址、密钥

4. **构建与运行**
   ```bash
   mvn clean package
   java -jar admin/target/admin.jar
   ```

5. **访问系统**
   ```
   http://localhost:8080
   默认账号：admin
   默认密码：admin123
   ```

## 开发指南

### 代码规范

- 遵循阿里巴巴Java开发手册规范
- 类注释与代码注释规范
- 统一的异常处理与日志记录

### 分支管理

- master: 主分支，稳定版本
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 紧急修复分支

### 贡献代码

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/xxxx`)
3. 提交变更 (`git commit -am 'Add feature xxxx'`)
4. 推送到远程分支 (`git push origin feature/xxxx`)
5. 创建Pull Request

## 许可证

本项目采用 [MIT 许可证](LICENSE) 进行授权。

## 联系方式

- 作者：Chuang
- 邮箱：chuang@zhangchuangla.cn
- 项目地址：https://github.com/lalala2726/app-backend/
