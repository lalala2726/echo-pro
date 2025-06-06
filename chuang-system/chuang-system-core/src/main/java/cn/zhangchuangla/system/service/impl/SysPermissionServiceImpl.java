package cn.zhangchuangla.system.service.impl;

import org.apache.commons.lang3.StringUtils;
import cn.zhangchuangla.common.core.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.model.vo.menu.SysMenuTreeList;
import cn.zhangchuangla.system.model.vo.role.SysRolePermVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysPermissionService;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final RedisCache redisCache;
    private final SysRoleService sysRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysMenuMapper sysMenuMapper;

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
                        .filter(StringUtils::isNotBlank)
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
                .filter(StringUtils::isNotBlank)
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
                        .filter(StringUtils::isNotBlank)
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
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        redisCache.setCacheObject(cacheKey, set);
        return set;
    }


    /**
     * 获取角色权限列表
     *
     * @param roleId 角色ID
     * @return 角色权限列表
     */
    @Override
    public SysRolePermVo getRolePermByRoleId(Long roleId) {
        SysRole role = Optional.ofNullable(sysRoleService.getById(roleId))
                .orElseThrow(() -> {
                    log.error("获取角色权限失败：角色ID {} 不存在。", roleId);
                    return new IllegalArgumentException("角色不存在，ID：" + roleId);
                });
        List<SysMenu> allMenus = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort));
        List<SysMenuTreeList> menuTreeList = buildMenuTreeList(allMenus);
        List<Long> selectedMenuIds = getRolePermissionSelectedByRoleId(roleId);
        return new SysRolePermVo(roleId, role.getRoleName(), role.getRoleKey(), menuTreeList, selectedMenuIds);
    }

    /**
     * 更新角色权限
     *
     * @param request 更新角色权限请求参数
     * @return 更新结果
     */
    @Override
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        Long roleId = request.getRoleId();
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            log.error("更新角色权限失败：角色ID {} 不存在。", roleId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "角色不存在");
        }
        if (SysRolesConstant.SUPER_ADMIN.equals(sysRole.getRoleKey())) {
            log.warn("试图修改超级管理员 ({}) 的权限，操作被禁止。", sysRole.getRoleKey());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        log.debug("已删除角色ID {} 的原有菜单权限。", roleId);
        if (request.getSelectedMenuId() != null && !request.getSelectedMenuId().isEmpty()) {
            List<SysRoleMenu> roleMenusToInsert = request.getSelectedMenuId().stream()
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(roleId);
                        sysRoleMenu.setMenuId(menuId);
                        return sysRoleMenu;
                    }).toList();
            log.debug("为角色ID {} 批量插入 {} 条新菜单权限。", roleId, roleMenusToInsert.size());
            return sysRoleMenuService.saveBatch(roleMenusToInsert);
        }
        return true;
    }


    /**
     * 构建用于权限分配等场景的菜单树列表 ({@link SysMenuTreeList} 结构)。
     *
     * @param allMenus 原始菜单列表 (应预先按 {@code parentId} 和 {@code sort} 排序)。
     * @return {@link SysMenuTreeList} 结构的树形菜单列表。
     */
    private List<SysMenuTreeList> buildMenuTreeList(List<SysMenu> allMenus) {
        if (allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return allMenus.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .map(menu -> {
                    SysMenuTreeList treeNode = new SysMenuTreeList();
                    BeanUtils.copyProperties(menu, treeNode);
                    List<SysMenuTreeList> children = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!children.isEmpty()) {
                        treeNode.setChildren(children);
                    }
                    return treeNode;
                })
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>此方法用于根据角色ID获取该角色已分配的菜单权限ID列表。主要流程包括：
     * <ul>
     *     <li>校验传入的角色ID是否为空，若为空则返回空列表并记录警告日志；</li>
     *     <li>通过角色ID查询对应的角色标识集合（roleKey）；</li>
     *     <li>如果角色包含超级管理员标识，则返回系统中所有菜单的ID；</li>
     *     <li>否则，调用数据访问层获取该角色关联的菜单ID列表。</li>
     * </ul>
     *
     * <p><b>关键点说明：</b></p>
     * <ul>
     *     <li>{@link SysRolesConstant#SUPER_ADMIN} 是超级管理员角色标识，拥有所有菜单权限；</li>
     *     <li>通过 {@link SysRoleService#getRoleSetByRoleId(Long)} 获取角色标识集合；</li>
     *     <li>通过 {@link SysMenuMapper#selectMenuListByRoleId(Long)} 查询角色对应的菜单ID列表。</li>
     * </ul>
     *
     * @param roleId 角色ID，用于查询该角色的菜单权限。
     * @return 返回与角色ID关联的菜单ID列表。如果角色为超级管理员，则返回所有菜单ID；
     * 如果角色ID为空或未找到相关菜单，则返回空列表。
     */
    @Override
    public List<Long> getRolePermissionSelectedByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("获取角色已选菜单ID列表时，角色ID为空。");
            return Collections.emptyList();
        }
        Set<String> roleKeys = sysRoleService.getRoleSetByRoleId(roleId);
        if (roleKeys.contains(SysRolesConstant.SUPER_ADMIN)) {
            log.info("角色ID {} (标识: {}) 是超级管理员，返回所有菜单ID。", roleId, roleKeys);
            return sysMenuService.list().stream().map(SysMenu::getMenuId).distinct().collect(Collectors.toList());
        }
        return sysRoleMenuService.selectMenuListByRoleId(roleId);
    }


    /**
     * 递归地获取指定父ID下的子菜单，并转换为 {@link SysMenuTreeList} 结构（通常用于权限分配树）。
     *
     * @param allMenus 所有菜单的扁平列表（应预先按sort排序）。
     * @param parentId 父菜单ID。
     * @return {@link SysMenuTreeList} 结构的子菜单列表。
     */
    private List<SysMenuTreeList> getChildrenAsMenuTreeList(List<SysMenu> allMenus, Long parentId) {
        if (parentId == null || allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return allMenus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                // 假设 allMenus 已经排序，或者 childrenList 在 map.forEach 中排序
                .map(menu -> {
                    SysMenuTreeList treeNode = new SysMenuTreeList();
                    BeanUtils.copyProperties(menu, treeNode);
                    // treeNode.setSort(menu.getSort()); // 如果 SysMenuTreeList 需要 sort
                    List<SysMenuTreeList> grandChildren = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!grandChildren.isEmpty()) {
                        treeNode.setChildren(grandChildren);
                    }
                    return treeNode;
                }).collect(Collectors.toList());
    }
}
