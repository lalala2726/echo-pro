package cn.zhangchuangla.framework.web.service.impl;

import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.exception.LoginException;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/2/19 13:34
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService sysUserService;
    private final SysPermissionsService sysPermissionsService;
    private final SysRoleService sysRoleService;

    public UserDetailsServiceImpl(SysUserService sysUserService, SysPermissionsService sysPermissionsService, SysRoleService sysRoleService) {
        this.sysUserService = sysUserService;
        this.sysPermissionsService = sysPermissionsService;
        this.sysRoleService = sysRoleService;
    }


    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser sysUser = sysUserService.getSysUserByUsername(username);
        if (sysUser == null) {
            log.error("用户名:{},不存在", username);
            throw new LoginException("账号或者密码错误");
        }
        Long userId = sysUser.getUserId();
        Set<String> roles = sysRoleService.getUserRoleSetByUserId(userId);
        Set<String> permissions = sysPermissionsService.getPermissionsByUserId(userId);
        return new LoginUser(sysUser, permissions, roles);
    }

}
