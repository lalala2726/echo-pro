package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 根据角色ID获取菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getSelectedMenuIdByRoleId(Long roleId);

}
