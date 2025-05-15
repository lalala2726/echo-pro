package cn.zhangchuangla.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:18
 */
@SpringBootApplication
@MapperScan({
        "cn.zhangchuangla.**.mapper",
        "cn.zhangchuangla.**.converter",
})
@ComponentScan(basePackages = "cn.zhangchuangla.**")
@EnableCaching
public class APIApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIApplication.class, args);
        System.out.println("  ____             _           _   ____                                    _ _         _ \n" +
                " / ___|  __ _ _ __| |_ ___  __| | / ___| _   _  ___ ___ ___  ___ ___ _   _| | |_   _  | |\n" +
                " \\___ \\ / _` | '__| __/ _ \\/ _` | \\___ \\| | | |/ __/ __/ _ \\/ __/ __| | | | | | | | | | |\n" +
                "  ___) | (_| | |  | ||  __/ (_| |  ___) | |_| | (_| (_|  __/\\__ \\__ \\ |_| | | | |_| | |_|\n" +
                " |____/ \\__,_|_|   \\__\\___|\\__,_| |____/ \\__,_|\\___\\___\\___||___/___/\\__,_|_|_|\\__, | (_)\n" +
                "                                                                               |___/     ");
    }

}
