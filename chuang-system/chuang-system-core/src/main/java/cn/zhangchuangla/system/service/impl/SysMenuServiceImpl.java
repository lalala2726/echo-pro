package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.MetaVo;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务实现类
 *
 * @author zhangchuang
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysRoleService sysRoleService;
    private final SysMenuMapper sysMenuMapper;

    /**
     * 更新角色权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID数组
     * @return 是否成功
     */
    @Override
    public boolean updateRoleMenu(Long roleId, Long[] menuIds) {
        return false;
    }

    /**
     * 获取角色菜单列表
     *
     * @param userId 角色ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> listMenu(Long userId) {
        //1.判断当前角色是否为管理员
        Set<String> set = sysRoleService.getRoleSetByUserId(userId);
        if (set.contains(SysRolesConstant.SUPER_ADMIN)) {
            //2.如果是管理员，查询所有菜单
            return list();
        }
        //3.如果不是管理员，查询当前角色的菜单
        //4.返回菜单列表
        return sysMenuMapper.listMenuByUserId(userId);
    }

    /**
     * 构建路由信息
     *
     * @param sysMenus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> sysMenus) {
        return buildMenuTree(sysMenus, 0L);
    }

    /**
     * 递归构建菜单树
     *
     * @param sysMenus 菜单列表
     * @param parentId 父级菜单ID
     * @return 菜单树
     */
    private List<RouterVo> buildMenuTree(List<SysMenu> sysMenus, Long parentId) {
        return sysMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MetaVo meta = getMeta(menu);
                    RouterVo router = new RouterVo();
                    router.setName(menu.getMenuName());
                    router.setPath(menu.getPath());
                    router.setMeta(meta);
                    router.setComponent(menu.getComponent());
                    // 递归构造子菜单
                    List<RouterVo> children = buildMenuTree(sysMenus, menu.getMenuId());
                    if (!children.isEmpty()) {
                        router.setChildren(children);
                    }
                    return router;
                }).toList();
    }

    /**
     * 构建路由元信息
     *
     * @param menu 菜单对象
     * @return 路由元信息
     */
    private MetaVo getMeta(SysMenu menu) {
        boolean isCache = Constants.IS_CACHE.equals(menu.getIsCache());
        MetaVo meta = new MetaVo();
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setNoCache(isCache);
        return meta;
    }

    /**
     * 获取组件
     *
     * @param sysMenu 菜单对象
     * @return 组件路径
     */
    private String getComponent(SysMenu sysMenu) {

        return "";
    }

    /**
     * 获取路由路径
     *
     * @return 路由路径
     */
    private String getRoutePath() {
        return "";
    }

    /**
     * 获取路由名称
     *
     * @return 路由名称
     */
    private String getRouteName() {
        return "";
    }

    /**
     * 判断是否有子菜单
     *
     * @param sysMenu 菜单对象
     * @return 是否有子菜单
     */
    private boolean hasChildren(SysMenu sysMenu) {
        return false;
    }
}




