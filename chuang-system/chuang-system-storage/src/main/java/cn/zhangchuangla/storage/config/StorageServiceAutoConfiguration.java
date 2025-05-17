package cn.zhangchuangla.storage.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangchuang
 */
@Configuration
@EnableConfigurationProperties(StorageSystemProperties.class)
// Scan for @Component, @Service in the storage module, including StorageManager and service impls
@ComponentScan("cn.zhangchuangla.storage")
public class StorageServiceAutoConfiguration {

    // StorageService 实现 (LocalStorageServiceImpl 等)
    // 仍然通过 @Service 注解被 @ComponentScan 扫描到。
    // 它们的构造函数将需要修改以注入新的特定配置内部类。

    // StorageManager 同样通过 @Component 被扫描到。
    // 其构造函数将需要修改以注入 StorageSystemProperties 和 ApplicationContext (可能还有 StorageConfigLoader)。

    // ConditionalOnProperty 的Bean定义示例不再直接适用，因为属性的激活和存在性判断
    // 将在 StorageManager 内部根据新的 StorageSystemProperties 和数据库配置进行。
    // 服务Bean本身应该总是被创建，由StorageManager决定哪个是激活的。

    // Example of conditional bean creation if we didn't use @Service on impls:
    /*
    @Bean("localStorageService")
    @ConditionalOnProperty(prefix = "storage.local", name = "root-path-or-bucket-name")
    public StorageService localStorageService(LocalStorageProperties properties) {
        return new LocalStorageServiceImpl(properties);
    }

    @Bean("minioStorageService")
    @ConditionalOnProperty(prefix = "storage.minio", name = "endpoint")
    public StorageService minioStorageService(MinioProperties properties) {
        return new MinioStorageServiceImpl(properties);
    }

    @Bean("aliyunOssStorageService")
    @ConditionalOnProperty(prefix = "storage.aliyun-oss", name = "endpoint")
    public StorageService aliyunOssStorageService(AliyunOssProperties properties) {
        return new AliyunOssStorageServiceImpl(properties);
    }

    @Bean("tencentCosStorageService")
    @ConditionalOnProperty(prefix = "storage.tencent-cos", name = "region")
    public StorageService tencentCosStorageService(TencentCosProperties properties) {
        return new TencentCosStorageServiceImpl(properties);
    }
    */

    // StorageManager bean is already created via @Component.
    // If it wasn't, it would be defined like this:
    /*
    @Bean
    public StorageManager storageManager(ApplicationContext applicationContext,
                                         LocalStorageProperties localStorageProperties,
                                         MinioProperties minioProperties,
                                         AliyunOssProperties aliyunOssProperties,
                                         TencentCosProperties tencentCosProperties) {
        return new StorageManager(applicationContext, localStorageProperties, minioProperties, aliyunOssProperties, tencentCosProperties);
    }
    */
}
