# chuang-backend

This is a Java backend application built with Spring Boot and Maven.

## Modules

The project is divided into the following modules:

- `chuang-api`: Exposes the application's API.
- `chuang-common`: Contains common utilities and core functionalities.
  - `chuang-common-core`: Core components for the common module.
  - `chuang-common-excel`: Utilities for Excel file handling.
  - `chuang-common-mq`: Message queue functionalities.
  - `chuang-common-redis`: Redis integration.
  - `chuang-common-websocket`: WebSocket functionalities.
- `chuang-framework`: Provides the core framework for the application.
- `chuang-generator`: Includes code generation tools.
- `chuang-quartz`: Manages scheduled tasks using Quartz.
- `chuang-system`: Contains system-level functionalities.
  - `chuang-system-core`: Core components for the system module.
  - `chuang-system-message`: System messaging functionalities.
  - `chuang-system-storage`: Storage management functionalities.

## Key Technologies

- Java 17
- Spring Boot 3.4.3
- Spring Security 6.4.4
- Maven
- MyBatis-Plus (ORM)
- MySQL (Database)
- Redis (Caching)
- RabbitMQ (Message Queue)
- Quartz (Scheduling)
- Aliyun OSS / MinIO / Tencent COS (Object Storage)
- Docker (Containerization - inferred from typical Spring Boot backend deployments, though not explicitly in pom.xml)
