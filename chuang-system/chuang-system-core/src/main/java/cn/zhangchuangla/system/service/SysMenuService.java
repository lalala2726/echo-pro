package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuQueryRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.vo.menu.MenuOption;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.model.vo.menu.SysMenuListVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/5 12:47
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询系统菜单列表
     *
     * @param request 查询参数
     * @return 系统菜单列表
     */
    List<SysMenu> listMenu(SysMenuQueryRequest request);

    /**
     * 根据菜单ID查询菜单信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu getMenuById(Long menuId);

    /**
     * 添加菜单
     *
     * @param request 添加参数
     * @return 是否添加成功
     */
    boolean addMenu(SysMenuAddRequest request);

    /**
     * 修改菜单
     *
     * @param request 修改参数
     * @return 是否修改成功
     */
    boolean updateMenu(SysMenuUpdateRequest request);

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否删除成功
     */
    boolean deleteMenu(Long menuId);


    /**
     * 根据角色名称获取菜单列表
     *
     * @param roleName 角色名称
     * @return 菜单列表
     */
    List<SysMenu> listSysMenuByRoleName(Set<String> roleName);

    /**
     * 构造前端路由
     *
     * @param sysMenu 系统
     * @return 返回前端路由
     */
    List<RouterVo> buildRouteVo(List<SysMenu> sysMenu);

    /**
     * 获取菜单选项
     *
     * @return 菜单选项
     */
    List<Option<String>> getMenuOptions();

    /**
     * 检查菜单名称是否已经存在
     *
     * @param id   菜单ID
     * @param name 菜单名称
     * @return 是否已经存在
     */
    boolean isMenuNameExists(Long id, String name);


    /**
     * 检查菜单路径是否已经存在
     *
     * @param id   菜单ID
     * @param path 菜单路径
     * @return 是否已经存在
     */
    boolean isMenuPathExists(Long id, String path);


    /**
     * 获取菜单树
     *
     * @return 菜单树
     */
    List<MenuOption> menuTree();

    List<SysMenuListVo> buildMenuList(List<SysMenu> menuList);

    /**
     * 判断菜单是否有子菜单
     *
     * @param menuId 菜单ID
     * @return 是否有子菜单
     */
    boolean hasChildren(Long menuId);
}
