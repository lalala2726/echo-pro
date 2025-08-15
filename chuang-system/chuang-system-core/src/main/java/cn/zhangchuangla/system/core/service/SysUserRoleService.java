package cn.zhangchuangla.system.core.service;

import cn.zhangchuangla.system.core.model.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户角色服务接口
 *
 * @author Chuang
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
     */
    void deleteUserRoleAssociation(Long userId);


    /**
     * 删除用户角色关联角色信息
     *
     * @param userIds 用户ID集合
     */
    void deleteUserRoleAssociation(List<Long> userIds);


    /**
     * 添加用户角色关联
     *
     * @param roleId 角色ID列表
     * @param userId 用户ID
     */
    void addUserRoleAssociation(List<Long> roleId, Long userId);

    /**
     * 判断角色是否被用户关联
     *
     * @param roleIds 角色ID列表
     * @return true:被关联 false:未被关联
     */
    boolean isRoleAssignedToUsers(List<Long> roleIds);
}
