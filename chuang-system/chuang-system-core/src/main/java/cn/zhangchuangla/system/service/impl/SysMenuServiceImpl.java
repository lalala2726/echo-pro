package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuQueryRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.vo.menu.MenuOption;
import cn.zhangchuangla.system.model.vo.menu.MetaVo;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.model.vo.menu.SysMenuListVo;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/5 12:47
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    private final long ROOT_MENU_ID = 0L;


    /**
     * 获取菜单列表
     *
     * @param request 查询参数
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> listMenu(SysMenuQueryRequest request) {
        return list();
    }


    /**
     * 根据菜单ID查询菜单信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu getMenuById(Long menuId) {
        return getById(menuId);
    }

    /**
     * 新增菜单
     *
     * @param request 菜单信息
     * @return 是否成功
     */
    @Override
    public boolean addMenu(SysMenuAddRequest request) {
        SysMenu sysMenu = BeanCotyUtils.copy(request, SysMenu.class);
        return save(sysMenu);
    }

    /**
     * 根据Path生成唯一路由名
     *
     * @param sysMenu 菜单
     * @return 路由名
     */
    private String generateName(SysMenu sysMenu) {
        return sysMenu.getPath().replace("/", "");
    }

    /**
     * 修改菜单
     *
     * @param request 菜单信息
     * @return 是否成功
     */
    @Override
    public boolean updateMenu(SysMenuUpdateRequest request) {
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);
        return updateById(sysMenu);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    @Override
    public boolean deleteMenu(Long menuId) {
        return removeById(menuId);
    }

    /**
     * 根据角色名查询菜单列表
     *
     * @param roleName 角色名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> listSysMenuByRoleName(Set<String> roleName) {
        //超级管理员拥有所有权限
        if (roleName.contains(SysRolesConstant.SUPER_ADMIN)) {
            return list();
        }
        return sysMenuMapper.listSysMenuByRoleName(roleName);
    }

    /**
     * 构建菜单路由
     *
     * @param sysMenu 菜单
     * @return 路由
     */
    @Override
    public List<RouterVo> buildRouteVo(List<SysMenu> sysMenu) {
        return buildRouterTree(sysMenu, ROOT_MENU_ID);
    }

    /**
     * 递归构建路由树
     *
     * @param menuList 菜单列表
     * @param parentId 父菜单ID
     * @return 路由树
     */
    private List<RouterVo> buildRouterTree(List<SysMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .map(menu -> {
                    RouterVo routerVo = new RouterVo();
                    routerVo.setName(menu.getName());
                    routerVo.setPath(menu.getPath());
                    routerVo.setComponent(menu.getComponent());
                    routerVo.setMeta(setMateVo(menu));
                    // 递归构建子路由
                    List<RouterVo> children = buildRouterTree(menuList, menu.getId());
                    if (!children.isEmpty()) {
                        routerVo.setChildren(children);
                    }
                    return routerVo;
                })
                .toList();
    }


    /**
     * 设置路由元信息
     *
     * @param sysMenu 菜单信息
     * @return 路由元信息
     */
    private MetaVo setMateVo(SysMenu sysMenu) {
        MetaVo metaVo = new MetaVo();
        metaVo.setTitle(sysMenu.getTitle());
        metaVo.setOrder(sysMenu.getSort());
        metaVo.setIcon(sysMenu.getIcon());
        return metaVo;
    }

    /**
     * 获取菜单选项
     *
     * @return 菜单选项
     */
    @Override
    public List<Option<String>> getMenuOptions() {
        return list().stream()
                .map(menu -> new Option<>(menu.getId().toString(), menu.getTitle()))
                .toList();
    }

    /**
     * 检查菜单名称是否已存在
     *
     * @param id   菜单ID
     * @param name 菜单名称
     * @return true已存在，false不存在
     */
    @Override
    public boolean isMenuNameExists(Long id, String name) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getName, name);
        if (id != null) {
            queryWrapper.ne(SysMenu::getId, id);
        }
        return count(queryWrapper) > 0;
    }

    /**
     * 判断菜单路径是否存在
     *
     * @param id   菜单id
     * @param path 路径
     * @return true已存在，false不存在
     */
    @Override
    public boolean isMenuPathExists(Long id, String path) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getPath, path);
        if (id != null) {
            queryWrapper.ne(SysMenu::getId, id);
        }
        return count(queryWrapper) > 0;
    }

    /**
     * 获取菜单选项
     *
     * @return 菜单选项
     */
    @Override
    public List<MenuOption> menuTree() {
        List<SysMenu> list = list();
        return buildMenuTreeOption(list, ROOT_MENU_ID);
    }

    /**
     * 构建菜单列表
     *
     * @return 菜单列表
     */
    @Override
    public List<SysMenuListVo> buildMenuList(List<SysMenu> list) {
        return buildMenuList(list, ROOT_MENU_ID);
    }

    private List<SysMenuListVo> buildMenuList(List<SysMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .map(menu -> {
                    SysMenuListVo sysMenuListVo = BeanCotyUtils.copyProperties(menu, SysMenuListVo.class);
                    sysMenuListVo.setChildren(buildMenuList(menuList, menu.getId()));
                    return sysMenuListVo;
                })
                .toList();
    }

    public List<MenuOption> buildMenuTreeOption(List<SysMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .map(menu -> {
                    MenuOption menuOption = new MenuOption();
                    menuOption.setId(menu.getId());
                    menuOption.setTitle(menu.getTitle());
                    menuOption.setIcon(menu.getIcon());
                    menuOption.setChildren(buildMenuTreeOption(menuList, menu.getId()));
                    return menuOption;
                })
                .toList();
    }
}
