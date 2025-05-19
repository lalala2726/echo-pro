package cn.zhangchuangla.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:18
 */
@SpringBootApplication
@MapperScan("cn.zhangchuangla.**.mapper")
@ComponentScan("cn.zhangchuangla.**")
@EnableConfigurationProperties
@EnableCaching
public class APIApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIApplication.class, args);
        String startupSuccessful = """
                  ____  _             _   _   _         ____                               __       _\s
                 / ___|| |_ __ _ _ __| |_| | | |_ __   / ___| _   _  ___ ___ ___  ___ ___ / _|_   _| |
                 \\___ \\| __/ _` | '__| __| | | | '_ \\  \\___ \\| | | |/ __/ __/ _ \\/ __/ __| |_| | | | |
                  ___) | || (_| | |  | |_| |_| | |_) |  ___) | |_| | (_| (_|  __/\\__ \\__ \\  _| |_| | |
                 |____/ \\__\\__,_|_|   \\__|\\___/| .__/  |____/ \\__,_|\\___\\___\\___||___/___/_|  \\__,_|_|
                                               |_|                                                   \s
                """;
        System.out.println(startupSuccessful);
    }

}
