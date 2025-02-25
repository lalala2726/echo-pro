package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.entity.SysRolePermissions;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色权限服务接口
 *
 * @author zhangchuang
 */
public interface SysRolePermissionsService extends IService<SysRolePermissions> {

    /**
     * 根据角色id获取角色权限
     *
     * @param roleId 角色ID
     * @return 返回角色对应的权限列表
     */
    List<SysPermissions> getRolePermissionsByRoleId(Long roleId);

    /**
     * 根据角色id获取角色权限(多个角色)
     *
     * @param roleIds 角色ID
     * @return 返回角色对应的权限列表
     */
    List<SysPermissions> getRolePermissionsByRoleId(List<Long> roleIds);

}
