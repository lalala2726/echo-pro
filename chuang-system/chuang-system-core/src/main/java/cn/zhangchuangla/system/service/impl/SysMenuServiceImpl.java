package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 系统菜单服务实现类
 *
 * @author zhangchuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysRoleService sysRoleService;
    private final SysMenuMapper sysMenuMapper;


    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> getMenuListByUserId(Long userId) {
        Set<String> roleSet = sysRoleService.getUserRoleSetByUserId(userId);
        if (roleSet.contains(SysRolesConstant.SUPER_ADMIN)) {
            //如果是超级管理员获取全部菜单
            return list();
        }
        return sysMenuMapper.getMenuListByUserId(userId);
    }

    /**
     * 构造前端需要的路由界面
     *
     * @param menus 菜单列表
     * @return 返回前端需要的路由界面
     */
    @Override
    public List<RouteVo> buildMenus(List<SysMenu> menus) {
        return List.of();
    }
}
