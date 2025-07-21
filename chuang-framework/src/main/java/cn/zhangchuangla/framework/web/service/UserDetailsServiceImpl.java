package cn.zhangchuangla.framework.web.service;

import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 自定义用户详情服务实现
 * 用于Spring Security认证过程中加载用户信息
 *
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
     * 此方法会在用户尝试登录时由Spring Security调用
     *
     * @param username 用户名
     * @return UserDetails 用户详情对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 获取系统用户信息
            SysUser sysUser = sysUserService.getUserInfoByUsername(username);
            if (sysUser == null) {
                log.warn("用户[{}]不存在", username);
                throw new UsernameNotFoundException("用户不存在");
            }
            Set<String> roleSet = sysRoleService.getRoleSetByUserId(sysUser.getUserId());
            log.info("用户[{}]角色集合: {}", username, roleSet);
            return new SysUserDetails(sysUser, roleSet);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("加载用户[{}]信息时发生错误", username, e);
            throw new ServiceException(ResultCode.SYSTEM_ERROR, "系统错误，无法加载用户信息");
        }
    }
}

