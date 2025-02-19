package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.entity.LoginUser;
import cn.zhangchuangla.system.model.request.LoginRequest;
import cn.zhangchuangla.system.service.LoginService;
import cn.zhangchuangla.system.service.SysLoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:55
 */
@RestController
@Slf4j
public class LoginController {


    private final LoginService loginService;

    private final RedisCache redisCache;

    private final SysLoginLogService sysLoginLogService;

    public LoginController(LoginService loginService, RedisCache redisCache, SysLoginLogService sysLoginLogService) {
        this.loginService = loginService;
        this.redisCache = redisCache;
        this.sysLoginLogService = sysLoginLogService;
    }

    /**
     * 登录
     *
     * @param request 请求参数
     * @return token
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        log.info("登录请求参数：{}", request);
        if (loginRequest.getUsername() == null) {
            return AjaxResult.error("用户名不能为空");
        }
        if (loginRequest.getPassword() == null) {
            return AjaxResult.error("密码不能为空");
        }
        String login = loginService.login(loginRequest);
        AjaxResult ajax = new AjaxResult();
        ajax.put("token", login);


        return ajax;
    }

    @PostMapping("/logout")
    public AjaxResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getSysUser().getId();
        redisCache.deleteObject("login:" + id);
        return AjaxResult.success();
    }
}
