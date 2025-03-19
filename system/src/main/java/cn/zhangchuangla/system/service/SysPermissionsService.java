package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * 权限服务接口
 *
 * @author zhangchuang
 */
public interface SysPermissionsService extends IService<SysPermissions> {

    /**
     * 根据角色名称查询权限集合
     *
     * @param roleName 角色名称
     * @return 返回角色权限集合
     */
    Set<String> getPermissionsByRoleName(String roleName);


    /**
     * 根据用户id查询权限集合
     *
     * @param id 用户ID
     * @return 返回用户权限集合
     */
    Set<String> getPermissionsByUserId(Long id);

    /**
     * 保存用户权限到Redis
     *
     * @param userId 用户ID
     */
    void saveUserPermissionsToRedis(Long userId, final long expireTime);
}
