package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.MetaVo;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单服务实现类
 *
 * @author zhangchuang
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Autowired
    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }


    /**
     * 构建菜单
     *
     * @param menus 菜单列表
     * @return 菜单树形结构
     */
    @Override
    public List<RouterVo> buildMenu(List<SysMenu> menus) {
        return buildMenuTreeRecursive(0L, menus);
    }

    /**
     * 根据用户ID获取菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> getMenuByUserId(Long userId) {
        boolean superAdmin = SecurityUtils.isSuperAdmin();
        //管理员获取所有菜单
        if (superAdmin) {
            return list();
        }
        return sysMenuMapper.getMenuByUserId(userId);
    }

    /**
     * 构建菜单树形结构
     *
     * @param parentId 父级菜单ID
     * @param menus    菜单
     * @return 菜单树形结构
     */
    private List<RouterVo> buildMenuTreeRecursive(Long parentId, List<SysMenu> menus) {
        return menus.stream()
                .filter(menu -> {
                    Long menuParentId = menu.getParentId();
                    return menuParentId != null && menuParentId.equals(parentId);
                })
                .map(menu -> {
                    RouterVo routerVo = new RouterVo();
                    routerVo.setName(menu.getName());
                    routerVo.setPath(menu.getPath());
                    routerVo.setComponent(getComponent(menu));
                    routerVo.setHidden(Constants.IS_HIDDEN.equals(menu.getHidden()));
                    routerVo.setMeta(buildMetaVo(menu));
                    //递归构建子菜单
                    List<RouterVo> children = buildMenuTreeRecursive(menu.getMenuId(), menus);
                    if (!children.isEmpty()) {
                        routerVo.setChildren(children);
                    }
                    return routerVo;
                })
                .toList();
    }


    /**
     * 构建菜单元数据
     *
     * @param menu 菜单
     * @return MetaVo
     */
    private MetaVo buildMetaVo(SysMenu menu) {
        MetaVo metaVo = new MetaVo();
        metaVo.setTitle(menu.getTitle());
        metaVo.setIcon(menu.getIcon());
        metaVo.setRank(menu.getSort());
        metaVo.setActiveMenu(menu.getRedirect());
        return metaVo;
    }

    private String getComponent(SysMenu menu) {
        return switch (menu.getMenuType()) {
            case Constants.MENU_TYPE_DIRECTORY -> "";
            case Constants.MENU_TYPE_MENU -> menu.getComponent();
            case Constants.MENU_TYPE_BUTTON -> "";
            default -> "InnerLink";
        };
    }
}
