package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.model.request.LoginRequest;
import cn.zhangchuangla.infrastructure.web.service.SysLoginService;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:55
 */
@RestController
@Slf4j
@Tag(name = "登录接口")
public class LoginController extends BaseController {


    private final SysLoginService sysLoginService;

    private final SysUserService sysUserService;

    public LoginController(SysLoginService sysLoginService, SysUserService sysUserService) {
        this.sysLoginService = sysLoginService;
        this.sysUserService = sysUserService;
    }


    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public AjaxResult login(@Parameter(name = "登录参数", required = true)
                            @Validated @RequestBody LoginRequest loginRequest,
                            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        log.info("登录请求参数：{}", request);
        String token = sysLoginService.login(loginRequest, request);
        HashMap<String, String> result = new HashMap<>();
        result.put(Constants.TOKEN, token);
        return success(result);
    }


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息")
    public AjaxResult getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser sysUser = sysUserService.getUserInfoByUserId(loginUser.getUserId());
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(sysUser, userInfoVo);
        return AjaxResult.success(userInfoVo);
    }

}
