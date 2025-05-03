package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysRoleMenuMapper;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chuang
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

}




