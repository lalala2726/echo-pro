package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/11 18:15
 */
@RequestMapping
@Tag(name = "权限接口")
@RestController("/system/permission")
@RequiredArgsConstructor
public class SysPermissionsController extends BaseController {


}
