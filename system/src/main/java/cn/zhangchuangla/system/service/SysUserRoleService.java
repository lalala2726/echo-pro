package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户角色服务接口
 *
 * @author zhangchuang
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户id获取用户角色
     *
     * @param userId 用户ID
     * @return 用户角色
     */
    List<SysUserRole> getUserRoleByUserId(Long userId);


    /**
     * 删除用户角色关联角色信息
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUserRoleAssociation(Long userId);


    /**
     * 添加用户角色关联,使用此方法前请先调用removeUserRoleAssociation方法删除用户角色关联
     *
     * @param roleId 角色ID列表
     * @param userId 用户ID
     */
    boolean addUserRoleAssociation(List<Long> roleId, Long userId);

}
