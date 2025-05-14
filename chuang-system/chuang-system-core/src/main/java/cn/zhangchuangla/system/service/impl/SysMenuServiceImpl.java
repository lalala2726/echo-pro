package cn.zhangchuangla.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.converter.SysMenuConverter;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.mapper.SysRoleMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.model.vo.menu.MetaVo;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.model.vo.menu.SysMenuListVo;
import cn.zhangchuangla.system.model.vo.menu.SysMenuTreeList;
import cn.zhangchuangla.system.model.vo.role.SysRolePermVo;
import cn.zhangchuangla.system.service.SysMenuService;
import cn.zhangchuangla.system.service.SysRoleMenuService;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单服务实现类
 *
 * @author Chuang
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysRoleService sysRoleService;
    private final SysRoleMenuService sysRoleMenuService;
    private final SysMenuConverter sysMenuConverter;


    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> getMenuListByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        // 获取用户角色集合
        Set<String> roleSetByUserId = sysRoleService.getRoleSetByUserId(userId);
        if (roleSetByUserId.contains(SysRolesConstant.SUPER_ADMIN)) {
            return list();
        }
        return menuMapper.getMenuListByUserId(userId);
    }

    /**
     * 查询菜单信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu getMenuById(Long menuId) {
        if (menuId == null) {
            return null;
        }
        return getById(menuId);
    }

    /**
     * 查询菜单列表
     *
     * @param menu 菜单查询条件
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        return menuMapper.selectMenuList(menu);
    }

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单树结构列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return getMenuListByUserId(userId);
    }

    /**
     * 构建前端选择菜单树（Option结构）
     *
     * @param menus 菜单列表
     * @return Option树列表
     */
    @Override
    public List<Option<Long>> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        // 创建一个菜单ID到菜单对象的映射
        Map<Long, SysMenu> menuMap = menus.stream()
                .collect(Collectors.toMap(SysMenu::getMenuId, Function.identity(), (k1, k2) -> k1));

        // 查找所有的一级菜单（父ID为0的菜单）
        return menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .map(menu -> getOption(menu, menus, menuMap))
                // 按照排序字段排序
                .sorted(Comparator.comparing(o -> {
                    SysMenu menu = menuMap.get(o.getValue());
                    return menu != null ? menu.getSort() : Integer.MAX_VALUE;
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }


    /**
     * 构建前端选项树
     *
     * @param menus 菜单列表
     * @return 选项树
     */
    @Override
    public List<Option<Long>> buildMenuOption(List<SysMenu> menus) {
        if (menus == null) {
            return Collections.emptyList();
        }
        return buildMenuTree(menus);
    }

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return roleMenuMapper.selectMenuListByRoleId(roleId);
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
        if (menu == null) {
            return false;
        }
        return save(menu);
    }

    /**
     * 修改菜单
     *
     * @param request 菜单信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenuUpdateRequest request) {
        if (request == null || request.getMenuId() == null) {
            return false;
        }
        SysMenu sysMenu = sysMenuConverter.toEntity(request);
        //菜单名称必须唯一
        if (!checkMenuNameUnique(sysMenu)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单名称已存在");
        }
        //如果是外链，地址必须以http(s)://开头
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getIsFrame())) {
            if (!StringUtils.isHttp(sysMenu.getPath())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "地址必须以http(s)://开头");
            }
        }
        //父菜单不能选择自己
        if (sysMenu.getMenuId().equals(sysMenu.getParentId())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "上级菜单不能选择自己");
        }
        //路由名称必须唯一并且首字母必须大写
        checkRouteNameIsLegal(sysMenu);
        //检验组件
        checkComponentIsValid(sysMenu);
        //菜单类型必须是目录、菜单、按钮
        if (!Arrays.asList(Constants.MenuConstants.TYPE_DIRECTORY, Constants.MenuConstants.TYPE_MENU,
                Constants.MenuConstants.TYPE_BUTTON).contains(sysMenu.getMenuType())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单类型不合法");
        }
        String username = SecurityUtils.getUsername();
        sysMenu.setUpdateBy(username);
        return updateById(sysMenu);
    }

    /**
     * 检验组件地址是否合法，当组件类型不为按钮时，组件地址不能为空，组件地址不能以 / 开头
     *
     * @param menu 菜单信息
     */
    private void checkComponentIsValid(SysMenu menu) {
        if (!Constants.MenuConstants.TYPE_BUTTON.equals(menu.getMenuType())) {
            if (menu.getComponent() == null || menu.getComponent().isEmpty()) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "组件地址不能为空");
            }
        }
        if (menu.getComponent() != null && menu.getComponent().startsWith("/")) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "组件地址不能以 / 开头");
        }

    }

    /**
     * 检查路由名称是否合法
     *
     * @param menu 菜单信息
     */
    private void checkRouteNameIsLegal(SysMenu menu) {
        //如果当前菜单下面有子菜单，那么只能是目录类型
        if (hasChildByMenuId(menu.getMenuId())) {
            if (!Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "当前菜单下面有子菜单，菜单类型只能是目录");
            }
        }
        // 只有菜单类型需要校验路由名称
        if (!Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())) {
            return;
        }

        if (StringUtils.isBlank(menu.getMenuName())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单名称不能为空");
        }

        // 检查路由名称是否存在及合法性
        Long menuId = Optional.ofNullable(menu.getMenuId()).orElse(-1L);
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getRouteName, menu.getRouteName())
                .ne(SysMenu::getMenuId, menuId);

        SysMenu existMenu = getOne(queryWrapper);
        if (existMenu != null && !existMenu.getMenuId().equals(menuId)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "路由名称已存在");
        }

        // 路由名称首字母必须大写
        if (!Character.isUpperCase(menu.getRouteName().charAt(0))) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "路由名称首字母必须大写");
        }

        // 路由名称只能包含字母、数字和下划线
        if (!menu.getRouteName().matches("^[a-zA-Z][a-zA-Z0-9_]{0,49}$")) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "路由名称格式不合法，应以字母开头，仅包含字母、数字和下划线，且不超过50个字符");
        }
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
        if (menuId == null) {
            return false;
        }

        // 先检查是否有子菜单
        if (hasChildByMenuId(menuId)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "请先删除子菜单");
        }

        // 检查是否被角色使用
        if (checkMenuExistRole(menuId)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单已分配，不能删除");
        }

        return removeById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return true 唯一 false 不唯一
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        if (menu == null || StrUtil.isEmpty(menu.getMenuName())) {
            return false;
        }

        Long menuId = Objects.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu existMenu = getOne(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuName, menu.getMenuName())
                .eq(SysMenu::getParentId, menu.getParentId()));

        return existMenu == null || existMenu.getMenuId().equals(menuId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        if (menuId == null) {
            return false;
        }
        long count = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
        return count > 0;
    }

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        if (menuId == null) {
            return false;
        }
        return roleMenuMapper.checkMenuExistRole(menuId) > 0;
    }


    /**
     * 获取菜单路由列表
     *
     * @return 菜单路由列表
     */
    @Override
    public List<Option<Long>> getMenuOptions(boolean onlyParent) {
        LambdaQueryWrapper<SysMenu> sysMenuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysMenuLambdaQueryWrapper.ne(SysMenu::getMenuType, Constants.MenuConstants.TYPE_BUTTON);
        if (onlyParent) {
            sysMenuLambdaQueryWrapper.eq(SysMenu::getParentId, 0);
        }
        List<SysMenu> list = list(sysMenuLambdaQueryWrapper);
        return buildMenuOption(list);
    }

    /**
     * 添加菜单
     *
     * @param request 菜单添加请求
     * @return 结果
     */
    @Override
    public boolean addMenu(SysMenuAddRequest request) {
        return false;
    }

    /**
     * 根据角色ID获取菜单权限信息
     *
     * @param roleId 角色ID
     * @return 菜单权限信息
     */
    @Override
    public SysRolePermVo getRolePermByRoleId(Long roleId) {
        // 获取角色信息，若为空则抛出异常或返回默认值
        SysRole role = Optional.ofNullable(sysRoleService.getById(roleId))
                .orElseThrow(() -> new IllegalArgumentException("角色不存在，ID：" + roleId));

        List<SysMenu> sysMenus = list();

        List<SysMenuTreeList> menuTreeList = buildMenuTreeList(sysMenus);

        List<Long> selected = getRolePermSelectedByRoleId(roleId);

        return new SysRolePermVo(
                roleId,
                role.getRoleName(),
                role.getRoleKey(),
                menuTreeList,
                selected
        );
    }


    /**
     * 更新角色权限
     *
     * @param request 请求参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        Long roleId = request.getRoleId();
        SysRole sysRole = sysRoleService.getById(request.getRoleId());
        if (SysRolesConstant.SUPER_ADMIN.contains(sysRole.getRoleKey())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }
        // 删除原有的角色菜单权限
        LambdaQueryWrapper<SysRoleMenu> eq = new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuService.remove(eq);
        // 添加新的角色菜单权限
        List<SysRoleMenu> roleMenus = request.getSelectedMenuId().stream()
                .map(menuId -> {
                    SysRoleMenu sysRoleMenu = new SysRoleMenu();
                    sysRoleMenu.setRoleId(roleId);
                    sysRoleMenu.setMenuId(menuId);
                    return sysRoleMenu;
                })
                .toList();
        // 批量插入角色菜单权限
        return sysRoleMenuService.saveBatch(roleMenus);
    }

    /**
     * 查询菜单列表
     *
     * @param request 请求参数
     * @return 返回菜单列表
     */
    @Override
    public List<SysMenuListVo> listMenu(SysMenuListRequest request) {
        List<SysMenu> list = list();
        return buildMenuList(list);
    }

    /**
     * 构建菜单列表
     *
     * @param list 菜单列表
     * @return 菜单列表视图对象
     */
    private List<SysMenuListVo> buildMenuList(List<SysMenu> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        // 创建菜单映射关系：parentId -> menuList
        Map<Long, List<SysMenu>> parentChildMap = list.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        return list.stream()
                .filter(menu -> menu.getParentId() == 0) // 只处理一级菜单
                .map(menu -> convertToVo(menu, parentChildMap))
                .sorted(Comparator.comparing(SysMenuListVo::getOrderNum)) // 按显示顺序排序
                .collect(Collectors.toList());
    }

    /**
     * 将菜单转换为前端需要的视图对象
     *
     * @param menu           菜单对象
     * @param parentChildMap 父子菜单映射
     * @return 菜单视图对象
     */
    private SysMenuListVo convertToVo(SysMenu menu, Map<Long, List<SysMenu>> parentChildMap) {
        SysMenuListVo vo = new SysMenuListVo();
        vo.setMenuId(menu.getMenuId());
        vo.setMenuName(menu.getMenuName());
        vo.setParentId(menu.getParentId());
        vo.setOrderNum(menu.getOrderNum());
        vo.setRouteName(menu.getRouteName());
        vo.setIsFrame(menu.getIsFrame());
        vo.setIsCache(menu.getIsCache());
        vo.setMenuType(menu.getMenuType());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setIcon(menu.getIcon());
        vo.setSort(menu.getSort());

        // 如果有子菜单，递归转换
        List<SysMenu> children = parentChildMap.getOrDefault(menu.getMenuId(), Collections.emptyList());
        if (!children.isEmpty()) {
            vo.setChildren(children.stream()
                    .map(child -> convertToVo(child, parentChildMap))
                    .sorted(Comparator.comparing(SysMenuListVo::getOrderNum))
                    .collect(Collectors.toList()));
        }

        return vo;
    }


    /**
     * 根据角色ID获取已选中的菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @Override
    public List<Long> getRolePermSelectedByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        Set<String> roleSet = sysRoleService.getRoleSetByRoleId(roleId);
        if (roleSet.contains(SysRolesConstant.SUPER_ADMIN)) {
            // 如果是超级管理员，返回所有菜单ID
            return list().stream()
                    .map(SysMenu::getMenuId)
                    .toList();
        }
        return roleMenuMapper.selectMenuListByRoleId(roleId);
    }


    /**
     * 构造前端需要的路由界面
     *
     * @param menus 菜单列表
     * @return 返回前端需要的路由界面
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        // 查找所有一级菜单（父ID为0的菜单）
        return menus.stream()
                // 过滤掉按钮类型菜单
                .filter(menu -> !Constants.MenuConstants.TYPE_BUTTON.equals(menu.getMenuType()))
                .filter(menu -> menu.getParentId() == 0)
                // 按排序进行排序
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(menu -> convertToRouter(menu, menus, "")) // 初始调用传入空父路径
                .collect(Collectors.toList());
    }


    /**
     * 将菜单转换为路由对象
     *
     * @param menu       菜单
     * @param allMenus   所有菜单
     * @param parentPath 父级路由路径
     * @return 路由对象
     */
    private RouterVo convertToRouter(SysMenu menu, List<SysMenu> allMenus, String parentPath) {
        if (menu == null) {
            return null;
        }

        RouterVo router = new RouterVo();
        String currentFullPath = buildFullPath(parentPath, menu); // 构建完整路径

        // 设置路由名称 - 必须唯一,只有菜单类型为菜单时才设置
        if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())) {
            router.setName(menu.getRouteName());
        }
        // 设置路由路径
        router.setPath(currentFullPath); // 使用构建好的完整路径
        // 设置组件
        router.setComponent(getComponent(menu));
        // 设置查询参数
        router.setQuery(menu.getQuery());
        // 设置是否隐藏路由
        router.setHidden(Constants.MenuConstants.HIDDEN.equals(menu.getVisible()));

        // 获取子菜单，过滤掉按钮类型
        List<SysMenu> childMenus = allMenus.stream()
                .filter(m -> !Constants.MenuConstants.TYPE_BUTTON.equals(m.getMenuType()))
                .filter(m -> menu.getMenuId().equals(m.getParentId()))
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        // 如果存在子菜单，递归构建子路由
        if (!childMenus.isEmpty()) {
            List<RouterVo> children = childMenus.stream()
                    // 递归调用时传递当前完整路径作为父路径
                    .map(childMenu -> convertToRouter(childMenu, allMenus, currentFullPath))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            router.setChildren(children);

            // 如果子菜单数量大于1个，设置为总是显示
            router.setAlwaysShow(children.size() > 1);

            // 如果是目录类型且有子菜单，设置重定向到第一个子菜单的完整路径
            if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType()) && !children.isEmpty()) {
                // 重定向到第一个子路由的完整路径
                router.setRedirect(children.get(0).getPath());
            }
        }

        // 设置元数据
        router.setMeta(buildMetaVo(menu));

        return router;
    }

    /**
     * 构建完整路由路径
     *
     * @param parentPath 父路径
     * @param menu       当前菜单
     * @return 完整路径
     */
    private String buildFullPath(String parentPath, SysMenu menu) {
        String pathSegment = menu.getPath();
        if (StrUtil.isBlank(pathSegment)) {
            pathSegment = ""; // 处理空路径段
        } else {
            pathSegment = pathSegment.trim();
        }

        // 如果是外部链接，直接返回
        if (StringUtils.isHttp(pathSegment)) {
            return pathSegment;
        }

        // 规范化路径段，移除开头和结尾的 '/'
        if (pathSegment.startsWith("/")) {
            pathSegment = pathSegment.substring(1);
        }
        if (pathSegment.endsWith("/")) {
            pathSegment = pathSegment.substring(0, pathSegment.length() - 1);
        }

        String fullPath;
        // 拼接父路径和当前路径段
        if (StrUtil.isBlank(parentPath) || "/".equals(parentPath)) {
            // 如果父路径为空或是根路径"/"
            fullPath = "/" + pathSegment;
        } else {
            // 确保父路径以 "/" 结尾，当前路径段不以 "/" 开头
            String formattedParent = parentPath.endsWith("/") ? parentPath : parentPath + "/";
            fullPath = formattedParent + pathSegment;
        }

        // 移除重复的 "//" (虽然上面的逻辑尽量避免，但以防万一)
        fullPath = fullPath.replaceAll("//+", "/");

        // 移除末尾的 "/" (除非就是根路径 "/")
        if (fullPath.length() > 1 && fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }

        // 如果是菜单类型且有组件，添加 /index 后缀
        // 注意: 使用 Constants.MENU_TYPE_MENU 保持与 getComponent 方法一致
        if (Constants.MENU_TYPE_MENU.equals(menu.getMenuType()) && StrUtil.isNotBlank(menu.getComponent())
                && !Constants.MenuConstants.PARENT_VIEW.equals(menu.getComponent())) {
            // ParentView 不是实际页面组件，不应加 /index
            if (!fullPath.endsWith("/index")) { // 避免重复添加
                fullPath += "/index";
            }
        }

        return fullPath;
    }

    /**
     * 获取路由组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    private String getComponent(SysMenu menu) {
        if (menu == null) {
            return null;
        }

        // 如果是外链，直接返回处理外链的组件
        if (StringUtils.isHttp(menu.getPath())) {
            return Constants.MenuConstants.INNER_LINK;
        }

        // 目录类型且不是iframe，返回null，让前端自动处理
        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())
                && menu.getIsFrame() != 1) {
            return null;
        }

        // 菜单类型且不是iframe，返回组件路径
        if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame() != 1) {
            return menu.getComponent();
        }

        // 对于iframe类型，返回iframe组件
        if (menu.getIsFrame() == 1) {
            return Constants.MenuConstants.INNER_LINK;
        }

        // 其他情况（如按钮类型）返回null
        return null;
    }


    /**
     * 构建菜单元数据
     *
     * @param menu 菜单信息
     * @return 菜单元数据
     */
    private MetaVo buildMetaVo(SysMenu menu) {
        if (menu == null) {
            return null;
        }

        MetaVo metaVo = new MetaVo();

        // 设置标题（菜单名称）
        metaVo.setTitle(menu.getMenuName());
        // 设置图标
        metaVo.setIcon(menu.getIcon());
        // 设置是否在菜单中显示
        metaVo.setShowLink(true);
        // 设置是否显示父级菜单
        metaVo.setShowParent(true);

        // 如果有权限标识，设置权限信息
        if (StrUtil.isNotEmpty(menu.getPermission())) {
            metaVo.setAuths(new String[]{menu.getPermission()});
        } else if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            // 目录类型，添加空的auths数组
            metaVo.setAuths(new String[]{""});
        }
        return metaVo;
    }

    /**
     * 获取菜单的选项（Option类型）
     *
     * @param menu    菜单对象
     * @param menus   所有菜单列表
     * @param menuMap 菜单映射
     * @return 菜单选项
     */
    private Option<Long> getOption(SysMenu menu, List<SysMenu> menus, Map<Long, SysMenu> menuMap) {
        Option<Long> option = new Option<>();
        option.setValue(menu.getMenuId());
        option.setLabel(menu.getMenuName());

        // 获取子菜单
        List<Option<Long>> children = getChildrenMenuOptions(menu.getMenuId(), menus, menuMap);
        if (!children.isEmpty()) {
            option.setChildren(children);
        }

        return option;
    }

    /**
     * 获取菜单的子节点（Option类型）
     *
     * @param parentId 父菜单ID
     * @param menus    所有菜单列表
     * @param menuMap  菜单映射
     * @return 子菜单列表
     */
    private List<Option<Long>> getChildrenMenuOptions(Long parentId, List<SysMenu> menus, Map<Long, SysMenu> menuMap) {
        if (parentId == null || menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        return menus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .map(menu -> getOption(menu, menus, menuMap))
                // 按照排序字段排序
                .sorted(Comparator.comparing(o -> {
                    SysMenu menu = menuMap.get(o.getValue());
                    return menu != null ? menu.getSort() : Integer.MAX_VALUE;
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 递归获取子菜单 {@link SysMenuTreeList}
     *
     * @param sysMenus 菜单列表
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    private List<SysMenuTreeList> getChildMenus(List<SysMenu> sysMenus, Long parentId) {
        if (parentId == null || sysMenus.isEmpty()) {
            return Collections.emptyList();
        }

        return sysMenus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .map(menu -> {
                    SysMenuTreeList sysMenuTreeList = new SysMenuTreeList();
                    sysMenuTreeList.setMenuId(menu.getMenuId());
                    sysMenuTreeList.setMenuName(menu.getMenuName());
                    sysMenuTreeList.setMenuType(menu.getMenuType());

                    // 递归获取子菜单
                    List<SysMenuTreeList> children = getChildMenus(sysMenus, menu.getMenuId());
                    if (!children.isEmpty()) {
                        sysMenuTreeList.setChildren(children);
                    }

                    return sysMenuTreeList;
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建菜单树列表 类型:{@link SysMenuTreeList}
     *
     * @param sysMenus 菜单列表
     * @return 菜单树列表
     */
    private List<SysMenuTreeList> buildMenuTreeList(List<SysMenu> sysMenus) {
        if (sysMenus == null || sysMenus.isEmpty()) {
            return Collections.emptyList();
        }

        return sysMenus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .map(menu -> {
                    SysMenuTreeList treeList = new SysMenuTreeList();
                    treeList.setMenuId(menu.getMenuId());
                    treeList.setMenuName(menu.getMenuName());
                    treeList.setMenuType(menu.getMenuType());
                    treeList.setParentId(menu.getParentId());

                    List<SysMenuTreeList> children = getChildMenus(sysMenus, menu.getMenuId());
                    if (!children.isEmpty()) {
                        treeList.setChildren(children);
                    }

                    return treeList;
                })
                .toList();
    }
}
