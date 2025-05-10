package cn.zhangchuangla.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j整合Swagger3 Api接口文档配置类
 *
 * @author Chuang
 */
@Configuration
public class Knife4jConfig {

    /**
     * 系统管理分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("系统管理")
                // 扫描的基础包路径
                .packagesToScan("cn.zhangchuangla.api.controller.system")
                .build();
    }

    /**
     * 系统监控分组
     */
    @Bean
    public GroupedOpenApi monitorApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("系统监控")
                // 扫描的基础包路径
                .packagesToScan("cn.zhangchuangla.api.controller.monitor")
                .build();
    }

    /**
     * 常用功能分组
     */
    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("常用功能")
                // 扫描的基础包路径
                .packagesToScan("cn.zhangchuangla.api.controller.common")
                .build();
    }

    /**
     * 消息功能分组
     */
    @Bean
    public GroupedOpenApi messageApi() {
        return GroupedOpenApi.builder()
                // 分组名称
                .group("消息功能")
                // 扫描的基础包路径
                .packagesToScan("cn.zhangchuangla.api.controller.message")
                .build();
    }

    /**
     * 配置基本信息
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        // 标题
                        .title("通用软件后端接口文档")
                        // 描述 Api 接口文档的基本信息
                        .description("这是一个通用软件后端接口文档")
                        // 版本
                        .version("v1.0.0")
                        // 设置 OpenAPI 文档的联系信息，姓名，邮箱。
                        .contact(new Contact().name("Chuang").email("chuang@zhangchuangla.cn"))
                        // 设置 OpenAPI 文档的许可证信息，包括许可证名称为 "Apache 2.0"，许可证 URL 为 "http://springdoc.org"。
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                );
    }
}
