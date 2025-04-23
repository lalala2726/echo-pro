package cn.zhangchuangla.system.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.RedisConstants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.redis.RedisCache;
import cn.zhangchuangla.common.enums.MenuTypeEnum;
import cn.zhangchuangla.common.enums.StatusEnum;
import cn.zhangchuangla.common.model.entity.KeyValue;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.converter.SysMenuConverter;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.MenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.MenuQueryRequest;
import cn.zhangchuangla.system.model.vo.menu.MenuVo;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import cn.zhangchuangla.system.model.vo.permission.MenuListVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangchuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysMenuConverter sysMenuConverter;
    private final SysRoleService roleMenuService;
    private final SysMenuMapper sysMenuMapper;
    private final RedisCache redisCache;

    /**
     * 菜单列表
     *
     * @param queryParams {@link MenuQueryRequest}
     */
    @Override
    public List<MenuVo> listMenus(MenuQueryRequest queryParams) {
        List<SysMenu> sysMenus = this.list(new LambdaQueryWrapper<SysMenu>()
                .like(StrUtil.isNotBlank(queryParams.getKeywords()), SysMenu::getName, queryParams.getKeywords())
                .orderByAsc(SysMenu::getSort)
        );
        // 获取所有菜单ID
        Set<Long> menuIds = sysMenus.stream()
                .map(SysMenu::getId)
                .collect(Collectors.toSet());

        // 获取所有父级ID
        Set<Long> parentIds = sysMenus.stream()
                .map(SysMenu::getParentId)
                .collect(Collectors.toSet());

        // 获取根节点ID（递归的起点），即父节点ID中不包含在部门ID中的节点，注意这里不能拿顶级菜单 O 作为根节点，因为菜单筛选的时候 O 会被过滤掉
        List<Long> rootIds = parentIds.stream()
                .filter(id -> !menuIds.contains(id))
                .toList();

        // 使用递归函数来构建菜单树
        return rootIds.stream()
                .flatMap(rootId -> buildMenuTree(rootId, sysMenus).stream())
                .collect(Collectors.toList());
    }

    /**
     * 递归生成菜单列表
     *
     * @param parentId    父级ID
     * @param sysMenuList 菜单列表
     * @return 菜单列表
     */
    private List<MenuVo> buildMenuTree(Long parentId, List<SysMenu> sysMenuList) {
        return CollectionUtil.emptyIfNull(sysMenuList)
                .stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(entity -> {
                    MenuVo menuVO = sysMenuConverter.toVo(entity);
                    List<MenuVo> children = buildMenuTree(entity.getId(), sysMenuList);
                    menuVO.setChildren(children);
                    return menuVO;
                }).toList();
    }

    /**
     * 菜单下拉数据
     *
     * @param onlyParent 是否只查询父级菜单 如果为true，排除按钮
     */
    @Override
    public List<Option<Long>> listMenuOptions(boolean onlyParent) {
        List<SysMenu> sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>()
                .in(onlyParent, SysMenu::getType, MenuTypeEnum.CATALOG.getValue(), MenuTypeEnum.MENU.getValue())
                .orderByAsc(SysMenu::getSort)
        );
        return buildMenuOptions(Constants.ROOT_NODE_ID, sysMenuList);
    }

    /**
     * 递归生成菜单下拉层级列表
     *
     * @param parentId    父级ID
     * @param sysMenuList 菜单列表
     * @return 菜单下拉列表
     */
    private List<Option<Long>> buildMenuOptions(Long parentId, List<SysMenu> sysMenuList) {
        List<Option<Long>> menuOptions = new ArrayList<>();

        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().equals(parentId)) {
                Option<Long> option = new Option<>(sysMenu.getId(), sysMenu.getName());
                List<Option<Long>> subMenuOptions = buildMenuOptions(sysMenu.getId(), sysMenuList);
                if (!subMenuOptions.isEmpty()) {
                    option.setChildren(subMenuOptions);
                }
                menuOptions.add(option);
            }
        }

        return menuOptions;
    }

    /**
     * 获取菜单路由列表
     */
    @Override
    public List<RouteVo> getCurrentUserRoutes() {

        Set<String> roleCodes = SecurityUtils.getRoles();

        if (CollectionUtil.isEmpty(roleCodes)) {
            return Collections.emptyList();
        }
        List<SysMenu> sysMenuList;
        // 超级管理员获取所有菜单
        if (SecurityUtils.isAdmin()) {
            log.info("当前用户是超级管理员，允许所有菜单信息:{}", roleCodes);
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>()
                    .ne(SysMenu::getType, MenuTypeEnum.BUTTON.getValue())
                    .orderByAsc(SysMenu::getSort)
            );
        } else {
            sysMenuList = this.baseMapper.getMenusByRoleCodes(roleCodes);
        }
        return buildRoutes(Constants.ROOT_NODE_ID, sysMenuList);
    }


    /**
     * 根据RouteBO创建RouteVO
     */
    private RouteVo toRouteVo(SysMenu sysMenu) {
        RouteVo routeVO = new RouteVo();
        // 获取路由名称
        String routeName = sysMenu.getRouteName();
        if (StrUtil.isBlank(routeName)) {
            // 路由 name 需要驼峰，首字母大写
            routeName = StringUtils.capitalize(StrUtil.toCamelCase(sysMenu.getRoutePath(), '-'));
        }
        // 根据name路由跳转 this.$router.push({name:xxx})
        routeVO.setName(routeName);

        // 根据path路由跳转 this.$router.push({path:xxx})
        routeVO.setPath(sysMenu.getRoutePath());
        routeVO.setRedirect(sysMenu.getRedirect());
        routeVO.setComponent(sysMenu.getComponent());

        RouteVo.Meta meta = new RouteVo.Meta();
        meta.setTitle(sysMenu.getName());
        meta.setIcon(sysMenu.getIcon());
        meta.setHidden(StatusEnum.DISABLE.getValue().equals(sysMenu.getVisible()));
        // 【菜单】是否开启页面缓存
        if (MenuTypeEnum.MENU.getValue().equals(sysMenu.getType())
                && ObjectUtil.equals(sysMenu.getKeepAlive(), 1)) {
            meta.setKeepAlive(true);
        }
        meta.setAlwaysShow(ObjectUtil.equals(sysMenu.getAlwaysShow(), 1));

        String paramsJson = sysMenu.getParams();
        // 将 JSON 字符串转换为 Map<String, String>
        if (StrUtil.isNotBlank(paramsJson)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, String> paramMap = objectMapper.readValue(paramsJson, new TypeReference<>() {
                });
                meta.setParams(paramMap);
            } catch (Exception e) {
                throw new RuntimeException("解析参数失败", e);
            }
        }
        routeVO.setMeta(meta);
        return routeVO;
    }

    /**
     * 新增/修改菜单
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean saveMenu(MenuAddRequest menuAddRequest) {

        Integer menuType = menuAddRequest.getType();

        if (MenuTypeEnum.CATALOG.getValue().equals(menuType)) {  // 如果是目录
            String path = menuAddRequest.getRoutePath();
            if (menuAddRequest.getParentId() == 0 && !path.startsWith("/")) {
                menuAddRequest.setRoutePath("/" + path); // 一级目录需以 / 开头
            }
            menuAddRequest.setComponent("Layout");
        } else if (MenuTypeEnum.EXTLINK.getValue().equals(menuType)) {
            // 外链菜单组件设置为 null
            menuAddRequest.setComponent(null);
        }
        if (Objects.equals(menuAddRequest.getParentId(), menuAddRequest.getId())) {
            throw new RuntimeException("父级菜单不能为当前菜单");
        }
        SysMenu entity = sysMenuConverter.toEntity(menuAddRequest);
        String treePath = generateMenuTreePath(menuAddRequest.getParentId());
        entity.setTreePath(treePath);

        List<KeyValue> params = menuAddRequest.getParams();
        // 路由参数 [{key:"id",value:"1"}，{key:"name",value:"张三"}] 转换为 [{"id":"1"},{"name":"张三"}]
        if (CollectionUtil.isNotEmpty(params)) {
            entity.setParams(JSONUtil.toJsonStr(params.stream()
                    .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue))));
        } else {
            entity.setParams(null);
        }
        // 新增类型为菜单时候 路由名称唯一
        if (MenuTypeEnum.MENU.getValue().equals(menuType)) {
            Assert.isFalse(this.exists(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getRouteName, entity.getRouteName())
                    .ne(menuAddRequest.getId() != null, SysMenu::getId, menuAddRequest.getId())
            ), "路由名称已存在");
        } else {
            // 其他类型时 给路由名称赋值为空
            entity.setRouteName(null);
        }

        boolean result = this.saveOrUpdate(entity);
        if (result) {
            // 编辑刷新角色权限缓存
            if (menuAddRequest.getId() != null) {
                roleMenuService.refreshRolePermsCache();
            }
        }
        // 修改菜单如果有子菜单，则更新子菜单的树路径
        updateChildrenTreePath(entity.getId(), treePath);
        return result;
    }

    /**
     * 更新子菜单树路径
     *
     * @param id       当前菜单ID
     * @param treePath 当前菜单树路径
     */
    private void updateChildrenTreePath(Long id, String treePath) {
        List<SysMenu> children = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (CollectionUtil.isNotEmpty(children)) {
            // 子菜单的树路径等于父菜单的树路径加上父菜单ID
            String childTreePath = treePath + "," + id;
            this.update(new LambdaUpdateWrapper<SysMenu>()
                    .eq(SysMenu::getParentId, id)
                    .set(SysMenu::getTreePath, childTreePath)
            );
            for (SysMenu child : children) {
                // 递归更新子菜单
                updateChildrenTreePath(child.getId(), childTreePath);
            }
        }
    }

    /**
     * 部门路径生成
     *
     * @param parentId 父ID
     * @return 父节点路径以英文逗号(, )分割，eg: 1,2,3
     */
    private String generateMenuTreePath(Long parentId) {
        if (Constants.ROOT_NODE_ID.equals(parentId)) {
            return String.valueOf(parentId);
        } else {
            SysMenu parent = this.getById(parentId);
            return parent != null ? parent.getTreePath() + "," + parent.getId() : null;
        }
    }


    /**
     * 修改菜单显示状态
     *
     * @param menuId  菜单ID
     * @param visible 是否显示(1->显示；2->隐藏)
     * @return 是否修改成功
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean updateMenuVisible(Long menuId, Integer visible) {
        return this.update(new LambdaUpdateWrapper<SysMenu>()
                .eq(SysMenu::getId, menuId)
                .set(SysMenu::getVisible, visible)
        );
    }

    /**
     * 获取菜单表单数据
     *
     * @param id 菜单ID
     * @return 菜单表单数据
     */
    @Override
    public MenuAddRequest getMenuForm(Long id) {
        SysMenu entity = this.getById(id);
        Assert.isTrue(entity != null, "菜单不存在");
        MenuAddRequest formData = sysMenuConverter.toAddRequest(entity);
        // 路由参数字符串 {"id":"1","name":"张三"} 转换为 [{key:"id", value:"1"}, {key:"name", value:"张三"}]
        String params = null;
        if (entity != null) {
            params = entity.getParams();
        }
        if (StrUtil.isNotBlank(params)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                // 解析 JSON 字符串为 Map<String, String>
                Map<String, String> paramMap = objectMapper.readValue(params, new TypeReference<>() {
                });

                // 转换为 List<KeyValue> 格式 [{key:"id", value:"1"}, {key:"name", value:"张三"}]
                List<KeyValue> transformedList = paramMap.entrySet().stream()
                        .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                        .toList();

                // 将转换后的列表存入 MenuForm
                formData.setParams(transformedList);
            } catch (Exception e) {
                throw new RuntimeException("解析参数失败", e);
            }
        }

        return formData;
    }

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否删除成功
     */
    @Override
    @CacheEvict(cacheNames = "menu", key = "'routes'")
    public boolean deleteMenu(Long id) {
        boolean result = this.remove(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getId, id)
                .or()
                .apply("CONCAT (',',tree_path,',') LIKE CONCAT('%,',{0},',%')", id));
        // 刷新角色权限缓存
        if (result) {
            roleMenuService.refreshRolePermsCache();
        }
        return result;

    }


    /**
     * 根据角色名称获取权限
     *
     * @param roleName 角色名称
     * @return 返回权限集合
     */
    @Override
    public Set<String> getPermissionsByRoleName(String roleName) {
        // 如果角色名称是超级管理员，则返回所有权限
        if (SysRolesConstant.SUPER_ADMIN.equals(roleName)) {
            return Set.of(Constants.ALL_PERMISSION);
        }
        // 如果角色名称不是超级管理员，则查询权限，这边先从缓存中获取如果缓存中没有，则查询数据库
        Set<String> cacheSet = redisCache.getCacheSet(StrUtil.format(RedisConstants.Auth.ROLE_PERMISSIONS_PREFIX, roleName));
        if (cacheSet != null) {
            return cacheSet;
        }
        // 如果缓存中没有，则查询数据库,并将角色关联的权限信息保存到缓存中
        List<SysMenu> sysMenus = sysMenuMapper.getPermissionsByRoleName(roleName);
        Set<String> collect = sysMenus.stream()
                .map(SysMenu::getPermission)
                .collect(Collectors.toSet());
        redisCache.setCacheSet(StrUtil.format(RedisConstants.Auth.ROLE_PERMISSIONS_PREFIX), collect);
        return collect;
    }

    /**
     * 根据角色名称集合获取权限
     *
     * @param roleSet 角色名称集合
     * @return 返回权限集合
     */
    @Override
    public Set<String> getPermissionsByRoleName(Set<String> roleSet) {
        // 如果角色名称其中一项是超级管理员将返回所有权限
        if (roleSet.contains(SysRolesConstant.SUPER_ADMIN)) {
            return Set.of(Constants.ALL_PERMISSION);
        }
        //如果角色名称集合不包含超级管理员，则查询权限，然后合并相同的
        Set<String> permissions = new HashSet<>();
        for (String roleName : roleSet) {
            List<SysMenu> sysMenus = sysMenuMapper.getPermissionsByRoleName(roleName);
            Set<String> rolePermissions = sysMenus.stream()
                    .map(SysMenu::getPermission)
                    .collect(Collectors.toSet());
            permissions.addAll(rolePermissions);
        }
        return permissions;
    }

    /**
     * 获取系统中所有的可用的权限
     *
     * @return 权限列表
     */
    @Override
    public List<MenuListVo> listPermission() {
        List<SysMenu> list = list();
        return buildPermissionList(Constants.ROOT_NODE_ID, list);
    }

    /**
     * 递归生成菜单路由层级列表
     *
     * @param parentId    父级ID
     * @param sysMenuList 菜单列表
     * @return 路由层级列表
     */
    private List<RouteVo> buildRoutes(Long parentId, List<SysMenu> sysMenuList) {
        List<RouteVo> routeList = new ArrayList<>();

        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().equals(parentId)) {
                RouteVo routeVO = toRouteVo(sysMenu);
                List<RouteVo> children = buildRoutes(sysMenu.getId(), sysMenuList);
                if (!children.isEmpty()) {
                    routeVO.setChildren(children);
                }
                routeList.add(routeVO);
            }
        }

        return routeList;
    }

    /**
     * 递归构建权限列表
     *
     * @param parentId    父级ID
     * @param sysMenuList 菜单列表
     * @return 权限列表
     */
    private List<MenuListVo> buildPermissionList(long parentId, List<SysMenu> sysMenuList) {
        List<MenuListVo> permissionList = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().equals(parentId)) {
                MenuListVo menuListVo = createPermissionListVo(sysMenu, sysMenuList);
                menuListVo.setParentId(parentId);
                permissionList.add(menuListVo);
            }
        }
        return permissionList;
    }

    /**
     * 创建权限列表VO
     *
     * @param sysMenu     菜单实体
     * @param sysMenuList 菜单列表
     * @return 权限列表VO
     */
    private MenuListVo createPermissionListVo(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        MenuListVo menuListVo = new MenuListVo();
        menuListVo.setMenuId(sysMenu.getId());
        menuListVo.setMenuName(sysMenu.getName());
        menuListVo.setMenuType(sysMenu.getType());
        List<MenuListVo> children = buildPermissionList(sysMenu.getId(), sysMenuList);
        menuListVo.setChildren(children);
        return menuListVo;
    }
}
