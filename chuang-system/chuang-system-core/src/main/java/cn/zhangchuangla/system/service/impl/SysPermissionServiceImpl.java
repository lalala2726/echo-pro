package cn.zhangchuangla.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysPermissionService;
import cn.zhangchuangla.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chuang
 * <p>
 * created on 2025/5/14 08:23
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysMenuService sysMenuService;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleService sysRoleService;

    /**
     * 获取用户权限列表
     *
     * @param roleSet 角色集合
     * @return 权限列表
     */
    @Override
    public Set<String> getUserPermissionByRole(Set<String> roleSet) {
        if (roleSet == null) {
            return Collections.emptySet();
        }

        //判断是否包含超级管理员角色
        boolean isSuperAdmin = roleSet.contains(SysRolesConstant.SUPER_ADMIN);
        if (isSuperAdmin) {
            // 如果是超级管理员，返回所有权限
            log.info("用户具有超级管理员角色，返回所有权限");
            return sysMenuService.list().stream()
                    .map(SysMenu::getPermission)
                    .filter(StrUtil::isNotEmpty)
                    .collect(Collectors.toSet());
        }
        //如果不是超级管理员，查询用户的权限列表
        List<SysMenu> userPermissionListByRole = sysMenuMapper.getUserPermissionListByRole(roleSet);
        return userPermissionListByRole.stream()
                .map(SysMenu::getPermission)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户权限列表
     *
     * @param role 角色
     * @return 权限列表
     */
    @Override
    public Set<String> getUserPermissionByRole(String role) {
        if (role.isEmpty()) {
            return Collections.emptySet();
        }

        //判断是否包含超级管理员角色
        boolean isSuperAdmin = role.contains(SysRolesConstant.SUPER_ADMIN);
        if (isSuperAdmin) {
            // 如果是超级管理员，返回所有权限
            return sysMenuService.list().stream()
                    .map(SysMenu::getPermission)
                    .filter(StrUtil::isNotEmpty)
                    .collect(Collectors.toSet());
        }
        //如果不是超级管理员，查询用户的权限列表
        Set<String> roleSet = Set.of(role);
        List<SysMenu> userPermissionListByRole = sysMenuMapper.getUserPermissionListByRole(roleSet);
        return userPermissionListByRole.stream()
                .map(SysMenu::getPermission)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> getUserPermissionByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        //根据角色ID查询用户关联的角色
        Set<String> roleSet = sysRoleService.getRoleSetByUserId(userId);
        //判断是否包含超级管理员角色
        if (roleSet.contains(SysRolesConstant.SUPER_ADMIN)) {
            //如果是超级管理员，通过超级管理员角色标识符查询所有权限
            return getUserPermissionByRole(SysRolesConstant.SUPER_ADMIN);
        }
        //否则，根据角色ID查询用户的权限列表
        return getUserPermissionByRole(roleSet);
    }

    /**
     * 获取当前用户权限列表,如果不传递用户ID，则默认使用当前登录用户的ID
     *
     * @return 权限列表
     */
    @Override
    public Set<String> getUserPermissionByUserId() {
        Long userId = SecurityUtils.getUserId();
        return getUserPermissionByUserId(userId);
    }

}
