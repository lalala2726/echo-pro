package cn.zhangchuangla.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/11 03:18
 */
@SpringBootApplication
@MapperScan("cn.zhangchuangla.**.mapper")
@ComponentScan(basePackages = "cn.zhangchuangla.**")
public class APIApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIApplication.class, args);
        System.out.println("  ____  _             _                 ____                               __       _ \n" +
                " / ___|| |_ __ _ _ __| |_ _   _ _ __   / ___| _   _  ___ ___ ___  ___ ___ / _|_   _| |\n" +
                " \\___ \\| __/ _` | '__| __| | | | '_ \\  \\___ \\| | | |/ __/ __/ _ \\/ __/ __| |_| | | | |\n" +
                "  ___) | || (_| | |  | |_| |_| | |_) |  ___) | |_| | (_| (_|  __/\\__ \\__ \\  _| |_| | |\n" +
                " |____/ \\__\\__,_|_|   \\__|\\__,_| .__/  |____/ \\__,_|\\___\\___\\___||___/___/_|  \\__,_|_|\n" +
                "                               |_|                                                    ");
    }

}
