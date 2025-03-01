package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.SysRoleQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author zhangchuang
 */
public interface SysRoleService extends IService<SysRole> {


    /**
     * 角色列表
     *
     * @param request 查询参数
     * @return 分页列表
     */
    Page<SysRole> RoleList(SysRoleQueryRequest request);

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
    Set<String> getUserRoleSet(Long userId);
}
