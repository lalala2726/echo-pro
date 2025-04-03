package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.model.vo.menu.MetaVo;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * //todo 后续调试完毕，这边需要给前端路由完善
 * 菜单服务实现类
 *
 * @author zhangchuang
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {


    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public Page<SysMenu> listMenu(SysMenuListRequest sysMenuListRequest) {
        Page<SysMenu> sysMenuPage = new Page<>(sysMenuListRequest.getPageNum(), sysMenuListRequest.getPageSize());
        return sysMenuMapper.listMenu(sysMenuPage, sysMenuListRequest);
    }

    @Override
    public List<SysMenu> getMenuUserId(Long userId) {
        if (SecurityUtils.isSuperAdmin()) {
            // 超级管理员获取所有菜单
            return sysMenuMapper.selectList(null);
        }
        return sysMenuMapper.getMenuUserId(userId);
    }

    /**
     * 构建菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenu(List<SysMenu> menus) {
        if (CollectionUtils.isEmpty(menus)) {
            return null;
        }

        // 获取所有顶级菜单（父节点ID为0的菜单）
        List<SysMenu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == Constants.IS_PARENT_NODE)
                .collect(Collectors.toList());

        // 递归构建菜单树
        return buildRouterTree(rootMenus, menus);
    }

    /**
     * 递归构建路由树
     *
     * @param rootMenus 根菜单列表
     * @param allMenus  所有菜单列表
     * @return 路由树列表
     */
    private List<RouterVo> buildRouterTree(List<SysMenu> rootMenus, List<SysMenu> allMenus) {
        List<RouterVo> routers = new ArrayList<>();

        if (CollectionUtils.isEmpty(rootMenus)) {
            return routers;
        }

        // 遍历根菜单列表
        for (SysMenu menu : rootMenus) {
            RouterVo router = new RouterVo();
            router.setName(getRouteName(menu));

            String routerPath = getRouterPath(menu);
            router.setPath(routerPath);

            String component = getComponent(menu);
            router.setComponent(component);
            router.setHidden(Constants.IS_HIDDEN.equals(menu.getVisible()));
            router.setRedirect(getRedirect(menu));

            // 设置meta信息
            MetaVo metaVo = MetaVo.builder()
                    .title(menu.getTitle())
                    .icon(menu.getIcon())
                    .noCache(Constants.IS_CACHE.equals(menu.getIsCache()))
                    .build();
            router.setMeta(metaVo);

            // 查找当前菜单的子菜单
            List<SysMenu> childMenus = getChildMenus(menu.getMenuId(), allMenus);

            // 如果存在子菜单，递归构建子菜单树
            if (!CollectionUtils.isEmpty(childMenus)) {
                router.setChildren(buildRouterTree(childMenus, allMenus));
            }

            routers.add(router);
        }

        return routers;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    private String getRouteName(SysMenu menu) {
        // 使用菜单名称作为路由名称
        return StringUtils.capitalize(menu.getMenuName());
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    private String getRouterPath(SysMenu menu) {
        // 如果是外链
        if (Constants.IS_MENU_EXTERNAL_LINK.equals(menu.getIsFrame())) {
            StringUtils.isHttp(menu.getPath());
        }
        // 其他情况
        return menu.getPath();
    }

    /**
     * 获取指定父菜单ID的所有子菜单
     *
     * @param parentId 父菜单ID
     * @param allMenus 所有菜单列表
     * @return 子菜单列表
     */
    private List<SysMenu> getChildMenus(Long parentId, List<SysMenu> allMenus) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }

    /**
     * 获取菜单重定向地址
     *
     * @param menu 菜单
     * @return 返回重定向地址
     */
    private String getRedirect(SysMenu menu) {
        if (StringUtils.isNotBlank(menu.getRedirect())) {
            return menu.getRedirect();
        }
        return null;
    }

    /**
     * 获取菜单组件的类型
     *
     * @param menu 菜单
     * @return 返回组件类型
     */
    private String getComponent(SysMenu menu) {
        String component = "#";
        if (StringUtils.isNotBlank(menu.getComponent()) && isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isNotBlank(menu.getComponent()) && menu.getParentId()
                != Constants.IS_PARENT_NODE && isInnerLink(menu)) {
            component = Constants.INNER_LINK;
        } else if (StringUtils.isNotBlank(menu.getComponent()) && isParentView(menu)) {
            component = Constants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 判断菜单是否为父视图
     *
     * @param menu 菜单对象
     * @return 如果菜单是父视图则返回true，否则返回false
     */
    private boolean isParentView(SysMenu menu) {
        // 检查菜单的父ID是否不为0且菜单类型为目录
        return menu.getParentId() != Constants.IS_PARENT_NODE && Constants.MENU_TYPE_DIRECTORY.equals(menu.getMenuType());
    }

    /**
     * 判断菜单是否为内部菜单框架
     *
     * @param menu 菜单对象
     * @return 如果菜单是内部菜单框架则返回true，否则返回false
     */
    private boolean isMenuFrame(SysMenu menu) {
        // 检查菜单的父ID是否为0，菜单类型为菜单且不是外部链接
        return Constants.MENU_TYPE_MENU.equals(menu.getMenuType())
                && !Constants.IS_NOT_MENU_EXTERNAL_LINK.equals(menu.getIsFrame());
    }

    /**
     * 判断菜单是否为内部链接
     *
     * @param menu 菜单对象
     * @return 如果菜单是内部链接则返回true，否则返回false
     */
    private boolean isInnerLink(SysMenu menu) {
        // 检查菜单是否为外部链接且路径是HTTP格式
        return Constants.IS_MENU_EXTERNAL_LINK.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath());
    }
}




