package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.exception.LoginException;
import cn.zhangchuangla.system.model.entity.LoginUser;
import cn.zhangchuangla.system.model.entity.SysUser;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:34
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService sysUserService;

    public UserDetailsServiceImpl(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }


    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("用户名:{}", username);
        SysUser sysUser = sysUserService.getSysUserByUsername(username);
        //todo将请求登录日志等信息写入日志
        if (sysUser == null) {
            log.error("用户名:{},不存在", username);
            throw new LoginException("账号或者密码错误");
        }
        log.info("用户信息:{}", sysUser);
        return new LoginUser(sysUser);
    }
}
