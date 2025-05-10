package cn.zhangchuangla.framework.controller;

import cn.zhangchuangla.common.config.property.AppProperty;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.annotation.Anonymous;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * Created on 2025/3/1 17:45
 */
@Tag(name = "欢迎页")
@RestController
@Anonymous
public class AppController {

    @Resource
    private AppProperty appProperty;


    /**
     * 欢迎页
     */
    @GetMapping("/")
    @Schema(name = "欢迎页")
    public AjaxResult<Void> getAppConfig() {
        return AjaxResult.success("欢迎访问！");
    }

}
