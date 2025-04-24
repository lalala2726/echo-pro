package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.MenuForm;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.model.vo.menu.MenuVo;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.model.vo.permission.MenuListVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author zhangchuang
 */
public interface SysMenuService extends IService<SysMenu> {


    /**
     * 获取菜单表格列表
     */
    List<MenuVo> listMenus(SysMenuListRequest request);

    /**
     * 获取菜单下拉列表
     *
     * @param onlyParent 是否只查询父级菜单
     */
    List<Option<Long>> listMenuOptions(boolean onlyParent);

    /**
     * 新增菜单
     *
     * @param menuForm 菜单表单对象
     */
    boolean saveMenu(MenuForm menuForm);

    /**
     * 获取路由列表
     */
    List<RouteVo> getCurrentUserRoutes();

    /**
     * 修改菜单显示状态
     *
     * @param menuId  菜单ID
     * @param visible 是否显示(1-显示 0-隐藏)
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * 获取菜单表单数据
     *
     * @param id 菜单ID
     */
    MenuForm getMenuForm(Long id);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    boolean deleteMenu(Long id);

    /**
     * 根据角色名称查询权限
     *
     * @param roleName 角色名称
     * @return 权限列表
     */
    Set<String> getPermissionsByRoleName(String roleName);

    /**
     * 根据角色名称集合查询权限
     *
     * @param roleSet 角色名称集合
     * @return 权限列表
     */
    Set<String> getPermissionsByRoleName(Set<String> roleSet);


    /**
     * 获取系统中所有的可用的权限
     *
     * @return 权限列表
     */
    List<MenuListVo> listPermission();

}
