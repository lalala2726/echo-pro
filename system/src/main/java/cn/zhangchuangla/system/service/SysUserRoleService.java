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


}
