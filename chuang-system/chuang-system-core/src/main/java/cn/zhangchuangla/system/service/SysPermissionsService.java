package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.request.permissions.SysPermissionsListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    void saveUserPermissionsToRedis(Long userId, final int expireTime);

    /**
     * 获取权限列表
     *
     * @param request 请求参数
     * @return 返回权限列表
     */
    Page<SysPermissions> listPermissions(SysPermissionsListRequest request);
}
