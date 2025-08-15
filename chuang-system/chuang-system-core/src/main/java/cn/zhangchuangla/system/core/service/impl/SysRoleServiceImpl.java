package cn.zhangchuangla.system.core.service.impl;

import cn.zhangchuangla.common.core.constant.RolesConstant;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.enums.ResultCode;
import cn.zhangchuangla.common.core.exception.ParamException;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.redis.constant.RedisConstants;
import cn.zhangchuangla.common.redis.core.RedisCache;
import cn.zhangchuangla.system.core.mapper.SysRoleMapper;
import cn.zhangchuangla.system.core.model.entity.SysMenu;
import cn.zhangchuangla.system.core.model.entity.SysRole;
import cn.zhangchuangla.system.core.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.core.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.core.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.core.model.request.role.SysRoleUpdateRequest;
import cn.zhangchuangla.system.core.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.core.service.SysMenuService;
import cn.zhangchuangla.system.core.service.SysRoleMenuService;
import cn.zhangchuangla.system.core.service.SysRoleService;
import cn.zhangchuangla.system.core.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色接口实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuService sysRoleMenuService;
    private final RedisCache redisCache;
    private final SysMenuService sysMenuService;
    private final SysUserRoleService sysUserRoleService;

    /**
     * 角色列表
     *
     * @param request 查询参数
     * @return 分页列表
     */
    @Override
    public Page<SysRole> roleList(SysRoleQueryRequest request) {
        Page<SysRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        return sysRoleMapper.roleList(page, request);
    }

    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    @Override
    public List<SysRole> getRoleListByUserId(Long userId) {
        return sysRoleMapper.getRoleListByUserId(userId);
    }

    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public Set<String> getRoleSetByUserId(Long userId) {
        if (userId <= 0) {
            throw new ParamException("用户ID无效");
        }

        String cacheKey = RedisConstants.Auth.ROLE_KEY + userId;

        // 从缓存获取角色权限集合
        Set<String> roleSet = redisCache.getCacheObject(cacheKey);
        if (roleSet == null || roleSet.isEmpty()) {
            List<SysRole> roleList = getRoleListByUserId(userId);
            roleSet = roleList.stream()
                    .map(SysRole::getRoleKey)
                    .collect(Collectors.toSet());
            redisCache.setCacheObject(cacheKey, roleSet);
        }
        return roleSet;
    }

    /**
     * 添加角色信息
     *
     * @param roleAddRequest 请求参数
     */
    @Override
    public boolean addRoleInfo(SysRoleAddRequest roleAddRequest) {
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(roleAddRequest, sysRole);
        return save(sysRole);
    }

    /**
     * 判断角色名称是否存在
     *
     * @param roleName 角色名称
     * @return true存在，false不存在
     */
    @Override
    public boolean isRoleNameExist(String roleName) {
        if (roleName != null && !roleName.isEmpty()) {
            LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysRoleLambdaQueryWrapper.eq(SysRole::getRoleName, roleName);
            return count(sysRoleLambdaQueryWrapper) > 0;
        }
        return false;
    }

    /**
     * 判断角色权限字符串是否存在
     *
     * @param roleKey 角色权限字符串
     * @return true存在，false不存在
     */
    @Override
    public boolean isRoleKeyExist(String roleKey) {
        if (roleKey != null && !roleKey.isEmpty()) {
            LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysRoleLambdaQueryWrapper.eq(SysRole::getRoleKey, roleKey);
            return count(sysRoleLambdaQueryWrapper) > 0;
        }
        return false;
    }

    /**
     * 修改角色信息
     *
     * @param request 修改角色信息
     * @return 操作结果
     */
    @Override
    public boolean updateRoleInfo(SysRoleUpdateRequest request) {
        SysRole role = getById(request.getId());
        if (role == null) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, "角色不存在");
        }
        // 检查是否包含超级管理员角色
        boolean contains = RolesConstant.SUPER_ADMIN.equals(role.getRoleKey());
        if (contains) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(request, sysRole);
        return updateById(sysRole);
    }

    /**
     * 获取部门下拉列表
     *
     * @return 下拉列表
     */
    @Override
    public List<Option<Long>> getRoleOptions() {
        List<SysRole> roleList = list();
        if (roleList != null && !roleList.isEmpty()) {
            return roleList.stream()
                    .map(role -> new Option<>(role.getId(), role.getRoleName()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 根据用户id获取角色id集合
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    @Override
    public Set<Long> getUserRoleIdByUserId(Long userId) {
        if (userId <= 0) {
            throw new ParamException(ResultCode.PARAM_ERROR, "用户ID不能小于等于0");
        }
        List<SysRole> roleList = getRoleListByUserId(userId);
        if (roleList == null) {
            return null;
        }
        return roleList.stream()
                .map(SysRole::getId)
                .collect(Collectors.toSet());
    }

    /**
     * 删除角色信息，支持批量删除
     *
     * @param ids 角色ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(List<Long> ids) {
        Assert.isTrue(CollectionUtils.isNotEmpty(ids), "角色ID不能为空");
        // 检查是否包含超级管理员角色
        List<SysRole> roles = listByIds(ids);
        if (roles.stream().anyMatch(role -> RolesConstant.SUPER_ADMIN.equals(role.getRoleKey()))) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "超级管理员角色不允许删除");
        }

        // 检查角色是否已分配用户
        if (sysUserRoleService.isRoleAssignedToUsers(ids)) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "角色已分配用户，不能删除");
        }

        // 检查角色是否已分配菜单
        if (sysRoleMenuService.isRoleAssignedMenus(ids)) {
            throw new ServiceException(ResultCode.OPERATION_ERROR, "角色已分配菜单，不能删除");
        }

        return removeByIds(ids);
    }

    /**
     * 根据角色ID集合获取角色权限字符串集合
     *
     * @param roleId 角色ID集合
     * @return 角色权限字符串集合
     */
    @Override
    public Set<String> getRoleSetByRoleId(List<Long> roleId) {
        if (roleId == null || roleId.isEmpty()) {
            throw new ParamException(ResultCode.PARAM_ERROR, "角色ID集合不能为空");
        }
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleLambdaQueryWrapper.in(SysRole::getId, roleId);
        List<SysRole> roles = list(sysRoleLambdaQueryWrapper);
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
    }

    /**
     * 根据角色ID集合获取角色权限字符串集合
     *
     * @param roleId 角色ID集合
     * @return 角色权限字符串集合
     */
    @Override
    public Set<String> getRoleSetByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRole> sysRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysRoleLambdaQueryWrapper.in(SysRole::getId, roleId);
        List<SysRole> roles = list(sysRoleLambdaQueryWrapper);
        return roles.stream()
                .map(SysRole::getRoleKey)
                .collect(Collectors.toSet());
    }

    /**
     * 根据用户ID查询角色信息
     *
     * @param id 角色ID
     * @return 返回角色信息
     */
    @Override
    public SysRole getRoleInfoById(Long id) {
        SysRole sysRole = getById(id);
        if (sysRole == null) {
            throw new ServiceException(ResultCode.RESULT_IS_NULL, String.format("ID:【%s】的角色不存在", id));
        }
        return sysRole;
    }

    /**
     * 更新角色权限信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        // 参数校验
        if (request == null || request.getRoleId() == null) {
            throw new ParamException(ResultCode.PARAM_ERROR, "请求参数不能为空");
        }
        if (request.getAllocatedMenuId() == null) {
            throw new ParamException(ResultCode.PARAM_ERROR, "分配的菜单ID列表不能为空");
        }

        // 角色存在性和权限校验
        SysRole role = getById(request.getRoleId());
        Assert.isTrue(role != null, String.format("ID:【%s】的角色不存在", request.getRoleId()));
        Assert.isTrue(!RolesConstant.SUPER_ADMIN.equals(role.getRoleKey()), "超级管理员角色不允许修改权限");

        // 获取所有菜单并建立ID映射
        List<SysMenu> allMenus = sysMenuService.list();
        if (allMenus == null || allMenus.isEmpty()) {
            log.warn("系统中没有菜单数据，无法分配权限");
            return true;
        }

        Set<Long> validMenuIds = allMenus.stream()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());

        // 过滤有效的菜单ID
        Set<Long> validAllocatedMenuIds = request.getAllocatedMenuId().stream()
                .filter(validMenuIds::contains)
                .collect(Collectors.toSet());

        // 自动包含父级菜单ID（递归查找所有父级）
        Set<Long> finalMenuIds = new HashSet<>(validAllocatedMenuIds);
        for (Long menuId : validAllocatedMenuIds) {
            addParentMenuIds(menuId, allMenus, finalMenuIds);
        }

        // 删除旧权限
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, role.getId()));

        // 如果有新权限则插入
        if (!finalMenuIds.isEmpty()) {
            List<SysRoleMenu> roleMenusToInsert = finalMenuIds.stream()
                    .filter(Objects::nonNull)
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(request.getRoleId());
                        sysRoleMenu.setMenuId(menuId);
                        return sysRoleMenu;
                    })
                    .collect(Collectors.toList());

            if (!roleMenusToInsert.isEmpty()) {
                return sysRoleMenuService.saveBatch(roleMenusToInsert);
            }
        }

        // 没有分配新权限，视为操作成功
        return true;
    }

    /**
     * 递归添加父级菜单ID
     *
     * @param menuId    当前菜单ID
     * @param allMenus  所有菜单列表
     * @param resultSet 结果集合
     */
    private void addParentMenuIds(Long menuId, List<SysMenu> allMenus, Set<Long> resultSet) {
        if (menuId == null) {
            return;
        }
        // 查找当前菜单
        SysMenu currentMenu = allMenus.stream()
                .filter(menu -> menuId.equals(menu.getId()))
                .findFirst()
                .orElse(null);

        if (currentMenu != null && currentMenu.getParentId() != null && currentMenu.getParentId() > 0) {
            Long parentId = currentMenu.getParentId();
            if (!resultSet.contains(parentId)) {
                resultSet.add(parentId);
                // 递归添加父级的父级
                addParentMenuIds(parentId, allMenus, resultSet);
            }
        }
    }

}




