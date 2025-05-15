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
 * 菜单权限服务实现类。
 * <p>
 * 负责处理菜单相关的业务逻辑，包括菜单的增删改查、权限构建、路由构建以及菜单树的生成等。
 * 重点处理了“内嵌iframe”和“外部链接跳转”两种外链模式的路由生成逻辑，以符合前端期望。
 * </p>
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenuUpdateRequest request) {
        if (request == null || request.getMenuId() == null) {
            log.warn("更新菜单失败：请求对象或菜单ID为空。");
            return false;
        }
        SysMenu sysMenu = sysMenuConverter.toEntity(request);

        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType())
                && (sysMenu.getParentId() == null || sysMenu.getParentId() == 0L)
                && StrUtil.isBlank(sysMenu.getComponent())) {
            sysMenu.setComponent(Constants.MenuConstants.LAYOUT);
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
     * 对菜单实体进行基础规则校验。
     *
     * @param menu 待校验的 {@link SysMenu} 实体。
     * @throws ServiceException 如果任一校验规则不通过。
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


        if (isExternalRedirect) { // 明确为外部链接跳转模式
            if (!StringUtils.isHttp(menu.getRouteName())) { // routeName 必须是 URL
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "外部链接跳转模式下，路由名称必须是有效的HTTP(S)链接地址。");
            }
            if (StringUtils.isHttp(menu.getPath()) || StrUtil.isBlank(menu.getPath())) { // path 必须是内部路径段且非空
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "外部链接跳转模式下，路由路径必须是一个非空、非HTTP(S)的内部路径段。");
            }
        } else if (isEmbeddedIframe) { // 明确为内嵌iframe模式
            if (!StringUtils.isHttp(menu.getPath())) { // path 必须是 URL
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "内嵌iframe模式下，路由路径必须是有效的HTTP(S)链接地址。");
            }
            if (StringUtils.isHttp(menu.getRouteName())) { // routeName 不应是 URL
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "内嵌iframe模式下，路由名称不应是HTTP(S)链接，应为内部路由名。");
            }
        }
        // 如果 external_link 和 is_frame 都不是1，则为普通内部菜单，此处不需额外校验外链。
        // 如果 external_link=1 且 is_frame=1，以 external_link=1 的逻辑为准。

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
     * {@inheritDoc}
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
        if (menuId == null) return false;
        return count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId)) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        if (menuId == null) return false;
        return roleMenuMapper.checkMenuExistRole(menuId) > 0;
    }

    /**
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
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMenu(SysMenuAddRequest request) {
        if (request == null) {
            log.warn("添加菜单失败：请求对象为空。");
            return false;
        }
        SysMenu sysMenu = sysMenuConverter.toEntity(request);

        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(request.getMenuType())
                && (request.getParentId() == null || request.getParentId() == 0L)
                && StrUtil.isBlank(sysMenu.getComponent())) {
            sysMenu.setComponent(Constants.MenuConstants.LAYOUT);
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
     * 配置菜单的路由名称 ({@code routeName})。
     * <p>
     * 此方法会根据菜单是否为“外部链接跳转”模式 (由 {@code external_link} 字段决定，
     * 假设值为 {@link Constants.MenuConstants#IS_EXTERNAL_LINK})
     * 来决定 {@code routeName} 的来源：
     * <ul>
     * <li>如果是“外部链接跳转”模式，则直接使用用户在 {@code routeName} 字段中提供的外部URL，
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
            if (!StringUtils.isHttp(sysMenu.getRouteName())) { // 确保routeName是URL
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
            sysMenu.setRouteName(""); // 设置为空字符串
        }
        // 情况3: 其他情况 (如内嵌iframe, 普通菜单, 有路径的目录)
        // 这些需要根据其path或component生成一个内部路由名。
        else {
            generateAndSetUniqueInternalRouteName(sysMenu); // 调用通用的生成和唯一化逻辑
        }
    }

    /**
     * 为菜单生成并设置一个唯一的内部路由名称。
     * 此方法用于“内嵌iframe”和普通的“菜单”、“目录”类型。
     * 不适用于“外部链接跳转”模式（其routeName是外部URL，已由 {@link #configureRouteName(SysMenu)} 处理）。
     *
     * @param menu 菜单实体，其routeName将被设置为生成的内部名称。
     */
    private void generateAndSetUniqueInternalRouteName(SysMenu menu) {
        if (menu == null) return;
        String baseRouteName;

        // 情况1: 内嵌Iframe (isFrame=1, path是HTTP URL, external_link!=IS_EXTERNAL_LINK)
        // routeName 应基于一个稳定的内部标识。优先使用用户在routeName字段填写的内部名（如果合法），否则基于菜单名生成。
        if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) &&
                StringUtils.isHttp(menu.getPath()) &&
                !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {

            if (StrUtil.isNotBlank(menu.getRouteName()) && !StringUtils.isHttp(menu.getRouteName())) {
                // 用户已提供合法的内部路由名
                baseRouteName = capitalize(menu.getRouteName().replaceAll("[^a-zA-Z0-9]", ""));
            } else { // 用户未提供，或提供了URL（不应如此），则基于菜单名生成
                baseRouteName = capitalize(menu.getMenuName().replaceAll("[^a-zA-Z0-9]", ""));
            }
            if (StrUtil.isBlank(baseRouteName)) { // 如果菜单名全是特殊字符
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
     * 从组件路径（或类似结构的路径段，如目录的path）构建基础路由名称。
     * 会移除常见扩展名，忽略路径末尾的 "index" 段，并将各部分首字母大写后拼接。
     *
     * @param pathOrComponent 组件路径或路径段。
     * @return 构建的基础路由名称。如果无法生成则返回空字符串。
     */
    private String buildRouteNameFromComponentPath(String pathOrComponent) {
        if (StrUtil.isBlank(pathOrComponent)) return "";

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
            if (existingMenu == null) return tempRouteName;
            log.warn("内部路由名称 '{}' 已存在，尝试生成新名称 (尝试次数: {})...", tempRouteName, i + 1);
            tempRouteName = baseName + (i + 1);
        }
        log.error("多次尝试后仍无法为基础名称 '{}' 生成唯一的内部路由名称。", baseName);
        throw new ServiceException(ResponseCode.OPERATION_ERROR, "生成唯一内部路由名称失败，可能存在大量冲突，请检查配置或稍后重试。");
    }

    /**
     * 将字符串的首字母转为大写。
     *
     * @param str 原始字符串。
     * @return 首字母大写后的字符串；如果输入为null或isBlank，则原样返回。
     */
    private String capitalize(String str) {
        if (StrUtil.isBlank(str)) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 校验菜单的路由路径 (path) 是否符合基本要求。
     *
     * @param sysMenu 待校验的 {@link SysMenu} 实体。
     * @throws ServiceException 如果路径不符合要求。
     */
    private void checkPathIsLegal(SysMenu sysMenu) {
        if (sysMenu == null) throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单信息不能为空。");

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
        if (menu.getParentId() == null) menu.setParentId(0L);
        if (menu.getParentId() != 0L) {
            if (getById(menu.getParentId()) == null) {
                log.error("指定的父菜单ID {} 不存在。", menu.getParentId());
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "指定的父菜单不存在。");
            }
        }
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    @Override
    public List<SysMenuListVo> listMenu(SysMenuListRequest request) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        if (request != null && StrUtil.isNotBlank(request.getMenuName())) {
            queryWrapper.like(SysMenu::getMenuName, request.getMenuName());
        }
        queryWrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getSort);
        List<SysMenu> allMenus = list(queryWrapper);
        return buildTreeFormattedMenuList(allMenus);
    }

    /**
     * 将扁平的菜单列表构建成用于后台管理界面树形表格的 {@link SysMenuListVo} 结构。
     *
     * @param allMenus 已按 {@code parentId} 和 {@code sort} 排序的完整菜单列表。
     * @return {@link SysMenuListVo} 结构的树形菜单列表。
     */
    private List<SysMenuListVo> buildTreeFormattedMenuList(List<SysMenu> allMenus) {
        if (allMenus == null || allMenus.isEmpty()) return Collections.emptyList();
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
        SysMenuListVo vo = sysMenuConverter.toMenuListVo(menu);
        List<SysMenu> childrenEntities = parentChildrenMap.getOrDefault(menu.getMenuId(), Collections.emptyList());

        if (!childrenEntities.isEmpty()) {
            vo.setChildren(childrenEntities.stream()
                    .map(child -> convertToSysMenuListVoRecursive(child, parentChildrenMap))
                    .collect(Collectors.toList()));
        }
        return vo;
    }

    /**
     * {@inheritDoc}
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
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            log.debug("输入菜单列表为空，无法构建路由。");
            return Collections.emptyList();
        }

        List<SysMenu> processableMenus = menus.stream()
                .filter(menu -> !Constants.MenuConstants.TYPE_BUTTON.equals(menu.getMenuType()))
                .sorted(Comparator.comparing(SysMenu::getParentId, Comparator.nullsFirst(Comparator.naturalOrder()))
                        .thenComparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        if (processableMenus.isEmpty()) {
            log.debug("过滤按钮后，可处理菜单列表为空。");
            return Collections.emptyList();
        }

        Map<Long, List<SysMenu>> parentChildrenMap = processableMenus.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        parentChildrenMap.forEach((parentId, childrenList) ->
                childrenList.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
        );

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
        String routerNameValue = menu.getRouteName(); // 默认使用数据库中的routeName (可能已被configureRouteName处理)
        String componentPathValue;
        boolean isKeepAlive = false;

        // 情况1: "外部链接跳转"模式 (external_link = 1)
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {
            // menu.getPath() 此时应为内部路径段，如 "vite", "anything"
            // menu.getRouteName() 此时应为完整的外部URL，如 "https://vitejs.dev"
            routerPathValue = buildInternalPathStructure(parentPath, menu.getPath());
            // routerNameValue 已初始化为 menu.getRouteName() (即外部URL)
            meta.setFrameSrc(null); // 外部跳转链接不使用frameSrc
            isKeepAlive = Constants.MenuConstants.CACHE_ENABLED.equals(menu.getIsCache()); // 或对于外部跳转默认为false
            componentPathValue = null; // 外部跳转链接无组件
            log.debug("外部链接跳转: path='{}', name (URL)='{}'", routerPathValue, routerNameValue);
        }
        // 情况2: "内嵌Iframe"模式 (is_frame = 1, external_link != 1, 且 menu.path 是 HTTP(S) URL)
        else if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath())) {
            meta.setFrameSrc(menu.getPath()); // menu.path作为iframe的源
            isKeepAlive = true;               // iframe通常建议缓存

            // router.path 应为内部路径，可基于内部routeName或ID生成
            // menu.getRouteName() 此时应为内部路由名，如 "MyViteIframe"
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
        if (menu == null) return null;

        RouterVo router = new RouterVo();
        MetaVo meta = new MetaVo();

        fillBaseMetaInfo(meta, menu); // 填充标题、图标、权限等基础meta
        populateRouterProperties(menu, parentPath, router, meta); // 填充path, name, component及特定meta

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
        meta.setShowLink(Constants.MenuConstants.VISIBLE.equals(menu.getVisible()));
        if (StrUtil.isNotBlank(menu.getPermission())) {
            meta.setAuths(new String[]{menu.getPermission()});
        } else if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            meta.setAuths(new String[]{""});
        }
    }

    /**
     * 为“菜单”类型的路由路径按需添加 "/index" 后缀。
     * 不为LAYOUT和PARENT_VIEW组件添加此后缀。
     *
     * @param currentRouterPath  当前已构建的基础路由路径。
     * @param componentPathValue 菜单的组件路径。
     * @param menuPathOriginal   菜单原始的path字段值（用于检查是否已是index）。
     * @return 添加后缀（如果适用）后的路由路径。
     */
    private String appendIndexToMenuPath(String currentRouterPath, String componentPathValue, String menuPathOriginal) {
        // 仅当组件路径不是布局组件时才考虑添加 /index
        if (!Constants.MenuConstants.LAYOUT.equals(componentPathValue) &&
                !Constants.MenuConstants.PARENT_VIEW.equals(componentPathValue)) {

            String pathSegmentForIndexCheck = StrUtil.trimToEmpty(menuPathOriginal);
            // 避免给已经是 /index 结尾的路径重复添加，或给空路径段（如根目录下的菜单）错误添加
            // 只有当路径段本身非空，或者当前完整路径是根"/"或以"/"结尾的父目录时，才适合添加/index
            if (!pathSegmentForIndexCheck.endsWith("index") &&
                    (StrUtil.isNotBlank(pathSegmentForIndexCheck) || "/".equals(currentRouterPath) || currentRouterPath.endsWith("/"))
            ) {
                return currentRouterPath.endsWith("/") ? (currentRouterPath + "index") : (currentRouterPath + "/index");
            }
        }
        return currentRouterPath;
    }

    /**
     * 构建内部路由的路径结构。
     *
     * @param parentPath  父级完整内部路径。
     * @param pathSegment 当前菜单的路径段 (来自 menu.getPath())。
     * @return 拼接并规范化后的完整内部路径。
     */
    private String buildInternalPathStructure(String parentPath, String pathSegment) {
        String segment = StrUtil.trimToEmpty(pathSegment);
        String fullPath;

        if (segment.startsWith("/")) {
            fullPath = segment;
        } else {
            if (StrUtil.isBlank(parentPath) || "/".equals(parentPath)) {
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
        if (StrUtil.isBlank(segment) && (StrUtil.isBlank(parentPath) || "/".equals(parentPath))) {
            return "/";
        }
        return StrUtil.isBlank(fullPath) ? "/" : fullPath;
    }

    /**
     * 根据菜单配置获取其对应的前端组件路径（供RouterVo使用）。
     *
     * @param menu SysMenu 实体
     * @return 组件路径字符串，或特定布局组件名（如"Layout", "ParentView"），或null。
     */
    private String getComponentPathForRouter(SysMenu menu) {
        if (menu == null) return null;

        // 情况1: "外部链接跳转"模式 (external_link = 1) -> 无组件
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getExternalLink())) {
            return null;
        }
        // 情况2: "内嵌Iframe"模式 (is_frame = 1, path是URL, external_link != 1) -> 通常无显式组件
        if (Constants.MenuConstants.IS_FRAME.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath())) {
            return null; // 前端根据 meta.frameSrc 渲染
        }

        // 情况3: 目录类型 (TYPE_DIRECTORY)
        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            if (StrUtil.isNotBlank(menu.getComponent())) { // 如果DB中已配置component
                return menu.getComponent();
            }
            return (menu.getParentId() == null || menu.getParentId() == 0L) ?
                    Constants.MenuConstants.LAYOUT : Constants.MenuConstants.PARENT_VIEW;
        }

        // 情况4: 菜单类型 (TYPE_MENU) (此时已排除外链和iframe)
        if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())) {
            return menu.getComponent();
        }

        return null; // 按钮或其他未匹配情况
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
        if (!childrenOptions.isEmpty()) option.setChildren(childrenOptions);
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
        if (parentId == null || allMenus == null || allMenus.isEmpty()) return Collections.emptyList();
        return allMenus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                // 假设 allMenus 已经排序，或者 childrenList 在 map.forEach 中排序
                .map(menu -> {
                    SysMenuTreeList treeNode = sysMenuConverter.toMenuTreeList(menu);
                    // treeNode.setSort(menu.getSort()); // 如果 SysMenuTreeList 需要 sort
                    List<SysMenuTreeList> grandChildren = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!grandChildren.isEmpty()) treeNode.setChildren(grandChildren);
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
        if (allMenus == null || allMenus.isEmpty()) return Collections.emptyList();
        return allMenus.stream()
                .filter(menu -> menu.getParentId() == 0L)
                .map(menu -> {
                    SysMenuTreeList treeNode = sysMenuConverter.toMenuTreeList(menu);
                    List<SysMenuTreeList> children = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!children.isEmpty()) treeNode.setChildren(children);
                    return treeNode;
                })
                .collect(Collectors.toList());
    }
}
