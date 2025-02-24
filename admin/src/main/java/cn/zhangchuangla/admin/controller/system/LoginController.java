package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.constant.SystemConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.framework.model.entity.LoginUser;
import cn.zhangchuangla.framework.model.request.LoginRequest;
import cn.zhangchuangla.framework.web.service.SysLoginService;
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
    public AjaxResult login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
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
    public AjaxResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long id = loginUser.getSysUser().getUserId();
        redisCache.deleteObject("login:" + id);
        return AjaxResult.success();
    }
}
