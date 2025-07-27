package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.core.config.property.AppProperty;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.framework.annotation.Anonymous;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/14 18:33
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Anonymous
@Tag(name = "首页接口", description = "提供系统基本信息和系统健康状态")
public class IndexController extends BaseController {

    private final AppProperty appProperty;


    /**
     * 欢迎访问
     */
    @GetMapping
    @Schema(description = "欢迎访问")
    public AjaxResult<Void> index() {
        return success(String.format("欢迎访问 %s 当前版本号 %s", appProperty.getName(), appProperty.getVersion()));
    }

}
