package cn.zhangchuangla.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.zhangchuangla.system.mapper.SysRoleMenuMapper;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.model.request.menu.AssignedMenuIdsRequest;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhangchuang
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu>
        implements SysRoleMenuService {

    /**
     * 根据角色ID获取已分配的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> getSelectedMenuIdByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> eq = new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> list = list(eq);
        return list.stream()
                .map(SysRoleMenu::getMenuId)
                .toList();
    }

    /**
     * 更改角色菜单的权限
     *
     * @param assignedMenuIdsRequest 角色ID和菜单ID集合
     * @return 是否修改成功
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRoleMenus(AssignedMenuIdsRequest assignedMenuIdsRequest) {
        List<Long> menuIds = assignedMenuIdsRequest.getMenuIds();
        Long roleId = assignedMenuIdsRequest.getRoleId();
        //删除角色菜单权限
        LambdaQueryWrapper<SysRoleMenu> eq = new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId);
        remove(eq);

        //分配角色菜单权限
        if (CollectionUtil.isNotEmpty(menuIds)) {
            List<SysRoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> new SysRoleMenu(roleId, menuId))
                    .collect(Collectors.toList());
            return this.saveBatch(roleMenus);
        }
        return false;
    }
}




