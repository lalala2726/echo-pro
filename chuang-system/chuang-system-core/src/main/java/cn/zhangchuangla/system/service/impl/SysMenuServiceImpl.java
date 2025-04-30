package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.mapper.SysRoleMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.MetaVo;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private final SysRoleMenuMapper roleMenuMapper;

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
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        return buildMenus(menus, 0L);
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu getMenuById(Long menuId) {
        return getById(menuId);
    }

    /**
     * 查询菜单列表
     *
     * @param sysMenu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu sysMenu) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        // 根据菜单名称模糊查询
        if (sysMenu.getMenuName() != null && !sysMenu.getMenuName().isEmpty()) {
            queryWrapper.like(SysMenu::getMenuName, sysMenu.getMenuName());
        }
        // 根据菜单状态查询
        if (sysMenu.getStatus() != null && !sysMenu.getStatus().isEmpty()) {
            queryWrapper.eq(SysMenu::getStatus, sysMenu.getStatus());
        }
        // 排序
        queryWrapper.orderByAsc(SysMenu::getParentId);
        queryWrapper.orderByAsc(SysMenu::getOrderNum);

        return list(queryWrapper);
    }

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = getMenuListByUserId(userId);
        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        // 查找所有的一级菜单
        return menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .peek(menu -> {
                    // 递归获取子菜单
                    menu.setChildren(getChildrenMenus(menu, menus));
                })
                .sorted(Comparator.comparing(SysMenu::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 获取子菜单
     *
     * @param parentMenu 父菜单
     * @param menus      所有菜单
     * @return 子菜单列表
     */
    private List<SysMenu> getChildrenMenus(SysMenu parentMenu, List<SysMenu> menus) {
        return menus.stream()
                .filter(menu -> menu.getParentId().equals(parentMenu.getMenuId()))
                .map(menu -> {
                    // 递归获取子菜单
                    menu.setChildren(getChildrenMenus(menu, menus));
                    return menu;
                })
                .sorted(Comparator.comparing(SysMenu::getOrderNum))
                .collect(Collectors.toList());
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return sysMenuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertMenu(SysMenu menu) {
        return save(menu);
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenu menu) {
        return updateById(menu);
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenuById(Long menuId) {
        return removeById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        Long menuId = menu.getMenuId() == null ? -1L : menu.getMenuId();
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getMenuName, menu.getMenuName());
        queryWrapper.eq(SysMenu::getParentId, menu.getParentId());
        SysMenu info = getOne(queryWrapper);
        return info == null || info.getMenuId().equals(menuId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMenu::getParentId, menuId);
        return count(queryWrapper) > 0;
    }

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return roleMenuMapper.checkMenuExistRole(menuId) > 0;
    }

    /**
     * 递归构造前端需要的路由界面
     *
     * @param sysMenus 菜单列表
     * @param parentId 父级ID
     * @return 返回前端需要的路由界面
     */
    private List<RouterVo> buildMenus(List<SysMenu> sysMenus, Long parentId) {
        return sysMenus.stream()
                // 过滤掉按钮类型菜单
                .filter(sysMenu -> !Constants.MenuConstants.TYPE_BUTTON.equals(sysMenu.getMenuType()))
                .filter(sysMenu -> sysMenu.getParentId().equals(parentId))
                // 按排序进行排序
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(sysMenu -> {
                    RouterVo routerVo = new RouterVo();
                    // 设置路由名称
                    routerVo.setName(sysMenu.getRouteName());
                    // 设置路由路径
                    routerVo.setPath(getPath(sysMenu));
                    // 设置组件
                    routerVo.setComponent(getComponent(sysMenu));
                    // 设置查询参数
                    routerVo.setQuery(sysMenu.getQuery());
                    // 设置是否隐藏路由
                    routerVo.setHidden(Constants.MenuConstants.HIDDEN.equals(sysMenu.getVisible()));

                    // 递归构建子菜单
                    List<RouterVo> childrenMenus = buildMenus(sysMenus, sysMenu.getMenuId());
                    // 如果存在子菜单
                    if (!childrenMenus.isEmpty()) {
                        // 如果子菜单数量大于1个，设置为总是显示
                        routerVo.setAlwaysShow(childrenMenus.size() > 1);
                        routerVo.setChildren(childrenMenus);
                        // 如果是目录类型且有子菜单，设置重定向到第一个子菜单
                        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType())) {
                            routerVo.setRedirect(childrenMenus.get(0).getPath());
                        }
                    }

                    // 设置元数据
                    routerVo.setMeta(getMetaVo(sysMenu));

                    return routerVo;
                })
                .collect(Collectors.toList());
    }

    private String getComponent(SysMenu sysMenu) {
        // 如果是目录类型
        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType())) {
            if (sysMenu.getParentId() == 0L) {
                // 顶级目录使用 Layout
                return Constants.MenuConstants.LAYOUT;
            } else {
                // 二级及以下目录使用 ParentView
                return Constants.MenuConstants.PARENT_VIEW;
            }
        }
        // 如果是菜单类型
        if (Constants.MenuConstants.TYPE_MENU.equals(sysMenu.getMenuType())) {
            // 如果是外链
            if (Integer.valueOf(Constants.MenuConstants.IS_EXTERNAL_LINK).equals(sysMenu.getIsFrame())) {
                return Constants.MenuConstants.INNER_LINK;
            }
            // 如果指定了组件
            if (sysMenu.getComponent() != null && !sysMenu.getComponent().isEmpty()) {
                return sysMenu.getComponent();
            }
        }

        return sysMenu.getComponent();
    }

    private String getPath(SysMenu sysMenu) {
        String path = sysMenu.getPath();

        // 如果是一级目录
        if (sysMenu.getParentId() == 0L) {
            // 确保路径以 / 开头
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
        } else {
            // 如果是非一级目录或菜单，不需要以 / 开头
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
        }

        return path;
    }

    private MetaVo getMetaVo(SysMenu sysMenu) {
        MetaVo metaVo = new MetaVo();
        // 设置标题（菜单名称）
        metaVo.setTitle(sysMenu.getMenuName());
        // 设置图标
        metaVo.setIcon(sysMenu.getIcon());
        // 设置是否在菜单中显示
        metaVo.setShowLink(!Constants.MenuConstants.HIDDEN.equals(sysMenu.getVisible()));
        // 设置排序字段
        metaVo.setRank(sysMenu.getSort());
        // 设置是否缓存
        boolean isCache = Constants.MenuConstants.CACHE.equals(sysMenu.getIsCache());
        metaVo.setNoCache(!isCache);
        metaVo.setKeepAlive(isCache);

        // 设置链接地址（如果是外链）
        if (Integer.valueOf(Constants.MenuConstants.IS_EXTERNAL_LINK).equals(sysMenu.getIsFrame())) {
            metaVo.setLink(sysMenu.getPath());
        }

        // 如果有权限标识，设置权限信息
        if (sysMenu.getPermission() != null && !sysMenu.getPermission().isEmpty()) {
            metaVo.setAuths(new String[]{sysMenu.getPermission()});
        }

        return metaVo;
    }
}