package cn.zhangchuangla.framework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI(Swagger3)配置类
 *
 * @author Chuang
 * <p>
 * created on 2025/5/17 17:29
 */
@Configuration
public class OpenAPIConfig {

    @Value("${app.name:Chuang}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    /**
     * 系统接口组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("系统管理")
                .packagesToScan("cn.zhangchuangla.api.controller.system")
                .build();
    }

    /**
     * 监控接口组
     */
    @Bean
    public GroupedOpenApi monitorApi() {
        return GroupedOpenApi.builder()
                .group("系统监控")
                .packagesToScan("cn.zhangchuangla.api.controller.monitor")
                .build();
    }


    /**
     * 通用接口组
     */
    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
                .group("通用功能")
                .packagesToScan("cn.zhangchuangla.api.controller.common")
                .build();
    }

    /**
     * 系统工具
     */
    @Bean
    public GroupedOpenApi toolApi() {
        return GroupedOpenApi.builder()
                .group("系统工具")
                .packagesToScan("cn.zhangchuangla.api.controller.tool")
                .build();
    }


    /**
     * 所有接口
     */
    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("所有接口")
                .packagesToScan("cn.zhangchuangla.api.controller")
                .build();
    }


    /**
     * 所有接口
     */
    @Bean
    public GroupedOpenApi personal() {
        return GroupedOpenApi.builder()
                .group("个人中心")
                .packagesToScan("cn.zhangchuangla.api.controller.personal")
                .build();
    }


    /**
     * OpenAPI 主配置
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName + " API文档")
                        .description("提供完整的API接口定义与交互说明，便于快速集成和使用。")
                        .version(appVersion)
                        .contact(new Contact()
                                .name("Chuang")
                                .email("admin@zhangchuangla.cn")
                                .url("https://zhangchuangla.cn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://opensource.org/licenses/apache-2-0")))
                .externalDocs(new ExternalDocumentation()
                        .description("系统使用说明文档")
                        .url("https://docs.zhangchuangla.cn"))
                .components(securitySchemes())
                .addSecurityItem(new SecurityRequirement().addList("JWT认证"));
    }

    /**
     * 配置安全方案
     */
    private Components securitySchemes() {
        return new Components()
                .addSecuritySchemes("JWT认证", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization"));
    }
}
