package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class LoginController {


    private final RedisCache redisCache;

    private final SysLoginService sysLoginService;

    public LoginController(RedisCache redisCache, SysLoginService sysLoginService) {
        this.redisCache = redisCache;
        this.sysLoginService = sysLoginService;
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
                            @RequestBody LoginRequest loginRequest,
                            @Parameter(name = "请求对象", required = true) HttpServletRequest request) {
        log.info("登录请求参数：{}", request);
        if (loginRequest.getUsername() == null) {
            return AjaxResult.error("用户名不能为空");
        }
        if (loginRequest.getPassword() == null) {
            return AjaxResult.error("密码不能为空");
        }
        String token = sysLoginService.login(loginRequest, request);
        HashMap<String, String> map = new HashMap<>();
        map.put(SystemConstant.TOKEN, token);
        return AjaxResult.success(map);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public AjaxResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getSysUser().getUserId();
        redisCache.deleteObject("login:" + id);
        return AjaxResult.success();
    }
}
