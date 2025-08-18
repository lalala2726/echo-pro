package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.constant.RolesConstant;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.system.core.mapper.SysMenuMapper;
import cn.zhangchuangla.system.core.model.entity.SysMenu;
import cn.zhangchuangla.system.core.model.entity.SysRole;
import cn.zhangchuangla.system.core.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.core.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.core.model.vo.menu.SysMenuTreeList;
import cn.zhangchuangla.system.core.model.vo.role.SysRolePermissionVo;
import cn.zhangchuangla.system.core.service.SysMenuService;
import cn.zhangchuangla.system.core.service.SysPermissionService;
import cn.zhangchuangla.system.core.service.SysRoleMenuService;
import cn.zhangchuangla.system.core.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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

    private final SysMenuMapper sysMenuMapper;
    private final SysRoleService sysRoleService;
    private final SysMenuService sysMenuService;
    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 根据角色标识符集合获取权限标识符
     *
     * @param roleSet 角色标识符集合
     * @return 权限标识符集合
     */
    @Override
    public Set<String> getPermissionByRole(Set<String> roleSet) {
        // 如果是超级管理员将返回全部权限信息
        if (roleSet.contains(RolesConstant.SUPER_ADMIN)) {
            return Set.of("*:*:*");
        }
        return sysMenuMapper.getPermissionByRole(roleSet);
    }

    /**
     * 根据角色标识符获取权限标识符
     *
     * @param role 角色标识符
     * @return 权限标识符集合
     */
    @Override
    public Set<String> getPermissionByRole(String role) {
        return getPermissionByRole(Set.of(role));
    }

    /**
     * 根据角色ID获取角色权限信息
     *
     * @param roleId 角色ID
     * @return 角色权限信息
     */
    @Override
    public SysRolePermissionVo getPermissionByRoleId(Long roleId) {
        SysRole role = Optional.ofNullable(sysRoleService.getById(roleId))
                .orElseThrow(() -> new ServiceException(ResultCode.RESULT_IS_NULL, "角色不存在，ID：" + roleId));
        List<SysMenu> allMenus = sysMenuService.list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort));
        List<SysMenuTreeList> menuTreeList = buildMenuTreeList(allMenus);
        List<Long> selectedMenuIds = getRoleAllocatedPermissionByRoleId(roleId);
        return new SysRolePermissionVo(roleId, role.getRoleName(), role.getRoleKey(), menuTreeList, selectedMenuIds);
    }

    /**
     * 更新角色权限信息
     *
     * @param request 更新角色权限信息请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        Long roleId = request.getRoleId();
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "角色不存在");
        }
        if (RolesConstant.SUPER_ADMIN.equals(sysRole.getRoleKey())) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }

        // 删除旧权限
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));

        if (request.getAllocatedMenuId() != null && !request.getAllocatedMenuId().isEmpty()) {
            // 获取所有菜单数据
            List<SysMenu> allMenus = sysMenuService.list();
            Map<Long, SysMenu> menuMap = allMenus.stream()
                    .collect(Collectors.toMap(SysMenu::getId, menu -> menu));
            
            // 用户直接分配的菜单ID
            Set<Long> directMenuIds = new HashSet<>(request.getAllocatedMenuId());

            // 包含父级菜单的完整菜单ID集合
            Set<Long> completeMenuIds = new HashSet<>();
            for (Long menuId : directMenuIds) {
                addMenuWithAncestors(menuId, menuMap, completeMenuIds);
            }

            // 批量插入权限
            List<SysRoleMenu> roleMenusToInsert = completeMenuIds.stream()
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(roleId);
                        sysRoleMenu.setMenuId(menuId);
                        // 设置权限类型：1-直接分配，2-自动添加的父级权限
                        sysRoleMenu.setPermissionType(directMenuIds.contains(menuId) ? 1 : 2);
                        return sysRoleMenu;
                    }).toList();

            return sysRoleMenuService.saveBatch(roleMenusToInsert);
        }
        return true;
    }

    /**
     * 递归添加菜单及其所有父级菜单
     *
     * @param menuId    菜单ID
     * @param menuMap   菜单映射
     * @param resultSet 结果集合
     */
    private void addMenuWithAncestors(Long menuId, Map<Long, SysMenu> menuMap, Set<Long> resultSet) {
        if (menuId == null || resultSet.contains(menuId)) {
            return;
        }

        resultSet.add(menuId);

        SysMenu menu = menuMap.get(menuId);
        if (menu != null && menu.getParentId() != null && menu.getParentId() != 0L) {
            addMenuWithAncestors(menu.getParentId(), menuMap, resultSet);
        }
    }


    /**
     * 根据角色ID获取已分配的权限ID列表（仅返回直接分配的权限）
     *
     * @param roleId 角色ID
     * @return 已分配的权限ID列表
     */
    @Override
    public List<Long> getRoleAllocatedPermissionByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }

        // 无论是否为超级管理员，都只返回直接分配的权限（permissionType = 1）
        // 这样确保前端权限分配界面显示正确
        return sysRoleMenuService.list(new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId)
                        .eq(SysRoleMenu::getPermissionType, 1))
                .stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());
    }


    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> getPermissionByUserId(Long userId) {
        Set<String> roleKeys = sysRoleService.getRoleSetByUserId(userId);
        return getPermissionByRole(roleKeys);
    }

    /**
     * 构建用于权限分配等场景的菜单树列表。
     */
    private List<SysMenuTreeList> buildMenuTreeList(List<SysMenu> allMenus) {
        long rootId = 0L;
        if (allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return allMenus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), rootId))
                .map(menu -> {
                    SysMenuTreeList treeNode = BeanCotyUtils.copyProperties(menu, SysMenuTreeList.class);
                    List<SysMenuTreeList> children = getChildrenAsMenuTreeList(allMenus, menu.getId());
                    treeNode.setChildren(children);
                    return treeNode;
                })
                .collect(Collectors.toList());
    }

    /**
     * 递归地获取指定父ID下的子菜单。
     */
    private List<SysMenuTreeList> getChildrenAsMenuTreeList(List<SysMenu> allMenus, Long parentId) {
        if (allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return allMenus.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .map(menu -> {
                    SysMenuTreeList treeNode = BeanCotyUtils.copyProperties(menu, SysMenuTreeList.class);
                    List<SysMenuTreeList> grandChildren = getChildrenAsMenuTreeList(allMenus, menu.getId());
                    treeNode.setChildren(grandChildren);
                    return treeNode;
                }).collect(Collectors.toList());
    }
}
