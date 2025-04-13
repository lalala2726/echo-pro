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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author zhangchuang
 */
@Service
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
        List<RouterVo> routers = new LinkedList<>();

        // 如果菜单为空，直接返回空列表
        if (menus == null || menus.isEmpty()) {
            return routers;
        }

        // 根据父节点ID对菜单进行分组
        Map<Long, List<SysMenu>> childrenMap = menus.stream()
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        // 获取顶级菜单（parentId为0的菜单）
        List<SysMenu> rootMenus = childrenMap.getOrDefault(0L, new ArrayList<>());

        // 递归组装路由
        routers = buildRouters(rootMenus, childrenMap);

        return routers;
    }

    @Override
    public void roleMenuTree(Long roleId) {

    }

    /**
     * 递归构建路由
     *
     * @param menus       菜单列表
     * @param childrenMap 子菜单映射
     * @return 路由列表
     */
    private List<RouterVo> buildRouters(List<SysMenu> menus, Map<Long, List<SysMenu>> childrenMap) {
        List<RouterVo> routers = new ArrayList<>();

        // 菜单类型常量
        final String TYPE_DIR = "M";


        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();

            // 设置路由基本信息
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setHidden("1".equals(menu.getVisible()));

            // 使用Builder模式构建MetaVo对象
            MetaVo meta = MetaVo.builder()
                    .title(menu.getMenuName()) // 设置菜单标题
                    .icon(menu.getIcon()) // 设置菜单图标
                    .noCache(Constants.IS_CACHE.equals(menu.getIsCache())) // 设置缓存标识，0缓存 1不缓存，noCache=true表示不缓存
                    .affix(false) // 默认不固定在标签栏
                    .alwaysShow(TYPE_DIR.equals(menu.getMenuType())) // 如果是目录，则总是显示
                    .build();

            // 将构建好的MetaVo对象设置到路由中
            router.setMeta(meta);


            // 处理子菜单
            List<SysMenu> childMenus = childrenMap.get(menu.getMenuId());
            if (childMenus != null && !childMenus.isEmpty()) {
                // 如果是目录，需要设置redirect和alwaysShow
                if (TYPE_DIR.equals(menu.getMenuType())) {
                    // 设置重定向到第一个子菜单
                    router.setAlwaysShow(true);
                    router.setRedirect(router.getPath() + "/" + childMenus.get(0).getPath());
                }

                // 递归处理子菜单
                router.setChildren(buildRouters(childMenus, childrenMap));
            }

            routers.add(router);
        }

        return routers;
    }

    /**
     * 获取路由名称
     */
    private String getRouteName(SysMenu menu) {
        // 将下划线路径转为驼峰命名
        String path = menu.getPath();
        String routerName = StringUtils.toCamelCase(path);
        // 如果不是外链且是菜单类型，则使用路径的驼峰命名作为路由名称
        if (Constants.IS_NOT_MENU_EXTERNAL_LINK.equals(menu.getIsFrame()) && menu.getMenuType().equals("C")) {
            return routerName;
        }
        return null;
    }

    /**
     * 获取路由路径
     */
    private String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();

        // 如果是外链
        if (Constants.IS_MENU_EXTERNAL_LINK.equals(menu.getIsFrame())) {
            return routerPath;
        }

        // 一级目录
        if (menu.getParentId() == 0 && "M".equals(menu.getMenuType())) {
            routerPath = "/" + menu.getPath();
        }
        // 非一级目录或菜单
        else if (menu.getParentId() != 0 && "C".equals(menu.getMenuType())) {
            routerPath = menu.getPath();
        }

        return routerPath;
    }

    /**
     * 获取组件信息
     */
    private String getComponent(SysMenu menu) {
        String component = menu.getComponent();

        // 如果组件为空或使用了特殊组件标识
        if (component == null || component.isEmpty() || "#".equals(component)) {
            // 顶级目录使用Layout组件
            if (menu.getParentId() == 0 && "M".equals(menu.getMenuType())) {
                component = "Layout";
            }
            // 如果是内部菜单并且非顶级目录
            else if (menu.getParentId() != 0 && "M".equals(menu.getMenuType())) {
                component = "ParentView";
            }
            // 如果是按钮，则不需要组件
            else if ("F".equals(menu.getMenuType())) {
                component = "";
            }
            // 外链或其他情况
            else {
                component = "InnerLink";
            }
        }

        return component;
    }

}




