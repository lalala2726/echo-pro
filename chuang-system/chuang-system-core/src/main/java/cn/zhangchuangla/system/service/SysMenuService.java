package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRolePermRequest;
import cn.zhangchuangla.system.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.model.vo.role.SysRolePermVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务接口
 *
 * @author Chuang
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getMenuListByUserId(Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRolePermSelectedByRoleId(Long roleId);

    /**
     * 构造前端需要的路由界面
     *
     * @param menus 菜单列表
     * @return 返回前端需要的路由界面
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenu getMenuById(Long menuId);

    /**
     * 查询菜单列表
     *
     * @param sysMenu 菜单信息
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu sysMenu);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 构建前端选择菜单树（Option结构）
     *
     * @param menus 菜单列表
     * @return Option树列表
     */
    List<Option<Long>> buildMenuTree(List<SysMenu> menus);

    /**
     * 构建前端选项树
     *
     * @param menus 菜单列表
     * @return 选项树
     */
    List<Option<Long>> buildMenuOptionTree(List<SysMenu> menus);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 结果
     */
    boolean insertMenu(SysMenu menu);

    /**
     * 修改菜单
     *
     * @param request 菜单信息
     * @return 结果
     */
    boolean updateMenu(SysMenuUpdateRequest request);

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    boolean deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    boolean checkMenuNameUnique(SysMenu menu);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(Long menuId);


    /**
     * 更新角色菜单权限
     *
     * @param request 菜单更新角色权限请求
     * @return 结果
     */
    boolean updateRoleMenus(SysMenuUpdateRolePermRequest request);

    /**
     * 获取菜单路由列表
     *
     * @param onlyParent 是否只查询父级菜单
     * @return 菜单路由列表
     */
    List<Option<String>> getMenuOptions(boolean onlyParent);

    /**
     * 添加菜单
     *
     * @param request 请求参数
     * @return 结果
     */
    boolean addMenu(SysMenuAddRequest request);

    /**
     * 根据角色ID获取菜单权限信息
     *
     * @param roleId 角色ID
     * @return 菜单权限信息
     */
    SysRolePermVo getRolePermByRoleId(Long roleId);

    /**
     * 更新角色权限
     *
     * @param request 请求参数
     * @return 结果
     */
    boolean updateRolePermission(SysUpdateRolePermissionRequest request);

    /**
     * 根据用户ID获取权限字符串集合
     *
     * @param userId 用户ID
     * @return 权限字符串集合
     */
    Set<String> getUserPermissionByUserId(Long userId);
}
