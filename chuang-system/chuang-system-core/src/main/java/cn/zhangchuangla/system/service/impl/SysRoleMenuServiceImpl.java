package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysRoleMenuMapper;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu>
        implements SysRoleMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

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
     * 检查菜单是否已分配给角色
     *
     * @param menuId 菜单ID
     * @return 分配数量
     */
    @Override
    public boolean isMenuAssignedToRoles(Long menuId) {
        LambdaQueryWrapper<SysRoleMenu> eq = new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getMenuId, menuId);
        return count(eq) > 0;
    }

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return sysRoleMenuMapper.selectMenuListByRoleId(roleId);
    }

}




