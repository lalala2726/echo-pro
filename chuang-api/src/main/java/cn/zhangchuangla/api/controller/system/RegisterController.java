package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.model.request.RegisterRequest;
import cn.zhangchuangla.infrastructure.web.service.RegisterService;
import cn.zhangchuangla.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 14:59
 */
@RestController
@Tag(name = "注册接口")
public class RegisterController extends BaseController {

    private final RegisterService registerService;

    private final SysUserService sysUserService;

    public RegisterController(RegisterService registerService, SysUserService sysUserService) {
        this.registerService = registerService;
        this.sysUserService = sysUserService;
    }


    /**
     * 注册
     *
     * @param request 请求参数
     * @return 返回结果
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    @Log(title = "用户注册", businessType = BusinessType.REGISTER, isSaveRequestData = false)
    public AjaxResult register(@Parameter(name = "注册参数", required = true)
                               @Validated @RequestBody RegisterRequest request) {
        if (request.getUsername() == null) {
            return error("用户名不能为空");
        }
        if (request.getPassword() == null) {
            return error("密码不能为空");
        }
        if (sysUserService.isUsernameExist(request.getUsername())) {
            return error("用户名已存在");
        }

        Long userId = registerService.register(request);
        return success(userId);
    }

}
