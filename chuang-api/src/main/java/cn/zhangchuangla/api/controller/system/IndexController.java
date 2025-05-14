package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.config.property.AppProperty;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.annotation.Anonymous;
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
public class IndexController extends BaseController {

    private final AppProperty appProperty;


    /**
     * 欢迎访问
     */
    @GetMapping
    public AjaxResult<Void> index() {
        return success(String.format("欢迎访问 %s 当前版本号 %s", appProperty.getName(), appProperty.getVersion()));
    }

}
