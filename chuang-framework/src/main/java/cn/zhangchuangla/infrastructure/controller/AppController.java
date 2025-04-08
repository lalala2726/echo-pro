package cn.zhangchuangla.infrastructure.controller;

import cn.zhangchuangla.common.config.AppConfig;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangchuang
 * Created on 2025/3/1 17:45
 */
@Tag(name = "欢迎页")
@RestController
@Anonymous
public class AppController {


    /**
     * 欢迎页
     */
    @GetMapping("/")
    @Schema(name = "欢迎页")
    public AjaxResult getAppConfig() {
        AppConfig appConfig = new AppConfig();
        String name = appConfig.getName();
        String version = appConfig.getVersion();
        StringBuffer append = new StringBuffer()
                .append("欢迎访问:")
                .append(name)
                .append("，版本号：")
                .append(version)
                .append("，系统描述：")
                .append(appConfig.getDescription());
        return AjaxResult.success(append);
    }

}
