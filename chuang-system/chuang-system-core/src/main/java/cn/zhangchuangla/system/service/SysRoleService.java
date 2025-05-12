package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleUpdateRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 角色服务接口
 *
 * @author Chuang
 */
public interface SysRoleService extends IService<SysRole> {


    /**
     * 角色列表
     *
     * @param request 查询参数
     * @return 分页列表
     */
    Page<SysRole> roleList(SysRoleQueryRequest request);

    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<SysRole> getRoleListByUserId(Long userId);


    /**
     * 根据用户id获取角色
     *
     * @param userId 用户ID
     * @return 返回Set集合
     */
    Set<String> getRoleSetByUserId(Long userId);

    /**
     * 添加角色信息
     *
     * @param roleAddRequest 请求参数
     */
    boolean addRoleInfo(SysRoleAddRequest roleAddRequest);

    /**
     * 判断角色名是否存在
     *
     * @param roleName 角色名称
     * @return true 存在，false不存在
     */
    boolean isRoleNameExist(String roleName);

    /**
     * 判断角色权限字符串是否存在
     *
     * @param roleKey 角色权限字符串
     * @return true 存在，false不存在
     */
    boolean isRoleKeyExist(String roleKey);

    /**
     * 修改角色信息
     *
     * @param request 修改角色信息
     * @return true 修改成功，false 修改失败
     */
    boolean updateRoleInfo(SysRoleUpdateRequest request);

    /**
     * 刷新权限缓存(所有角色)
     */
    void refreshRolePermsCache();


    /**
     * 获取部门下拉列表
     *
     * @return 下拉列表
     */
    List<Option<Long>> getRoleOptions();

    /**
     * 根据用户ID获取角色ID集合
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    Set<Long> getUserRoleIdByUserId(Long userId);

    /**
     * 删除角色信息，支持批量删除
     *
     * @param ids 角色ID集合
     * @return 删除结果
     */
    boolean deleteRoleInfo(List<Long> ids);

    /**
     * 根据角色ID集合获取角色权限字符串集合
     *
     * @param roleId 角色ID集合
     * @return 角色权限字符串集合
     */
    Set<String> getRoleSetByRoleId(List<Long> roleId);

    /**
     * 根据角色ID集合获取角色权限字符串集合
     *
     * @param roleId 角色ID
     * @return 角色权限字符串集合
     */
    Set<String> getRoleSetByRoleId(Long roleId);
}
