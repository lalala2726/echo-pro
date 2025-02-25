package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author zhangchuang
 */
public interface SysPermissionsService extends IService<SysPermissions> {


    /**
     * 根据角色id获取权限
     *
     * @param roleId 角色ID
     * @return 返回角色对应的权限
     */
    List<SysPermissions> getPermissionsByRoleId(Long roleId);


    /**
     * 根据用户id获取权限
     *
     * @param userId 用户ID
     * @return 返回用户对应的权限
     */
    List<SysPermissions> getPermissionsByUserId(Long userId);


}
