package cn.zhangchuangla.infrastructure.web.service.impl;

import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.common.core.security.model.SysUserDetails;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser sysUser = sysUserService.getUserInfoByUsername(username);
        Set<String> roleSet = sysRoleService.getRoleSetByUserId(sysUser.getUserId());
        log.info("用户权限信息:{}", roleSet);
        return new SysUserDetails(sysUser, roleSet);
    }


}

