package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.model.request.menu.AssignedMenuIdsRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    /**
     * 根据角色ID获取菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getSelectedMenuIdByRoleId(Long roleId);


    /**
     * 更改角色菜单的权限
     *
     * @param assignedMenuIdsRequest 角色ID和菜单ID集合
     * @return 是否修改成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateRoleMenus(AssignedMenuIdsRequest assignedMenuIdsRequest);
}
