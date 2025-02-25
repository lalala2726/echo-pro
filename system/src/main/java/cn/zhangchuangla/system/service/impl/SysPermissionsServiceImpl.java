package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.SysPermissionsMapper;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.entity.SysRolePermissions;
import cn.zhangchuangla.system.model.entity.SysUserRole;
import cn.zhangchuangla.system.service.SysPermissionsService;
import cn.zhangchuangla.system.service.SysRolePermissionsService;
import cn.zhangchuangla.system.service.SysUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
public class SysPermissionsServiceImpl extends ServiceImpl<SysPermissionsMapper, SysPermissions>
        implements SysPermissionsService {

    private final SysRolePermissionsService sysRolePermissionsService;
    private final SysUserRoleService sysUserRoleService;

    public SysPermissionsServiceImpl(SysRolePermissionsService sysRolePermissionsService, SysUserRoleService sysUserRoleService) {
        this.sysRolePermissionsService = sysRolePermissionsService;
        this.sysUserRoleService = sysUserRoleService;
    }


    /**
     * 在角色权限对应表从传入的角色ID中获取权限ID，再根据权限ID获取权限
     *
     * @param roleId 角色ID
     * @return 返回角色对应的权限
     */
    @Override
    public List<SysPermissions> getPermissionsByRoleId(Long roleId) {
        ParamsUtils.paramNotIsZeroOrBlank(roleId);
        LambdaQueryWrapper<SysRolePermissions> sysRolePermissionsLambdaQueryWrapper =
                new LambdaQueryWrapper<SysRolePermissions>()
                        .eq(SysRolePermissions::getRoleId, roleId);
        List<SysRolePermissions> sysRolePermissions = sysRolePermissionsService.list(sysRolePermissionsLambdaQueryWrapper);
        List<Long> PermissionId = sysRolePermissions.stream()
                .map(SysRolePermissions::getPermissionId).toList();
        LambdaQueryWrapper<SysPermissions> lambdaQueryWrapper = new LambdaQueryWrapper<SysPermissions>()
                .in(SysPermissions::getPermissionId, PermissionId);
        return list(lambdaQueryWrapper);
    }


    /**
     * 根据用户id获取权限
     *
     * @param userId 用户ID
     * @return 返回用户对应的权限
     */
    @Override
    public List<SysPermissions> getPermissionsByUserId(Long userId) {
        ParamsUtils.paramNotIsZeroOrBlank(userId);
        List<SysUserRole> userRoles = sysUserRoleService.getUserRoleByUserId(userId);
        List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).toList();
        LambdaQueryWrapper<SysRolePermissions> rolePermissionsLambdaQueryWrapper =
                new LambdaQueryWrapper<SysRolePermissions>()
                        .in(SysRolePermissions::getRoleId, roleIds);
        List<SysRolePermissions> sysRolePermissions = sysRolePermissionsService.list(rolePermissionsLambdaQueryWrapper);
        List<Long> sysPermissionIds = sysRolePermissions.stream().map(SysRolePermissions::getPermissionId).toList();
        LambdaQueryWrapper<SysPermissions> lambdaQueryWrapper = new LambdaQueryWrapper<SysPermissions>()
                .in(SysPermissions::getPermissionId, sysPermissionIds);
        return list(lambdaQueryWrapper);
    }
}




