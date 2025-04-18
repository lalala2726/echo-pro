package cn.zhangchuangla.api.controller.common;

import cn.zhangchuangla.common.core.controller.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用接口控制器
 * 可扩展通用功能
 *
 * @author Chuang
 */
@Slf4j
@Tag(name = "通用接口")
@RequestMapping("/common")
@RestController
public class CommonController extends BaseController {

}
