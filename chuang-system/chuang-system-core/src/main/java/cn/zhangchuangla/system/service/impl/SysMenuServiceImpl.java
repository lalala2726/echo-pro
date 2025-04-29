package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Route;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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
        Set<String> roleSet = sysRoleService.getRoleSetByUserId(userId);
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
        return buildMenus(menus, 0L);
    }

    /**
     * 递归构造前端需要的路由界面
     *
     * @param sysMenus 菜单列表
     * @param parentId 父级ID
     * @return 返回前端需要的路由界面
     */
    private List<RouteVo> buildMenus(List<SysMenu> sysMenus, Long parentId) {
        return sysMenus.stream()
                // 过滤掉按钮类型菜单
                .filter(sysMenu -> !Constants.MenuConstants.TYPE_BUTTON.equals(sysMenu.getMenuType()))
                .filter(sysMenu -> sysMenu.getParentId().equals(parentId))
                .map(sysMenu -> {
                    RouteVo routeVo = new RouteVo();
                    String component = getComponent(sysMenu);
                    String path = getPath(sysMenu);

                    routeVo.setComponent(component);
                    routeVo.setPath(path);
                    routeVo.setName(sysMenu.getMenuName());
                    // 递归构建子菜单
                    routeVo.setChildren(buildMenus(sysMenus, sysMenu.getMenuId()));
                    return routeVo;
                })
                .toList();
    }

    private String getComponent(SysMenu sysMenu) {
        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType())) {
            return Constants.MenuConstants.LAYOUT;
        }
        if (Constants.MenuConstants.TYPE_MENU.equals(sysMenu.getMenuType())) {
            return Constants.MenuConstants.INNER_LINK;
        }
        return sysMenu.getComponent();
    }

    private String getPath(SysMenu sysMenu) {
        String path = sysMenu.getPath();
        //Path前端必须以/开头如果开头则
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

}
