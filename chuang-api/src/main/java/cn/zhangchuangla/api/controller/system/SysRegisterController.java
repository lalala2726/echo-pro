package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.model.request.RegisterRequest;
import cn.zhangchuangla.infrastructure.web.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册接口控制器
 * 提供用户注册相关功能
 *
 * @author Chuang
 * created on 2025/2/19 14:59
 */
@Slf4j
@RestController
@Tag(name = "注册接口")
@RequiredArgsConstructor
public class SysRegisterController extends BaseController {

    private final RegisterService registerService;

    /**
     * 注册
     *
     * @param request 注册请求参数
     * @return 返回注册结果，成功返回用户ID
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public AjaxResult register(@Parameter(description = "注册参数", required = true)
                               @Validated @RequestBody RegisterRequest request) {
        request.setUsername(request.getUsername().trim());
        request.setPassword(request.getPassword().trim());
        Long userId = registerService.register(request);
        log.info("用户注册成功，用户ID：{}", userId);
        return success(userId);
    }
}
