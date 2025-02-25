package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ParamException;
import cn.zhangchuangla.system.mapper.SysRolePermissionsMapper;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.entity.SysRolePermissions;
import cn.zhangchuangla.system.service.SysRolePermissionsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangchuang
 */
@Service
public class SysRolePermissionsServiceImpl extends ServiceImpl<SysRolePermissionsMapper, SysRolePermissions>
        implements SysRolePermissionsService {


    /**
     * 根据角色id获取角色权限
     *
     * @param roleId 角色ID
     * @return 返回角色对应的权限列表
     */
    @Override
    public List<SysPermissions> getRolePermissionsByRoleId(Long roleId) {
        if (roleId <= 0) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "角色ID不能小于等于零");
        }
        LambdaQueryWrapper<SysRolePermissions> permissionsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionsLambdaQueryWrapper.eq(SysRolePermissions::getRoleId, roleId);
        List<SysRolePermissions> list = list(permissionsLambdaQueryWrapper);
        return null;
    }

    /**
     * 根据角色id获取角色权限(多个角色)
     *
     * @param roleIds 角色ID
     * @return 返回角色对应的权限列表
     */
    @Override
    public List<SysPermissions> getRolePermissionsByRoleId(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new ParamException(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        LambdaQueryWrapper<SysRolePermissions> permissionsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        permissionsLambdaQueryWrapper.in(SysRolePermissions::getRoleId, roleIds);
        return null;
    }
}




