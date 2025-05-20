package cn.zhangchuangla.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.zhangchuangla.common.constant.Constants;
import cn.zhangchuangla.common.constant.SysRolesConstant;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.common.utils.StringUtils;
import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.mapper.SysRoleMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuQueryRequest;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 菜单权限服务实现类。
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SysMenu> getMenuListByUserId(Long userId) {
        if (userId == null) {
            log.warn("根据用户ID查询菜单列表时，用户ID为空。");
            return Collections.emptyList();
        }
        Set<String> roles = SecurityUtils.getRoles();
        if (roles.contains(SysRolesConstant.SUPER_ADMIN)) {
            log.info("用户ID {} (角色: {}) 是超级管理员，返回所有有效菜单，并按父ID和排序值排序。", userId, roles);
            return list(new LambdaQueryWrapper<SysMenu>()
                    .orderByAsc(SysMenu::getParentId)
                    .orderByAsc(SysMenu::getSort));
        }
        log.debug("用户ID {} (角色: {}) 非超级管理员，根据权限查询菜单。", userId, roles);
        List<SysMenu> userMenus = menuMapper.getMenuListByUserId(userId);
        if (userMenus != null) {
            userMenus.sort(Comparator.comparing(SysMenu::getParentId, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        } else {
            userMenus = Collections.emptyList();
        }
        return userMenus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SysMenu getMenuById(Long menuId) {
        if (menuId == null) {
            log.warn("查询菜单信息时，菜单ID为空。");
            return null;
        }
        return getById(menuId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Option<Long>> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, SysMenu> menuMap = menus.stream()
                .collect(Collectors.toMap(SysMenu::getMenuId, Function.identity(), (k1, k2) -> k1));

        return menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .map(menu -> convertToOptionRecursive(menu, menus, menuMap))
                .sorted(Comparator.comparing(o -> {
                    SysMenu menuEntity = menuMap.get(o.getValue());
                    return menuEntity != null ? menuEntity.getSort() : Integer.MAX_VALUE;
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Option<Long>> buildMenuOption(List<SysMenu> menus) {
        if (menus == null) {
            return Collections.emptyList();
        }
        return buildMenuTree(menus);
    }

    /**
     * 更新菜单信息。
     *
     * <p>该方法用于更新系统中的菜单信息。主要流程包括：
     * <ul>
     *     <li>校验请求对象及菜单ID是否为空，若为空则记录警告并返回 false；</li>
     *     <li>将请求对象转换为 SysMenu 实体对象；</li>
     *     <li>如果菜单类型是目录且父菜单ID为空或为0，并且组件路径为空，则设置默认的布局组件（LAYOUT）；</li>
     *     <li>配置路由名称，确保其符合规范；</li>
     *     <li>对菜单进行基础规则校验，包括组件路径、路由路径、父菜单ID等；</li>
     *     <li>检查菜单类型是否合法，仅允许目录（M）、菜单（C）和按钮（F）三种类型；</li>
     *     <li>设置更新人信息，并记录日志；</li>
     *     <li>保存菜单信息到数据库，并返回操作结果。</li>
     * </ul>
     * </p>
     *
     * @param request 包含菜单更新信息的请求对象，不能为 null。
     *                - menuId: 菜单ID，必须不为 null 且大于等于 0；
     *                - menuName: 菜单名称，必须不为空；
     *                - path: 路由地址，必须不为空；
     *                - 其他字段可选，具体参考 SysMenuUpdateRequest 类定义。
     * @return 如果更新成功返回 true，否则返回 false。
     * @throws ServiceException 如果在更新过程中出现任何业务逻辑错误，例如参数校验失败、菜单类型不合法等。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenuUpdateRequest request) {
        if (request == null || request.getMenuId() == null) {
            log.warn("更新菜单失败：请求对象或菜单ID为空。");
            return false;
        }
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);

        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType())
                && (sysMenu.getParentId() == null || sysMenu.getParentId() == 0L)
                && StrUtil.isBlank(sysMenu.getComponent())) {
            sysMenu.setComponent(Constants.MenuConstants.LAYOUT);
        }

        // 检查外链模式
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(request.getExternalLink())) {
            if (!Constants.MenuConstants.IS_FRAME.equals(request.getIsFrame())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "只有内嵌链接模式才支持外部链接跳转。");
            }
        }

        // 必须先配置路由名称，因为基础校验（特别是外链）依赖于 path 和 routeName 的最终意图
        configureRouteName(sysMenu);
        menuBaseCheck(sysMenu);
        checkPathIsLegal(sysMenu);
        checkParentIdIsLegal(sysMenu);
        checkComponentIsLegal(sysMenu);

        if (!Arrays.asList(Constants.MenuConstants.TYPE_DIRECTORY, Constants.MenuConstants.TYPE_MENU,
                Constants.MenuConstants.TYPE_BUTTON).contains(sysMenu.getMenuType())) {
            log.error("更新菜单失败：菜单类型 {} 不合法。", sysMenu.getMenuType());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单类型不合法");
        }

        sysMenu.setUpdateBy(SecurityUtils.getUsername());
        log.info("用户 {} 正在更新菜单 ID: {}，名称: {}", sysMenu.getUpdateBy(), sysMenu.getMenuId(), sysMenu.getMenuName());
        return updateById(sysMenu);
    }

    /**
     * 删除菜单信息
     *
     * <p>根据提供的菜单ID删除对应的菜单项。在删除前会进行以下检查：
     * <ul>
     *   <li>如果菜单ID为空，则记录警告并返回false。</li>
     *   <li>如果该菜单下存在子菜单，则抛出ServiceException异常，提示用户先删除子菜单。</li>
     *   <li>如果该菜单已被分配给任何角色，则抛出ServiceException异常，提示菜单已分配，不能直接删除。</li>
     * </ul>
     *
     * @param menuId 需要删除的菜单的ID
     * @return 如果删除成功则返回true，否则返回false
     * @throws ServiceException 如果菜单存在子菜单或已被分配给角色时抛出异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenuById(Long menuId) {
        if (menuId == null) {
            log.warn("删除菜单失败：菜单ID为空。");
            return false;
        }
        if (hasChildByMenuId(menuId)) {
            log.warn("删除菜单失败：菜单ID {} 存在子菜单。", menuId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "请先删除子菜单。");
        }
        if (checkMenuExistRole(menuId)) {
            log.warn("删除菜单失败：菜单ID {} 已分配给角色。", menuId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单已分配，不能删除。");
        }
        log.info("正在删除菜单 ID: {}", menuId);
        return removeById(menuId);
    }

    /**
     * 检查菜单的名字是否唯一
     * <p>
     * {@inheritDoc}
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        if (menu == null || StrUtil.isEmpty(menu.getMenuName())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单名称不能为空。");
        }
        Long menuId = Objects.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu existMenu = getOne(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuName, menu.getMenuName())
                .eq(SysMenu::getParentId, menu.getParentId() == null ? 0L : menu.getParentId()));
        return existMenu != null && !existMenu.getMenuId().equals(menuId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        if (menuId == null) {
            return false;
        }
        return count(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId)
                .ne(SysMenu::getMenuType, Constants.MenuConstants.TYPE_BUTTON)) > 0;
    }

    /**
     * 检查菜单的角色是否存在
     * {@inheritDoc}
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        if (menuId == null) {
            return false;
        }
        return roleMenuMapper.checkMenuExistRole(menuId) > 0;
    }

    /**
     * 获取菜单的列表
     * <p>
     * {@inheritDoc}
     */
    @Override
    public List<Option<Long>> getMenuOptions(boolean onlyParent) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(SysMenu::getMenuType, Constants.MenuConstants.TYPE_BUTTON);
        if (onlyParent) {
            queryWrapper.eq(SysMenu::getParentId, 0L);
        }
        queryWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> list = list(queryWrapper);
        return buildMenuOption(list);
    }

    /**
     * {@inheritDoc}
     *
     * <p>此方法用于添加一个新的菜单项到系统中。主要流程包括：
     * <ul>
     *     <li>校验请求对象是否为空，若为空则记录警告并返回 false；</li>
     *     <li>将请求对象转换为 SysMenu 实体对象；</li>
     *     <li>如果菜单类型是目录且父菜单ID为空或为0，并且组件路径为空，则设置默认的布局组件（LAYOUT）；</li>
     *     <li>配置路由名称，确保其符合规范；</li>
     *     <li>对菜单进行基础规则校验，包括组件路径、路由路径、父菜单ID等；</li>
     *     <li>检查菜单类型是否合法，仅允许目录（M）、菜单（C）和按钮（F）三种类型；</li>
     *     <li>设置创建人信息，并记录日志；</li>
     *     <li>保存菜单信息到数据库，并返回操作结果。</li>
     * </ul>
     * </p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMenu(SysMenuAddRequest request) {
        if (request == null) {
            log.warn("添加菜单失败：请求对象为空。");
            return false;
        }
        SysMenu sysMenu = new SysMenu();
        BeanUtils.copyProperties(request, sysMenu);

        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(request.getMenuType())
                && (request.getParentId() == null || request.getParentId() == 0L)
                && StrUtil.isBlank(sysMenu.getComponent())) {
            sysMenu.setComponent(Constants.MenuConstants.LAYOUT);
        }
        // 检查外链模式
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(request.getExternalLink())) {
            if (!Constants.MenuConstants.IS_FRAME.equals(request.getIsFrame())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "只有内嵌链接模式才支持外部链接跳转。");
            }
        }

        configureRouteName(sysMenu);
        menuBaseCheck(sysMenu);
        checkPathIsLegal(sysMenu);
        checkParentIdIsLegal(sysMenu);
        checkComponentIsLegal(sysMenu);

        if (!Arrays.asList(Constants.MenuConstants.TYPE_DIRECTORY, Constants.MenuConstants.TYPE_MENU,
                Constants.MenuConstants.TYPE_BUTTON).contains(sysMenu.getMenuType())) {
            log.error("添加菜单失败：菜单类型 {} 不合法。", sysMenu.getMenuType());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单类型不合法");
        }

        sysMenu.setCreateBy(SecurityUtils.getUsername());
        log.info("用户 {} 正在添加新菜单: {}", sysMenu.getCreateBy(), sysMenu.getMenuName());
        return save(sysMenu);
    }


    /**
     * {@inheritDoc}
     *
     * <p>此方法用于根据角色ID获取该角色的权限菜单树信息。主要流程包括：
     * <ul>
     *     <li>根据角色ID查询角色信息，若不存在则抛出异常；</li>
     *     <li>查询系统中所有的菜单，并按父菜单ID和排序字段进行升序排列；</li>
     *     <li>将扁平化的菜单列表构建成树形结构的菜单权限对象（SysMenuTreeList）；</li>
     *     <li>获取该角色已分配的菜单ID列表；</li>
     *     <li>最终返回封装好的 SysRolePermVo 对象，包含角色信息、菜单树及已选中的菜单ID。</li>
     * </ul>
     */
    @Override
    public SysRolePermVo getRolePermByRoleId(Long roleId) {
        SysRole role = Optional.ofNullable(sysRoleService.getById(roleId))
                .orElseThrow(() -> {
                    log.error("获取角色权限失败：角色ID {} 不存在。", roleId);
                    return new IllegalArgumentException("角色不存在，ID：" + roleId);
                });
        List<SysMenu> allMenus = list(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getParentId)
                .orderByAsc(SysMenu::getSort));
        List<SysMenuTreeList> menuTreeList = buildMenuTreeList(allMenus);
        List<Long> selectedMenuIds = getRolePermSelectedByRoleId(roleId);
        return new SysRolePermVo(roleId, role.getRoleName(), role.getRoleKey(), menuTreeList, selectedMenuIds);
    }

    /**
     * 更新角色的菜单权限信息。
     *
     * <p>该方法会根据传入的请求对象更新指定角色关联的菜单权限。具体操作流程如下：
     * 1. 首先检查角色是否存在，若不存在则抛出异常；
     * 2. 如果是超级管理员角色（super_admin），则禁止修改其权限并抛出异常；
     * 3. 删除该角色原有的所有菜单权限；
     * 4. 如果新的菜单ID列表不为空，则为每个菜单ID创建一个新的 SysRoleMenu 对象，并批量保存到数据库中；
     * 5. 如果没有提供新的菜单ID，则直接返回 true 表示成功清除了该角色的所有权限。</p>
     *
     * @param request 包含角色ID和菜单ID列表的请求对象。
     * @return 如果操作成功完成，返回 true；否则返回 false。
     * @throws ServiceException 如果角色不存在或者尝试修改超级管理员角色权限。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        Long roleId = request.getRoleId();
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            log.error("更新角色权限失败：角色ID {} 不存在。", roleId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "角色不存在");
        }
        if (SysRolesConstant.SUPER_ADMIN.equals(sysRole.getRoleKey())) {
            log.warn("试图修改超级管理员 ({}) 的权限，操作被禁止。", sysRole.getRoleKey());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        log.debug("已删除角色ID {} 的原有菜单权限。", roleId);
        if (request.getSelectedMenuId() != null && !request.getSelectedMenuId().isEmpty()) {
            List<SysRoleMenu> roleMenusToInsert = request.getSelectedMenuId().stream()
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(roleId);
                        sysRoleMenu.setMenuId(menuId);
                        return sysRoleMenu;
                    }).toList();
            log.debug("为角色ID {} 批量插入 {} 条新菜单权限。", roleId, roleMenusToInsert.size());
            return sysRoleMenuService.saveBatch(roleMenusToInsert);
        }
        return true;
    }


    /**
     * 根据请求参数查询菜单信息并构建成树形结构的菜单列表视图对象。
     * <p>
     * 此方法用于获取满足条件的菜单数据，并将这些数据转换为适合前端展示的树形结构。
     * 支持根据菜单名称进行模糊匹配，同时按照父菜单ID和排序字段进行升序排列。
     * 最终通过 buildTreeFormattedMenuList 方法将扁平化的菜单列表组装成具有层级关系的 SysMenuListVo 列表。
     *
     * @param request 包含查询条件的 SysMenuListRequest 请求对象，可能为 null。
     *                - menuName：菜单名称，支持模糊匹配；
     *                - 其他字段可扩展，但当前未使用。
     * @return 构建完成的 SysMenuListVo 树形列表，每个节点包含菜单的基本信息以及子菜单列表（如有）。
     * <p>
     * 流程说明：
     * 1. 创建 LambdaQueryWrapper 查询包装器，用于构建动态查询条件；
     * 2. 如果请求对象不为空且 menuName 不为空或空白字符串，则添加对菜单名称的模糊查询条件；
     * 3. 添加排序条件：先按 parentId 升序，再按 sort 升序，以确保结果按层级和顺序排列；
     * 4. 执行查询，获取所有符合条件的 SysMenu 实体列表；
     * 5. 调用 buildTreeFormattedMenuList 方法，将扁平化菜单列表递归构建成树形结构；
     * 6. 返回最终的 SysMenuListVo 列表，供前端展示使用。
     */
    @Override
    public List<SysMenuListVo> listMenu(SysMenuQueryRequest request) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        if (request != null && StrUtil.isNotBlank(request.getMenuName())) {
            queryWrapper.like(SysMenu::getMenuName, request.getMenuName());
        }
        queryWrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getSort);
        List<SysMenu> allMenus = list(queryWrapper);
        return buildTreeFormattedMenuList(allMenus);
    }


    /**
     * {@inheritDoc}
     *
     * <p>此方法用于根据角色ID获取该角色已分配的菜单权限ID列表。主要流程包括：
     * <ul>
     *     <li>校验传入的角色ID是否为空，若为空则返回空列表并记录警告日志；</li>
     *     <li>通过角色ID查询对应的角色标识集合（roleKey）；</li>
     *     <li>如果角色包含超级管理员标识，则返回系统中所有菜单的ID；</li>
     *     <li>否则，调用数据访问层获取该角色关联的菜单ID列表。</li>
     * </ul>
     *
     * <p><b>关键点说明：</b></p>
     * <ul>
     *     <li>{@link SysRolesConstant#SUPER_ADMIN} 是超级管理员角色标识，拥有所有菜单权限；</li>
     *     <li>通过 {@link SysRoleService#getRoleSetByRoleId(Long)} 获取角色标识集合；</li>
     *     <li>通过 {@link SysMenuMapper#selectMenuListByRoleId(Long)} 查询角色对应的菜单ID列表。</li>
     * </ul>
     *
     * @param roleId 角色ID，用于查询该角色的菜单权限。
     * @return 返回与角色ID关联的菜单ID列表。如果角色为超级管理员，则返回所有菜单ID；
     * 如果角色ID为空或未找到相关菜单，则返回空列表。
     */
    @Override
    public List<Long> getRolePermSelectedByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("获取角色已选菜单ID列表时，角色ID为空。");
            return Collections.emptyList();
        }
        Set<String> roleKeys = sysRoleService.getRoleSetByRoleId(roleId);
        if (roleKeys.contains(SysRolesConstant.SUPER_ADMIN)) {
            log.info("角色ID {} (标识: {}) 是超级管理员，返回所有菜单ID。", roleId, roleKeys);
            return list().stream().map(SysMenu::getMenuId).distinct().collect(Collectors.toList());
        }
        return roleMenuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 此方法用于将给定的菜单列表构建为前端所需的路由对象列表（RouterVo）。主要流程包括：
     * 1. 过滤掉按钮类型的菜单项；
     * 2. 按照父级菜单ID和排序字段对菜单进行排序；
     * 3. 构建父子菜单关系映射，以便后续递归处理；
     * 4. 从顶级菜单开始递归生成对应的 RouterVo 对象，并过滤掉无效或空值；
     * 5. 返回最终的路由对象列表。
     * </p>
     *
     * <p>关键点说明：</p>
     * <ul>
     *     <li>{@link Constants.MenuConstants#TYPE_BUTTON} 类型的菜单会被排除，因为它们不需要在前端路由中展示。</li>
     *     <li>菜单会根据 parentId 和 sort 字段排序，确保构建的路由结构符合预期层级顺序。</li>
     *     <li>通过 parentChildrenMap 建立了父菜单与子菜单之间的关联，便于递归处理。</li>
     *     <li>递归调用 convertToRouterVoRecursive 方法将每个菜单节点转换为对应的 RouterVo 实例。</li>
     * </ul>
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            log.debug("输入菜单列表为空，无法构建路由。");
            return Collections.emptyList();
        }

        // 过滤掉按钮类型菜单，并根据 parentId 和 sort 排序
        List<SysMenu> processableMenus = menus.stream()
                .filter(menu -> !Constants.MenuConstants.TYPE_BUTTON.equals(menu.getMenuType()))
                .sorted(Comparator.comparing(SysMenu::getParentId, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        if (processableMenus.isEmpty()) {
            log.debug("过滤按钮后，可处理菜单列表为空。");
            return Collections.emptyList();
        }

        // 构建父子关系映射并按排序字段对每个子集进行排序
        Map<Long, List<SysMenu>> parentChildrenMap = processableMenus.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        parentChildrenMap.forEach((parentId, childrenList) ->
                childrenList.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
        );

        // 递归转换为 RouterVo 树形结构
        return processableMenus.stream()
                .filter(menu -> menu.getParentId() != null && menu.getParentId() == 0L)
                .map(menu -> convertToRouterVoRecursive(menu, parentChildrenMap, ""))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 填充RouterVo对象的path, name, component和MetaVo的frameSrc, keepAlive属性。
     * 此方法是convertToRouterVoRecursive的核心逻辑抽取，用于根据菜单类型和外链设置决定路由的关键属性。
     *
     * @param menu       当前SysMenu实体。
     * @param parentPath 父级路由路径。
     * @param router     要填充的RouterVo对象。
     * @param meta       要填充的MetaVo对象。
     */
    private void populateRouterProperties(SysMenu menu, String parentPath, RouterVo router, MetaVo meta) {
        String routerPathValue;
        // 默认使用数据库中的routeName (可能已被configureRouteName处理)
        String routerNameValue = menu.getRouteName();
        String componentPathValue;
        boolean isKeepAlive = false;

        // 情况1: "外部链接跳转"模式 (external_link = 1)
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())
                && Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame())) {
            routerPathValue = buildAndNormalizePathSegmentStructure(parentPath, menu.getPath());
            // 外部跳转链接不使用frameSrc
            meta.setFrameSrc(null);
            // 外部跳转链接无组件
            componentPathValue = null;
            log.debug("外部链接跳转: path='{}', name (URL)='{}'", routerPathValue, routerNameValue);
        }
        // 情况2: "内嵌Iframe"模式 (is_frame = 1, external_link != 1, 且 menu.path 是 HTTP(S) URL)
        else if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath())) {
            meta.setFrameSrc(menu.getPath());
            isKeepAlive = true;
            String segment = StrUtil.isNotBlank(menu.getRouteName()) ? menu.getRouteName() : "iframe-" + menu.getMenuId();
            routerPathValue = buildInternalPathStructure(parentPath, segment);
            componentPathValue = getComponentPathForRouter(menu);
            log.debug("内嵌Iframe: path='{}', name='{}', frameSrc='{}'", routerPathValue, routerNameValue, meta.getFrameSrc());
        }
        // 情况3: 普通内部菜单或目录
        else {
            routerPathValue = buildInternalPathStructure(parentPath, menu.getPath());
            componentPathValue = getComponentPathForRouter(menu);

            if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType()) && StrUtil.isNotBlank(componentPathValue)) {
                routerPathValue = appendIndexToMenuPath(routerPathValue, componentPathValue, menu.getPath());
                isKeepAlive = Constants.MenuConstants.CACHE_ENABLED.equals(menu.getIsCache());
            }
            log.debug("内部菜单/目录: path='{}', name='{}', component='{}'", routerPathValue, routerNameValue, componentPathValue);
        }

        router.setPath(routerPathValue.replaceAll("//+", "/"));

        //这边name如果path是外部链接并且是外部跳转的这边的属性就是path
        if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) && Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {
            router.setName(menu.getPath());
        } else {
            router.setName(StrUtil.isNotBlank(routerNameValue) ? routerNameValue : menu.getMenuId().toString());
        }
        router.setComponent(componentPathValue);
        meta.setKeepAlive(isKeepAlive);
    }

    /**
     * 递归地将SysMenu实体转换为前端RouterVo对象。
     *
     * @param menu              当前要转换的SysMenu实体。
     * @param parentChildrenMap 预先处理好的父ID到其子菜单列表的映射。子列表应已按sort排序。
     * @param parentPath        当前菜单的父级路由的完整内部路径。
     * @return 构建完成的RouterVo对象，或null（如果菜单不应生成路由）。
     */
    private RouterVo convertToRouterVoRecursive(SysMenu menu, Map<Long, List<SysMenu>> parentChildrenMap, String parentPath) {
        if (menu == null) {
            return null;
        }

        RouterVo router = new RouterVo();
        MetaVo meta = new MetaVo();

        // 填充标题、图标、权限等基础meta
        fillBaseMetaInfo(meta, menu);
        // 填充path, name, component及特定meta
        populateRouterProperties(menu, parentPath, router, meta);

        router.setMeta(meta);
        router.setQuery(menu.getQuery());
        router.setHidden(Constants.MenuConstants.HIDDEN.equals(menu.getVisible()));

        List<SysMenu> childrenEntities = parentChildrenMap.getOrDefault(menu.getMenuId(), Collections.emptyList());

        if (!childrenEntities.isEmpty()) {
            List<RouterVo> childrenRouters = childrenEntities.stream()
                    .map(childMenu -> convertToRouterVoRecursive(childMenu, parentChildrenMap, router.getPath()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!childrenRouters.isEmpty()) {
                router.setChildren(childrenRouters);
                router.setAlwaysShow(true);
            } else {
                router.setAlwaysShow(false);
            }
        } else {
            router.setAlwaysShow(false);
        }
        return router;
    }

    /**
     * 为MetaVo填充基础信息（标题、图标、显示状态、权限标识）。
     *
     * @param meta MetaVo实例
     * @param menu SysMenu实例
     */
    private void fillBaseMetaInfo(MetaVo meta, SysMenu menu) {
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        meta.setShowParent(true);
        meta.setShowLink(Constants.MenuConstants.VISIBLE.equals(menu.getVisible()));
        if (StrUtil.isNotBlank(menu.getPermission())) {
            meta.setAuths(new String[]{menu.getPermission()});
        } else if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            meta.setAuths(new String[]{""});
        }
    }

    /**
     * 根据菜单配置获取其对应的前端组件路径（供RouterVo使用）。
     *
     * @param menu SysMenu 实体
     * @return 组件路径字符串，或特定布局组件名（如"Layout", "ParentView"），或null。
     */
    private String getComponentPathForRouter(SysMenu menu) {
        if (menu == null) {
            return null;
        }

        // 情况1: "外部链接跳转"模式 (external_link = 1) -> 无组件
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {
            return null;
        }
        // 情况2: "内嵌Iframe"模式 (is_frame = 1, path是URL, external_link != 1) -> 通常无显式组件
        if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath())) {
            // 前端根据 meta.frameSrc 渲染
            return null;
        }

        // 情况3: 目录类型 (TYPE_DIRECTORY)
        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            // 如果DB中已配置component
            if (StrUtil.isNotBlank(menu.getComponent())) {
                return menu.getComponent();
            }
            return (menu.getParentId() == null || menu.getParentId() == 0L) ?
                    Constants.MenuConstants.LAYOUT : Constants.MenuConstants.PARENT_VIEW;
        }

        // 情况4: 菜单类型 (TYPE_MENU) (此时已排除外链和iframe)
        if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())) {
            return menu.getComponent();
        }

        // 按钮或其他未匹配情况
        return null;
    }

    /**
     * 递归地将 {@link SysMenu} 实体转换为 {@link Option} 树形结构（用于下拉选择）。
     *
     * @param menu     当前要转换的菜单实体。
     * @param allMenus 所有菜单的扁平列表，用于查找子节点。
     * @param menuMap  菜单ID到菜单实体的映射，用于高效获取排序等属性。
     * @return 包含子节点的 {@link Option} 对象。
     */
    private Option<Long> convertToOptionRecursive(SysMenu menu, List<SysMenu> allMenus, Map<Long, SysMenu> menuMap) {
        Option<Long> option = new Option<>();
        option.setValue(menu.getMenuId());
        option.setLabel(menu.getMenuName());
        List<Option<Long>> childrenOptions = allMenus.stream()
                .filter(childMenu -> menu.getMenuId().equals(childMenu.getParentId()))
                .map(childMenu -> convertToOptionRecursive(childMenu, allMenus, menuMap))
                .sorted(Comparator.comparing(o -> {
                    SysMenu childEntity = menuMap.get(o.getValue());
                    return childEntity != null ? childEntity.getSort() : Integer.MAX_VALUE;
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        if (!childrenOptions.isEmpty()) {
            option.setChildren(childrenOptions);
        }
        return option;
    }

    /**
     * 递归地获取指定父ID下的子菜单，并转换为 {@link SysMenuTreeList} 结构（通常用于权限分配树）。
     *
     * @param allMenus 所有菜单的扁平列表（应预先按sort排序）。
     * @param parentId 父菜单ID。
     * @return {@link SysMenuTreeList} 结构的子菜单列表。
     */
    private List<SysMenuTreeList> getChildrenAsMenuTreeList(List<SysMenu> allMenus, Long parentId) {
        if (parentId == null || allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return allMenus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                // 假设 allMenus 已经排序，或者 childrenList 在 map.forEach 中排序
                .map(menu -> {
                    SysMenuTreeList treeNode = new SysMenuTreeList();
                    BeanUtils.copyProperties(menu, treeNode);
                    // treeNode.setSort(menu.getSort()); // 如果 SysMenuTreeList 需要 sort
                    List<SysMenuTreeList> grandChildren = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!grandChildren.isEmpty()) {
                        treeNode.setChildren(grandChildren);
                    }
                    return treeNode;
                }).collect(Collectors.toList());
    }

    /**
     * 构建用于权限分配等场景的菜单树列表 ({@link SysMenuTreeList} 结构)。
     *
     * @param allMenus 原始菜单列表 (应预先按 {@code parentId} 和 {@code sort} 排序)。
     * @return {@link SysMenuTreeList} 结构的树形菜单列表。
     */
    private List<SysMenuTreeList> buildMenuTreeList(List<SysMenu> allMenus) {
        if (allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        return allMenus.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .map(menu -> {
                    SysMenuTreeList treeNode = new SysMenuTreeList();
                    BeanUtils.copyProperties(menu, treeNode);
                    List<SysMenuTreeList> children = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!children.isEmpty()) {
                        treeNode.setChildren(children);
                    }
                    return treeNode;
                })
                .collect(Collectors.toList());
    }

    /**
     * 根据组件路径构建路由名称。
     *
     * <p>该方法接收一个组件路径或文件名字符串，将其转换为驼峰格式的路由名称。
     * 处理逻辑如下：
     * <ul>
     *   <li>如果输入为空、null 或仅包含空白字符，则返回空字符串。</li>
     *   <li>移除文件扩展名（支持 vue/js/ts/tsx/jsx）。</li>
     *   <li>按斜杠 '/' 分割路径为多个部分。</li>
     *   <li>遍历每个部分，过滤掉空字符串和 "index" 片段，并将每个有效部分首字母大写后追加到结果中。</li>
     *   <li>如果所有片段都无效（如全是 index 或空），则尝试从后往前取第一个有效片段作为名称。</li>
     *   <li>如果仍未找到有效名称，则返回空字符串。</li>
     * </ul>
     * </p>
     *
     * @param pathOrComponent 组件路径或文件名字符串。例如："/views/user/index.vue", "components/Profile.js"
     * @return 构建后的路由名称，如："UserProfile", "ComponentsProfile"；若无效则返回空字符串
     *
     * <pre>{@code
     * 示例：
     * buildRouteNameFromComponentPath("/views/user/index.vue") => "User"
     * buildRouteNameFromComponentPath("components/Profile.js") => "ComponentsProfile"
     * buildRouteNameFromComponentPath("index/index.jsx") => "Index"
     * buildRouteNameFromComponentPath("") => ""
     * }</pre>
     */
    public static String buildRouteNameFromComponentPath(String pathOrComponent) {
        if (cn.hutool.core.util.StrUtil.isBlank(pathOrComponent)) {
            return "";
        }
        String pathWithoutExtension = pathOrComponent.replaceFirst("\\.(vue|js|ts|tsx|jsx)$", "");
        String[] parts = pathWithoutExtension.split("/");
        StringBuilder routeNameBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty() && !"index".equalsIgnoreCase(part)) {
                routeNameBuilder.append(capitalize(part));
            }
        }
        if (routeNameBuilder.isEmpty() && parts.length > 0) {
            for (int i = parts.length - 1; i >= 0; i--) {
                if (!parts[i].isEmpty() && !"index".equalsIgnoreCase(parts[i])) {
                    routeNameBuilder.append(capitalize(parts[i]));
                    break;
                }
            }
            if (routeNameBuilder.isEmpty()) {
                return "";
            }
        }
        return routeNameBuilder.toString();
    }

    /**
     * 将字符串的首字母转换为大写，其余部分保持不变。
     *
     * <p>如果输入字符串为空、null 或仅包含空白字符，则直接返回原字符串；
     * 否则将字符串的第一个字符转为大写，其余字符保持不变并返回新字符串。</p>
     *
     * @param str 需要处理的原始字符串
     * @return 首字母大写的字符串，或原样返回空/空白字符串
     *
     * <pre>{@code
     * 示例：
     * capitalize("hello") => "Hello"
     * capitalize("  world  ") => "  world  "
     * capitalize(null) => null
     * }</pre>
     */
    public static String capitalize(String str) {
        if (cn.hutool.core.util.StrUtil.isBlank(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 构建内部路由的路径结构。
     *
     * <p>此方法用于根据父级路由路径和当前菜单的路径段生成完整的内部路由路径。
     * 它会处理多种情况，包括绝对路径、相对路径以及空路径的情况，并确保最终路径格式正确且唯一。</p>
     *
     * @param parentPath  父级完整内部路径。可以为空或以 '/' 开头。
     * @param pathSegment 当前菜单的路径段 (来自 menu.getPath())。
     *                    如果路径段以 '/' 开头，则视为绝对路径，直接使用；
     *                    否则将基于父路径进行拼接。
     * @return 拼接并规范化后的完整内部路径。
     *
     * <pre>{@code
     * 示例：
     * buildInternalPathStructure("/", "user") => "/user"
     * buildInternalPathStructure("/user", "detail") => "/user/detail"
     * buildInternalPathStructure("", "home") => "/home"
     * buildInternalPathStructure("/settings/", "profile") => "/settings/profile"
     * }</pre>
     */
    public static String buildInternalPathStructure(String parentPath, String pathSegment) {
        String segment = cn.hutool.core.util.StrUtil.trimToEmpty(pathSegment);
        String fullPath;
        if (segment.startsWith("/")) {
            fullPath = segment;
        } else {
            if (cn.hutool.core.util.StrUtil.isBlank(parentPath) || "/".equals(parentPath)) {
                fullPath = "/" + segment;
            } else {
                String formattedParent = parentPath.endsWith("/") ? parentPath : parentPath + "/";
                fullPath = formattedParent + segment;
            }
        }
        fullPath = fullPath.replaceAll("//+", "/");
        if (fullPath.length() > 1 && fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        if (cn.hutool.core.util.StrUtil.isBlank(segment) && (cn.hutool.core.util.StrUtil.isBlank(parentPath) || "/".equals(parentPath))) {
            return "/";
        }
        return cn.hutool.core.util.StrUtil.isBlank(fullPath) ? "/" : fullPath;
    }

    /**
     * 构建并规范化路由路径结构。
     * <p>
     * 此方法接收父级路径和当前的路径段输入。
     * 如果当前路径段输入是一个HTTP(S)链接，它会尝试提取主机名并将其转换为大驼峰形式作为实际的路径段。
     * 如果不是HTTP(S)链接，则直接使用该输入（去除首尾空格）。
     * 然后，将处理后的路径段与父路径智能拼接（处理绝对/相对路径情况）。
     * 最后，对生成的完整路径进行规范化，包括去除多余的斜杠和末尾斜杠（根路径除外）。
     * </p>
     *
     * @param parentPath   父级路由路径。如果为 {@code null} 或空字符串，则视为根路径 "/"。
     * @param segmentInput 当前菜单的路径段输入。这可能是普通的内部路径段（如 "user", "/system/user"），
     */
    public static String buildAndNormalizePathSegmentStructure(String parentPath, String segmentInput) {
        String actualSegment;
        if (cn.zhangchuangla.common.utils.StringUtils.isHttp(segmentInput)) {
            try {
                java.net.URI uri = new java.net.URI(segmentInput);
                String host = uri.getHost();
                if (cn.hutool.core.util.StrUtil.isNotBlank(host)) {
                    String[] parts = host.split("\\.");
                    StringBuilder hostCamelCase = new StringBuilder();
                    for (String part : parts) {
                        if (!part.isEmpty()) {
                            hostCamelCase.append(Character.toUpperCase(part.charAt(0)))
                                    .append(part.substring(1).toLowerCase());
                        }
                    }
                    actualSegment = hostCamelCase.toString();
                    if (cn.hutool.core.util.StrUtil.isBlank(actualSegment)) {
                        actualSegment = "ExternalLinkPath";
                    }
                } else {
                    actualSegment = "ExternalHostMissing";
                }
            } catch (java.net.URISyntaxException e) {
                actualSegment = "InvalidExternalLink";
            }
        } else {
            actualSegment = cn.hutool.core.util.StrUtil.trimToEmpty(segmentInput);
        }
        String fullPath;
        if (actualSegment.startsWith("/")) {
            fullPath = actualSegment;
        } else {
            String normalizedParentPath = cn.hutool.core.util.StrUtil.isBlank(parentPath) ? "/" : parentPath;
            if ("/".equals(normalizedParentPath)) {
                fullPath = cn.hutool.core.util.StrUtil.isBlank(actualSegment) ? "/" : "/" + actualSegment;
            } else {
                String formattedParent = normalizedParentPath.endsWith("/") ? normalizedParentPath : normalizedParentPath + "/";
                fullPath = formattedParent + actualSegment;
            }
        }
        fullPath = fullPath.replaceAll("/{2,}", "/");
        if (fullPath.length() > 1 && fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        return cn.hutool.core.util.StrUtil.isBlank(fullPath) ? "/" : fullPath;
    }

    /**
     * 根据菜单路径和组件类型决定是否追加 "index" 路由段。
     *
     * <p>如果当前组件不是布局组件（如 Layout 或 ParentView），并且菜单的原始路径不以 "index" 结尾，
     * 同时满足以下条件之一：
     * <ul>
     *   <li>菜单原始路径非空</li>
     *   <li>当前路由路径为根路径 "/" </li>
     *   <li>当前路由路径以斜杠结尾</li>
     * </ul>
     * 则在当前路由路径后追加 "/index"。</p>
     *
     * @param currentRouterPath  当前已构建的完整路由路径，不能为空。
     * @param componentPathValue 当前菜单项关联的组件路径标识符，例如 "Layout", "ParentView" 等。
     *                           如果是这些特殊组件，则不会自动添加 index。
     * @param menuPathOriginal   当前菜单项的原始配置路径，用于判断是否需要添加 index。
     *                           如果该路径为空或仅包含空白字符，也可能影响 index 的添加逻辑。
     * @return 可能已追加了 "/index" 的新路由路径；否则返回原路由路径。
     *
     * <pre>{@code
     * 示例：
     * appendIndexToMenuPath("/user", "Layout", "user") => "/user"
     * appendIndexToMenuPath("/user", "UserComponent", "user") => "/user/index"
     * appendIndexToMenuPath("/user/", "UserComponent", "") => "/user//index"
     * }</pre>
     */
    public static String appendIndexToMenuPath(String currentRouterPath, String componentPathValue, String menuPathOriginal) {
        if (!cn.zhangchuangla.common.constant.Constants.MenuConstants.LAYOUT.equals(componentPathValue) &&
                !cn.zhangchuangla.common.constant.Constants.MenuConstants.PARENT_VIEW.equals(componentPathValue)) {
            String pathSegmentForIndexCheck = cn.hutool.core.util.StrUtil.trimToEmpty(menuPathOriginal);
            if (!pathSegmentForIndexCheck.endsWith("index") &&
                    (cn.hutool.core.util.StrUtil.isNotBlank(pathSegmentForIndexCheck) || "/".equals(currentRouterPath) || currentRouterPath.endsWith("/"))
            ) {
                return currentRouterPath.endsWith("/") ? (currentRouterPath + "index") : (currentRouterPath + "/index");
            }
        }
        return currentRouterPath;
    }

    /**
     * 配置菜单的路由名称 ({@code routeName})。
     * <p>
     * 此方法会根据菜单是否为"外部链接跳转"模式 (由 {@code external_link} 字段决定，
     * 假设值为 {@link Constants.MenuConstants#IS_EXTERNAL_LINK})
     * 来决定 {@code routeName} 的来源：
     * <ul>
     * <li>如果是"外部链接跳转"模式，则直接使用用户在 {@code routeName} 字段中提供的外部URL，
     * 并确保该URL本身是有效的HTTP(S)链接。不会再调用内部生成逻辑。</li>
     * <li>对于其他类型的菜单（如内嵌iframe、普通菜单、目录），则调用
     * {@link #generateAndSetUniqueInternalRouteName(SysMenu)} 来自动生成一个唯一的内部路由名。</li>
     * <li>对于按钮类型，或无路径且非外部跳转的目录，路由名称会设置为空字符串。</li>
     * </ul>
     * </p>
     *
     * @param sysMenu 菜单实体，其 {@code routeName} 字段将被此方法修改。
     */
    private void configureRouteName(SysMenu sysMenu) {
        // 情况1: "外部链接跳转"模式 (external_link = 1)
        // 此时，用户应在routeName字段直接提供外部URL。
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getExternalLink())) {
            // 确保routeName是URL
            if (!StringUtils.isHttp(sysMenu.getRouteName())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "外部链接跳转模式下，路由名称必须是有效的HTTP(S)链接地址。");
            }
            log.info("检测到“外部链接跳转”模式： path='{}', routeName (URL)='{}'. 将直接使用此routeName。",
                    sysMenu.getPath(), sysMenu.getRouteName());
            // routeName已经是外部URL，ensureRouteNameUnique 方法会直接返回它，不尝试唯一化。
            sysMenu.setRouteName(ensureRouteNameUnique(sysMenu.getRouteName(), sysMenu.getMenuId()));
            return;
        }

        // 情况2: 按钮类型，或者目录类型但没有路径且routeName不是外部URL (表示它不是外链跳转目录)
        // 这些类型的菜单通常没有或不需要路由名称。
        if (Constants.MenuConstants.TYPE_BUTTON.equals(sysMenu.getMenuType()) ||
                (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType()) &&
                        StrUtil.isBlank(sysMenu.getPath()) &&
                        (sysMenu.getRouteName() == null || !StringUtils.isHttp(sysMenu.getRouteName())))
        ) {
            // 设置为空字符串
            sysMenu.setRouteName("");
        }
        // 情况3: 其他情况 (如内嵌iframe, 普通菜单, 有路径的目录)
        // 这些需要根据其path或component生成一个内部路由名。
        else {
            // 调用通用的生成和唯一化逻辑
            generateAndSetUniqueInternalRouteName(sysMenu);
        }
    }

    /**
     * 为菜单生成并设置一个唯一的内部路由名称。
     * 此方法用于"内嵌iframe"和普通的"菜单"、"目录"类型。
     * 不适用于"外部链接跳转"模式（其routeName是外部URL，已由 {@link #configureRouteName(SysMenu)} 处理）。
     *
     * @param menu 菜单实体，其routeName将被设置为生成的内部名称。
     */
    private void generateAndSetUniqueInternalRouteName(SysMenu menu) {
        if (menu == null) {
            return;
        }
        String baseRouteName;

        // 情况1: 内嵌Iframe (isFrame=1, path是HTTP URL, external_link!=IS_EXTERNAL_LINK)
        // routeName 应基于一个稳定的内部标识。优先使用用户在routeName字段填写的内部名（如果合法），否则基于菜单名生成。
        if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) &&
                StringUtils.isHttp(menu.getPath()) &&
                !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {

            if (StrUtil.isNotBlank(menu.getRouteName()) && !StringUtils.isHttp(menu.getRouteName())) {
                // 用户已提供合法的内部路由名
                baseRouteName = capitalize(menu.getRouteName().replaceAll("[^a-zA-Z0-9]", ""));
            } else {
                // 用户未提供，或提供了URL（不应如此），则基于菜单名生成
                baseRouteName = capitalize(menu.getMenuName().replaceAll("[^a-zA-Z0-9]", ""));
            }
            // 如果菜单名全是特殊字符
            if (StrUtil.isBlank(baseRouteName)) {
                baseRouteName = "Iframe" + (menu.getMenuId() == null ? System.currentTimeMillis() % 10000 : menu.getMenuId());
            }
        }
        // 情况2: 普通菜单 (有组件路径，且非特殊外链类型)
        else if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType()) &&
                StrUtil.isNotBlank(menu.getComponent()) &&
                !Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) &&
                !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {
            baseRouteName = buildRouteNameFromComponentPath(menu.getComponent());
        }
        // 情况3: 有路径的目录 (且非特殊外链类型)
        else if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType()) &&
                StrUtil.isNotBlank(menu.getPath()) &&
                !Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) &&
                !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {
            baseRouteName = buildRouteNameFromComponentPath(menu.getPath());
        }
        // 情况4: 其他（如按钮、无路径目录、或已由configureRouteName处理的外部跳转链接），不应在此生成
        else {
            menu.setRouteName((StrUtil.isNotBlank(menu.getRouteName()) && !StringUtils.isHttp(menu.getRouteName())) ?
                    ensureRouteNameUnique(menu.getRouteName(), menu.getMenuId()) : "");
            return;
        }

        if (StrUtil.isBlank(baseRouteName)) {
            menu.setRouteName("");
            return;
        }
        menu.setRouteName(ensureRouteNameUnique(baseRouteName, menu.getMenuId()));
    }

    /**
     * 确保内部路由名称在数据库中的唯一性。
     * 如果基础名称是HTTP(S)链接，则直接返回。
     * 否则，如果名称已存在，则在基础名称后附加数字后缀 (1, 2, ...) 直到找到唯一的名称或达到最大尝试次数。
     *
     * @param baseName      希望使用的基础路由名称。
     * @param currentMenuId 当前正在操作的菜单ID。
     * @return 保证唯一的路由名称，或原始HTTP链接。
     * @throws ServiceException 如果在最大尝试次数后仍无法生成唯一的内部名称。
     */
    private String ensureRouteNameUnique(String baseName, Long currentMenuId) {
        if (StringUtils.isHttp(baseName)) {
            return baseName;
        }
        if (StrUtil.isBlank(baseName)) {
            return "";
        }
        final int MAX_RETRIES = 100;
        Long menuIdForExclusion = Optional.ofNullable(currentMenuId).orElse(-1L);
        String tempRouteName = baseName;
        for (int i = 0; i < MAX_RETRIES; i++) {
            SysMenu existingMenu = getOne(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getRouteName, tempRouteName)
                    .ne(SysMenu::getMenuId, menuIdForExclusion));
            if (existingMenu == null) {
                return tempRouteName;
            }
            log.warn("内部路由名称 '{}' 已存在，尝试生成新名称 (尝试次数: {})...", tempRouteName, i + 1);
            tempRouteName = baseName + (i + 1);
        }
        log.error("多次尝试后仍无法为基础名称 '{}' 生成唯一的内部路由名称。", baseName);
        throw new ServiceException(ResponseCode.OPERATION_ERROR, "生成唯一内部路由名称失败，可能存在大量冲突，请检查配置或稍后重试。");
    }

    /**
     * 校验菜单的路由路径 (path) 是否符合基本要求。
     *
     * @param sysMenu 待校验的 {@link SysMenu} 实体。
     * @throws ServiceException 如果路径不符合要求。
     */
    private void checkPathIsLegal(SysMenu sysMenu) {
        if (sysMenu == null) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单信息不能为空。");
        }

        if (!Constants.MenuConstants.TYPE_BUTTON.equals(sysMenu.getMenuType())) {
            boolean isEmbeddedIframe = Constants.MenuConstants.IS_FRAME.equals(sysMenu.getIsFrame()) &&
                    !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getExternalLink()) &&
                    StringUtils.isHttp(sysMenu.getPath());
            boolean isExternalRedirect = Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getExternalLink());

            if (!isEmbeddedIframe && !isExternalRedirect && StringUtils.isBlank(sysMenu.getPath())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "路由路径不能为空（按钮、内嵌iframe或外部跳转链接除外）。");
            }
        }
    }

    /**
     * 校验并设置菜单的父ID (parentId)。
     *
     * @param menu 待校验的 {@link SysMenu} 实体，其parentId可能会被修改。
     * @throws ServiceException 如果指定的父菜单ID无效或不存在。
     */
    private void checkParentIdIsLegal(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getParentId() != 0L) {
            if (getById(menu.getParentId()) == null) {
                log.error("指定的父菜单ID {} 不存在。", menu.getParentId());
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "指定的父菜单不存在。");
            }
        }
    }

    /**
     * 对菜单实体进行基础规则校验。
     *
     * <p>此方法用于验证菜单实体对象的基本业务规则，包括：
     * <ul>
     *     <li>组件路径不能为外部链接（HTTP/HTTPS）；</li>
     *     <li>如果该菜单存在子菜单，则菜单类型必须是目录（M）；</li>
     *     <li>菜单名称在同级目录下必须唯一；</li>
     *     <li>根据菜单配置的外链模式，分别对路由名称和路径进行格式校验；
     *         支持两种外链模式：外部链接跳转和内嵌iframe。</li>
     *     <li>上级菜单不能选择自身作为父级节点，防止循环引用。</li>
     * </ul>
     *
     * <p><b>参数说明：</b></p>
     * <ul>
     *     <li>{@code menu} - 待校验的 {@link SysMenu} 实体对象，不能为 null。</li>
     * </ul>
     *
     * <p><b>异常说明：</b></p>
     * <ul>
     *     <li>当任意一项校验不通过时，抛出 {@link ServiceException} 异常，并附带相应的错误提示信息。</li>
     * </ul>
     */
    private void menuBaseCheck(SysMenu menu) {
        if (StrUtil.isNotBlank(menu.getComponent()) && (menu.getComponent().contains(Constants.HTTP) || menu.getComponent().contains(Constants.HTTPS))) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "组件路径不应是外部链接。如需添加外链，请参考外链配置方式。");
        }
        if (menu.getMenuId() != null && hasChildByMenuId(menu.getMenuId())) {
            if (!Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "当前菜单下有子菜单，菜单类型只能是目录。");
            }
        }
        if (checkMenuNameUnique(menu)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单名称 '" + menu.getMenuName() + "' 已存在于同级目录下。");
        }

        boolean isExternalRedirect = Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink());
        boolean isEmbeddedIframe = Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) && !isExternalRedirect;


        // 明确为外部链接跳转模式
        if (isExternalRedirect) {
            // routeName 必须是 URL
            if (!StringUtils.isHttp(menu.getRouteName())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "外部链接跳转模式下，路由名称必须是有效的HTTP(S)链接地址。");
            }
            // path 必须是内部路径段且非空
            if (StringUtils.isHttp(menu.getPath()) || StrUtil.isBlank(menu.getPath())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "外部链接跳转模式下，路由路径必须是一个非空、非HTTP(S)的内部路径段。");
            }
            // 明确为内嵌iframe模式
        } else if (isEmbeddedIframe) {
            // path 必须是 URL
            if (!StringUtils.isHttp(menu.getPath())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "内嵌iframe模式下，路由路径必须是有效的HTTP(S)链接地址。");
            }
            // routeName 不应是 URL
            if (StringUtils.isHttp(menu.getRouteName())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "内嵌iframe模式下，路由名称不应是HTTP(S)链接，应为内部路由名。");
            }
        }
        if (menu.getMenuId() != null && menu.getMenuId().equals(menu.getParentId())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "上级菜单不能选择自己。");
        }
    }

    /**
     * 校验菜单的组件路径是否符合规范。
     *
     * @param menu 待校验的 {@link SysMenu} 实体。
     * @throws ServiceException 如果组件路径不符合规范。
     */
    private void checkComponentIsLegal(SysMenu menu) {
        // 检查是否为需要组件的内部菜单 (非按钮、非外部跳转、非内嵌iframe)
        boolean isExternalRedirect = Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink());
        boolean isEmbeddedIframe = Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) &&
                !isExternalRedirect &&
                StringUtils.isHttp(menu.getPath());

        boolean requiresComponent = Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType()) &&
                !isExternalRedirect &&
                !isEmbeddedIframe;

        if (requiresComponent && StrUtil.isBlank(menu.getComponent())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "普通菜单类型的组件路径不能为空。");
        }
        if (StrUtil.isNotBlank(menu.getComponent()) && menu.getComponent().startsWith("/")) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "组件路径预期为相对于views目录的相对路径，不应以 / 开头。例如：system/user/index");
        }
    }

    /**
     * 将扁平的菜单列表构建成用于后台管理界面树形表格的 {@link SysMenuListVo} 结构。
     *
     * @param allMenus 已按 {@code parentId} 和 {@code sort} 排序的完整菜单列表。
     * @return {@link SysMenuListVo} 结构的树形菜单列表。
     */
    private List<SysMenuListVo> buildTreeFormattedMenuList(List<SysMenu> allMenus) {
        if (allMenus == null || allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, List<SysMenu>> parentChildrenMap = allMenus.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        parentChildrenMap.forEach((parentId, childrenList) ->
                childrenList.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
        );

        return allMenus.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .map(menu -> convertToSysMenuListVoRecursive(menu, parentChildrenMap))
                .collect(Collectors.toList());
    }

    /**
     * 递归地将 {@link SysMenu} 实体转换为 {@link SysMenuListVo} 视图对象，并构建其子节点。
     *
     * @param menu              当前要转换的菜单实体。
     * @param parentChildrenMap 预处理好的父ID到其（已排序）子菜单列表的映射。
     * @return 包含子节点层级结构的 {@link SysMenuListVo} 对象。
     */
    private SysMenuListVo convertToSysMenuListVoRecursive(SysMenu menu, Map<Long, List<SysMenu>> parentChildrenMap) {
        SysMenuListVo vo = new SysMenuListVo();
        BeanUtils.copyProperties(menu, vo);
        List<SysMenu> childrenEntities = parentChildrenMap.getOrDefault(menu.getMenuId(), Collections.emptyList());

        if (!childrenEntities.isEmpty()) {
            vo.setChildren(childrenEntities.stream()
                    .map(child -> convertToSysMenuListVoRecursive(child, parentChildrenMap))
                    .collect(Collectors.toList()));
        }
        return vo;
    }

}
