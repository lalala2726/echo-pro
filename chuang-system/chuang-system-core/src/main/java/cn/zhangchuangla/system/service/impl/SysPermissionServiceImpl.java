package cn.zhangchuangla.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysPermissionService;
import cn.zhangchuangla.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
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
@CacheConfig(cacheNames = RedisConstants.Auth.PERMISSIONS_PREFIX)
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysMenuService sysMenuService;
    private final SysMenuMapper sysMenuMapper;
    private final SysRoleService sysRoleService;
    private final RedisCache redisCache;

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
            //先从缓存中获取权限列表
            Set<String> set = redisCache.getCacheObject(RedisConstants.Auth.PERMISSIONS_PREFIX + SysRolesConstant.SUPER_ADMIN);
            if (set == null) {
                //如果缓存中没有，则从数据库中获取权限列表
                set = sysMenuService.list().stream()
                        .map(SysMenu::getPermission)
                        .filter(StrUtil::isNotEmpty)
                        .collect(Collectors.toSet());
                //将权限列表存入缓存
                redisCache.setCacheObject(RedisConstants.Auth.PERMISSIONS_PREFIX + SysRolesConstant.SUPER_ADMIN, set);
            }
            return set;
        }
        //如果不是超级管理员，查询用户的权限列表
        //先从缓存中获取权限列表
        String cacheKey = RedisConstants.Auth.PERMISSIONS_PREFIX + String.join(",", roleSet);
        Set<String> set = redisCache.getCacheObject(cacheKey);
        if (set != null) {
            return set;
        }
        //如果缓存中没有，则从数据库中获取权限列表
        List<SysMenu> userPermissionListByRole = sysMenuMapper.getUserPermissionListByRole(roleSet);
        set = userPermissionListByRole.stream()
                .map(SysMenu::getPermission)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
        //将权限列表存入缓存
        redisCache.setCacheObject(cacheKey, set);
        return set;
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
            String cacheKey = RedisConstants.Auth.PERMISSIONS_PREFIX + SysRolesConstant.SUPER_ADMIN;
            Set<String> set = redisCache.getCacheObject(cacheKey);
            if (set == null) {
                set = sysMenuService.list().stream()
                        .map(SysMenu::getPermission)
                        .filter(StrUtil::isNotEmpty)
                        .collect(Collectors.toSet());
                redisCache.setCacheObject(cacheKey, set);
            }
            return set;
        }

        //如果不是超级管理员，查询用户的权限列表
        String cacheKey = RedisConstants.Auth.PERMISSIONS_PREFIX + role;
        Set<String> set = redisCache.getCacheObject(cacheKey);
        if (set != null) {
            return set;
        }

        Set<String> roleSet = Set.of(role);
        List<SysMenu> userPermissionListByRole = sysMenuMapper.getUserPermissionListByRole(roleSet);
        set = userPermissionListByRole.stream()
                .map(SysMenu::getPermission)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.toSet());
        redisCache.setCacheObject(cacheKey, set);
        return set;
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

        String cacheKey = RedisConstants.Auth.PERMISSIONS_PREFIX + userId;
        Set<String> permissions = redisCache.getCacheObject(cacheKey);
        if (permissions != null) {
            return permissions;
        }

        //根据角色ID查询用户关联的角色
        Set<String> roleSet = sysRoleService.getRoleSetByUserId(userId);
        //判断是否包含超级管理员角色
        if (roleSet.contains(SysRolesConstant.SUPER_ADMIN)) {
            //如果是超级管理员，通过超级管理员角色标识符查询所有权限
            permissions = getUserPermissionByRole(SysRolesConstant.SUPER_ADMIN);
        } else {
            //否则，根据角色ID查询用户的权限列表
            permissions = getUserPermissionByRole(roleSet);
        }

        redisCache.setCacheObject(cacheKey, permissions);
        return permissions;
    }

    /**
     * 获取用户权限列表，如果不传递用户ID，则默认获取当前登录用户的权限列表
     *
     * @return 权限列表
     */
    @Override
    public Set<String> getUserPermissionByUserId() {
        Long userId = SecurityUtils.getUserId();
        return getUserPermissionByUserId(userId);
    }

}
