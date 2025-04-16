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
 * @author Chuang
 * <p>
 * created on 2025/2/19 14:59
 */
@Slf4j
@RestController
@Tag(name = "注册接口")
@RequiredArgsConstructor
public class RegisterController extends BaseController {

    private final RegisterService registerService;


    /**
     * 注册
     *
     * @param request 请求参数
     * @return 返回结果
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    public AjaxResult register(@Parameter(name = "注册参数", required = true)
                               @Validated @RequestBody RegisterRequest request) {
        request.setUsername(request.getUsername().trim());
        request.setPassword(request.getPassword().trim());
        Long userId = registerService.register(request);
        log.info("用户注册成功，用户ID：{}", userId);
        return success(userId);
    }
}
