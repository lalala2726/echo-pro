package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.SysRoleQueryRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author zhangchuang
*/
public interface SysRoleService extends IService<SysRole> {


    /**
     * 角色列表
     * @param request  查询参数
     * @return  分页列表
     */
    Page<SysRole> RoleList(SysRoleQueryRequest request);
}
