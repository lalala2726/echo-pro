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
     * 根据用户ID获取菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> getMenuListByUserId(Long userId) {
        if (userId == null) {
            log.warn("根据用户ID查询菜单列表时，用户ID为空");
            return Collections.emptyList();
        }
        Set<String> roleSetByUserId = sysRoleService.getRoleSetByUserId(userId);
        if (roleSetByUserId.contains(SysRolesConstant.SUPER_ADMIN)) {
            log.info("用户ID {} 是超级管理员，返回所有有效菜单，并按父ID和排序值排序", userId);
            return list(new LambdaQueryWrapper<SysMenu>()
                    .orderByAsc(SysMenu::getParentId) // 先按父ID排序，保证父节点在前
                    .orderByAsc(SysMenu::getSort));   // 再按自定义排序值排序
        }
        log.debug("用户ID {} 非超级管理员，根据权限查询菜单", userId);
        List<SysMenu> userMenus = menuMapper.getMenuListByUserId(userId);
        userMenus.sort(Comparator.comparing(SysMenu::getParentId, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));
        return userMenus;
    }

    /**
     * 根据菜单ID查询菜单列表
     *
     * @param menuId 菜单ID
     * @return 菜单列表
     */
    @Override
    public SysMenu getMenuById(Long menuId) {
        if (menuId == null) {
            log.warn("查询菜单信息时，菜单ID为空");
            return null;
        }
        return getById(menuId);
    }

    /**
     * 构建菜单树结构
     *
     * @param menus 菜单列表
     * @return 菜单树结构
     */
    @Override
    public List<Option<Long>> buildMenuTree(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, SysMenu> menuMap = menus.stream()
                .collect(Collectors.toMap(SysMenu::getMenuId, Function.identity(), (k1, k2) -> k1));
        return menus.stream()
                .filter(menu -> menu.getParentId() == 0) // 筛选根节点
                .map(menu -> convertToOptionRecursive(menu, menus, menuMap)) // 递归转换
                .sorted(Comparator.comparing(o -> { // 按排序值排序
                    SysMenu menuEntity = menuMap.get(o.getValue());
                    return menuEntity != null ? menuEntity.getSort() : Integer.MAX_VALUE;
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 构造菜单选项
     *
     * @param menus 菜单
     * @return 菜单选项列表
     */
    @Override
    public List<Option<Long>> buildMenuOption(List<SysMenu> menus) {
        if (menus == null) {
            return Collections.emptyList();
        }
        return buildMenuTree(menus);
    }

    /**
     * 修改菜单信息
     *
     * @param request 菜单信息
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenuUpdateRequest request) {
        if (request == null || request.getMenuId() == null) {
            log.warn("更新菜单失败：请求对象或菜单ID为空");
            return false;
        }
        SysMenu sysMenu = sysMenuConverter.toEntity(request);

        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType())
                && (sysMenu.getParentId() == null || sysMenu.getParentId() == 0L)) {
            sysMenu.setComponent(Constants.MenuConstants.LAYOUT);
        }

        menuBaseCheck(sysMenu);
        checkPathIsLegal(sysMenu);
        checkParentIdIsLegal(sysMenu);
        checkComponentIsLegal(sysMenu);
        configureRouteName(sysMenu); // 配置路由名称

        if (!Arrays.asList(Constants.MenuConstants.TYPE_DIRECTORY, Constants.MenuConstants.TYPE_MENU,
                Constants.MenuConstants.TYPE_BUTTON).contains(sysMenu.getMenuType())) {
            log.error("更新菜单失败：菜单类型不合法 - {}", sysMenu.getMenuType());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单类型不合法");
        }

        String username = SecurityUtils.getUsername();
        sysMenu.setUpdateBy(username);
        log.info("用户 {} 正在更新菜单 ID: {}", username, sysMenu.getMenuId());
        return updateById(sysMenu);
    }

    /**
     * 检查菜单基本信息是否合法
     *
     * @param menu 菜单
     */
    private void menuBaseCheck(SysMenu menu) {
        if (StrUtil.isNotBlank(menu.getComponent()) && (menu.getComponent().contains(Constants.HTTP) || menu.getComponent().contains(Constants.HTTPS))) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "组件路径不应是外部链接。如需添加外链，请将链接地址配置到菜单的“路由路径”或“路由名称”属性中（根据外链类型），并将“是否外链”设为“是”。");
        }
        if (menu.getMenuId() != null && hasChildByMenuId(menu.getMenuId())) {
            if (!Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "当前菜单下有子菜单，菜单类型只能是目录");
            }
        }
        if (checkMenuNameUnique(menu)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单名称 '" + menu.getMenuName() + "' 已存在于同级目录下");
        }
        // 校验外链配置
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame())) {
            boolean isPathHttp = StringUtils.isHttp(menu.getPath());
            boolean isRouteNameHttp = StringUtils.isHttp(menu.getRouteName());

            // 模式1: 内嵌iframe (path是URL, routeName是内部名)
            if (isPathHttp) {
                if (isRouteNameHttp) { // routeName此时不应是URL
                    throw new ServiceException(ResponseCode.OPERATION_ERROR, "内嵌iframe模式下，路由名称不应是HTTP(S)链接。");
                }
            }
            // 模式2: 外部链接跳转 (routeName是URL, path是内部段)
            else if (isRouteNameHttp) {
                // path此时不应是URL (已经被isPathHttp=false覆盖，但明确一下)
                // 并且path不能为空
                if (StrUtil.isBlank(menu.getPath())) {
                    throw new ServiceException(ResponseCode.OPERATION_ERROR, "外部链接跳转模式下，路由路径（内部段）不能为空。");
                }
            }
            // 两种模式都不符合
            else {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "外链/内嵌iframe配置不正确：当“是否外链”为“是”时，需满足以下任一条件：1. 路由路径为http(s)链接（内嵌iframe）；2. 路由名称为http(s)链接且路由路径为内部路径段（外链跳转）。");
            }
        }

        if (menu.getMenuId() != null && menu.getMenuId().equals(menu.getParentId())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "上级菜单不能选择自己");
        }
    }

    /**
     * 检查组件是否合法
     *
     * @param menu 菜单
     */
    private void checkComponentIsLegal(SysMenu menu) {
        if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())
                && (menu.getIsFrame() == null || !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame()))) {
            if (StrUtil.isBlank(menu.getComponent())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单类型的组件路径不能为空 (非外链)");
            }
        }
        if (StrUtil.isNotBlank(menu.getComponent()) && menu.getComponent().startsWith("/")) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "组件路径预期为相对于views目录的相对路径，不应以 / 开头。例如：system/user/index");
        }
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenuById(Long menuId) {
        if (menuId == null) {
            log.warn("删除菜单失败：菜单ID为空");
            return false;
        }
        if (hasChildByMenuId(menuId)) {
            log.warn("删除菜单失败：菜单ID {} 存在子菜单", menuId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "请先删除子菜单");
        }
        if (checkMenuExistRole(menuId)) {
            log.warn("删除菜单失败：菜单ID {} 已分配给角色", menuId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单已分配，不能删除");
        }
        log.info("正在删除菜单 ID: {}", menuId);
        return removeById(menuId);
    }

    /**
     * 检查菜单名称是否唯一
     *
     * @param menu 菜单对象
     * @return true - 唯一，false - 不唯一
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        if (menu == null || StrUtil.isEmpty(menu.getMenuName())) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单名称不能为空");
        }
        Long menuId = Objects.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu existMenu = getOne(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getMenuName, menu.getMenuName())
                .eq(SysMenu::getParentId, menu.getParentId() == null ? 0L : menu.getParentId()));
        return existMenu != null && !existMenu.getMenuId().equals(menuId);
    }

    /**
     * 检查菜单是否存在子节点
     *
     * @param menuId 菜单ID
     * @return true - 存在，false - 不存在
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
     * 检查菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return true - 存在，false - 不存在
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        if (menuId == null) {
            return false;
        }
        return roleMenuMapper.checkMenuExistRole(menuId) > 0;
    }

    /**
     * 获取菜单选项列表
     *
     * @param onlyParent 是否只查询父级菜单
     * @return 菜单选项列表
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMenu(SysMenuAddRequest request) {
        if (request == null) {
            log.warn("添加菜单失败：请求对象为空");
            return false;
        }
        SysMenu sysMenu = sysMenuConverter.toEntity(request);

        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(request.getMenuType())
                && (request.getParentId() == null || request.getParentId() == 0L)) {
            sysMenu.setComponent(Constants.MenuConstants.LAYOUT);
        }

        // 在执行基础检查前，先尝试配置routeName，因为menuBaseCheck中可能依赖它
        configureRouteName(sysMenu); // 确保在baseCheck前处理，因为baseCheck中的外链判断可能依赖routeName
        menuBaseCheck(sysMenu); // 基础检查（包含外链配置的联合校验）
        checkPathIsLegal(sysMenu);
        checkParentIdIsLegal(sysMenu);
        checkComponentIsLegal(sysMenu);

        // 再次确保 routeName 配置，以防 configureRouteName 未完全覆盖所有场景或依赖后续字段

        if (!Arrays.asList(Constants.MenuConstants.TYPE_DIRECTORY, Constants.MenuConstants.TYPE_MENU,
                Constants.MenuConstants.TYPE_BUTTON).contains(sysMenu.getMenuType())) {
            log.error("添加菜单失败：菜单类型不合法 - {}", sysMenu.getMenuType());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单类型不合法");
        }

        String username = SecurityUtils.getUsername();
        sysMenu.setCreateBy(username);
        log.info("用户 {} 正在添加新菜单: {}", username, sysMenu.getMenuName());
        return save(sysMenu);
    }

    /**
     * 配置菜单的路由名称 (routeName)。
     * 根据菜单类型和外链设置，决定是直接使用用户提供的routeName（如外部URL），还是自动生成。
     *
     * @param sysMenu 菜单实体，其routeName将被修改
     */
    private void configureRouteName(SysMenu sysMenu) {
        // Case 1: "外部链接跳转"模式 (isFrame=1, path是内部段, routeName是外部URL)
        // 这种模式下，用户应该在routeName字段直接提供外部URL。
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getIsFrame()) &&
                !StringUtils.isHttp(sysMenu.getPath()) && // 路径是内部段
                StringUtils.isHttp(sysMenu.getRouteName())) { // 路由名称字段包含HTTP(S)链接

            log.info("检测到“外部链接跳转”模式： path='{}', routeName (URL)='{}'. 将直接使用此routeName。",
                    sysMenu.getPath(), sysMenu.getRouteName());
            sysMenu.setRouteName(ensureRouteNameUnique(sysMenu.getRouteName(), sysMenu.getMenuId()));
            return;
        }

        // Case 2: 按钮类型，或者目录类型但没有路径且routeName不是外部URL (表示它不是外链跳转目录)
        // 这些类型的菜单通常没有或不需要路由名称。
        if (Constants.MenuConstants.TYPE_BUTTON.equals(sysMenu.getMenuType()) ||
                (Constants.MenuConstants.TYPE_DIRECTORY.equals(sysMenu.getMenuType()) &&
                        StrUtil.isBlank(sysMenu.getPath()) &&
                        (sysMenu.getRouteName() == null || !StringUtils.isHttp(sysMenu.getRouteName())))
        ) {
            sysMenu.setRouteName(""); // 设置为空字符串
        }
        // Case 3: 其他情况 (如内嵌iframe, 普通菜单, 有路径的目录)
        // 这些需要根据其path或component生成一个内部路由名。
        else {
            String generatedRouteName = generateRouteName(sysMenu); // 调用通用的生成和唯一化逻辑
            sysMenu.setRouteName(generatedRouteName);
        }
    }

    /**
     * 生成路由名称的内部逻辑 (不包含唯一性检查的外部调用点)
     *
     * @param menu 菜单实体
     * @return 基于规则生成的基础路由名称
     */
    public String generateRouteName(SysMenu menu) {
        if (menu == null) return "";
        String baseRouteName;

        // 情况1: 内嵌Iframe (isFrame=1, path是HTTP URL)
        // routeName 应该基于组件名(如果有，不常见)或菜单名，而不是基于 path URL。
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath())) {
            if (StrUtil.isNotBlank(menu.getComponent())) {
                baseRouteName = buildRouteNameFromComponent(menu.getComponent());
            } else {
                baseRouteName = capitalize(menu.getMenuName().replaceAll("[^a-zA-Z0-9]", ""));
                if (StrUtil.isBlank(baseRouteName))
                    baseRouteName = "Iframe" + (menu.getMenuId() == null ? System.currentTimeMillis() % 10000 : menu.getMenuId()); // 增加唯一性以防菜单名全为特殊字符
            }
        }
        // 情况2: 普通菜单 (有组件路径，且非外链)
        else if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType()) &&
                StrUtil.isNotBlank(menu.getComponent()) &&
                (menu.getIsFrame() == null || !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame()))) {
            baseRouteName = buildRouteNameFromComponent(menu.getComponent());
        }
        // 情况3: 目录且有路径 (路径可能用于生成路由名，且非外链)
        else if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType()) &&
                StrUtil.isNotBlank(menu.getPath()) &&
                (menu.getIsFrame() == null || !Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame()))) {
            baseRouteName = buildRouteNameFromComponent(menu.getPath());
        }
        // 情况4: 其他情况，无法明确生成规则
        else {
            return "";
        }

        if (StrUtil.isBlank(baseRouteName)) return "";
        // 注意：ensureRouteNameUnique 会处理 baseName 是 HTTP URL 的情况，直接返回
        return ensureRouteNameUnique(baseRouteName, menu.getMenuId());
    }

    /**
     * 根据组件路径构建路由名称。
     * <p>
     * 此方法将组件路径转换成一个合适的 Vue 路由名称。它会移除文件扩展名（如 .vue、.js、.ts），
     * 然后将路径按斜杠分割，对每个部分进行首字母大写处理，并忽略空字符串和 "index" 部分。
     * 如果所有部分都被忽略，则返回默认名称 "DefaultRouteName"。
     * </p>
     *
     * @param componentPath 组件的文件路径，例如 "system/user/index.vue"
     * @return 生成的路由名称，例如 "SystemUser"
     */
    private String buildRouteNameFromComponent(String componentPath) {
        if (componentPath == null || componentPath.isBlank()) return "";
        // 移除文件扩展名
        String pathWithoutExtension = componentPath.replaceFirst("\\.(vue|js|ts)$", "");
        // 按路径分割
        String[] parts = pathWithoutExtension.split("/");
        StringBuilder routeNameBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty() && !"index".equalsIgnoreCase(part)) {
                routeNameBuilder.append(capitalize(part));
            }
        }
        // 如果没有有效部分且数组非空，则尝试从后往前找有效部分
        if (routeNameBuilder.isEmpty() && parts.length > 0) {
            for (int i = parts.length - 1; i >= 0; i--) {
                if (!parts[i].isEmpty() && !"index".equalsIgnoreCase(parts[i])) {
                    routeNameBuilder.append(capitalize(parts[i]));
                    break;
                }
            }
            // 如果仍然没有找到有效部分，则使用默认名称
            if (routeNameBuilder.isEmpty()) {
                routeNameBuilder.append("DefaultRouteName");
            }
        }
        return routeNameBuilder.toString();
    }

    /**
     * 确保路由名称唯一。
     * <p>
     * 如果给定的基础名称已经存在，则尝试在其后添加数字后缀（如 "BaseName1", "BaseName2" 等），
     * 直到找到一个唯一的名称或达到最大重试次数。
     * </p>
     *
     * @param baseName      基础名称
     * @param currentMenuId 当前菜单ID（用于排除当前菜单）
     * @return 唯一的路由名称
     */
    private String ensureRouteNameUnique(String baseName, Long currentMenuId) {
        if (StringUtils.isHttp(baseName)) { // 如果baseName是URL (用于“外部链接跳转”模式的routeName)
            return baseName; // 直接返回，不进行唯一化处理
        }
        final int MAX_RETRIES = 100;
        Long menuIdForExclusion = Optional.ofNullable(currentMenuId).orElse(-1L);
        String tempRouteName = baseName;
        for (int i = 0; i < MAX_RETRIES; i++) {
            SysMenu existingMenu = getOne(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getRouteName, tempRouteName)
                    .ne(SysMenu::getMenuId, menuIdForExclusion));
            if (existingMenu == null) return tempRouteName;
            log.warn("路由名称 '{}' 已存在，尝试生成新名称...", tempRouteName);
            tempRouteName = baseName + (i + 1);
        }
        log.error("多次尝试后仍无法为基础名称 '{}' 生成唯一的路由名称", baseName);
        throw new ServiceException(ResponseCode.OPERATION_ERROR, "生成唯一路由名称失败，可能存在大量冲突，请检查配置或稍后重试");
    }

    /**
     * 将字符串的首字母转换为大写，其余部分保持不变。
     *
     * @param str 输入的字符串
     * @return 首字母大写后的字符串；如果输入为 null 或空字符串，则返回原值
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 检查路由路径是否合法
     *
     * @param sysMenu 菜单对象
     */
    private void checkPathIsLegal(SysMenu sysMenu) {
        if (sysMenu == null) throw new ServiceException(ResponseCode.OPERATION_ERROR, "菜单信息不能为空");
        if (!Constants.MenuConstants.TYPE_BUTTON.equals(sysMenu.getMenuType())) {
            boolean isExternalWithUrlInPath = Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getIsFrame()) && StringUtils.isHttp(sysMenu.getPath());
            boolean isExternalWithUrlInRouteName = Constants.MenuConstants.IS_EXTERNAL_LINK.equals(sysMenu.getIsFrame()) && StringUtils.isHttp(sysMenu.getRouteName()) && !StringUtils.isHttp(sysMenu.getPath());

            if (!isExternalWithUrlInPath && !isExternalWithUrlInRouteName && StringUtils.isBlank(sysMenu.getPath())) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "路由路径不能为空（按钮、内嵌iframe或外部跳转链接除外）");
            }
        }
    }

    /**
     * 检查父菜单ID是否合法
     *
     * @param menu 菜单对象
     */
    private void checkParentIdIsLegal(SysMenu menu) {
        if (menu.getParentId() == null) menu.setParentId(0L);
        if (menu.getParentId() != 0L) {
            if (getById(menu.getParentId()) == null) {
                log.error("指定的父菜单ID {} 不存在", menu.getParentId());
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "指定的父菜单不存在");
            }
        }
    }

    /**
     * 获取角色权限信息
     *
     * @param roleId 角色ID
     * @return 角色权限信息
     */
    @Override
    public SysRolePermVo getRolePermByRoleId(Long roleId) {
        SysRole role = Optional.ofNullable(sysRoleService.getById(roleId))
                .orElseThrow(() -> {
                    log.error("获取角色权限失败：角色ID {} 不存在", roleId);
                    return new IllegalArgumentException("角色不存在，ID：" + roleId);
                });
        List<SysMenu> allMenus = list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort));
        List<SysMenuTreeList> menuTreeList = buildMenuTreeList(allMenus);
        List<Long> selectedMenuIds = getRolePermSelectedByRoleId(roleId);
        return new SysRolePermVo(roleId, role.getRoleName(), role.getRoleKey(), menuTreeList, selectedMenuIds);
    }


    /**
     * 修改角色前线信息
     *
     * @param request 请求参数
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRolePermission(SysUpdateRolePermissionRequest request) {
        Long roleId = request.getRoleId();
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            log.error("更新角色权限失败：角色ID {} 不存在", roleId);
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "角色不存在");
        }
        if (SysRolesConstant.SUPER_ADMIN.equals(sysRole.getRoleKey())) {
            log.warn("试图修改超级管理员 ({}) 的权限，操作被禁止", sysRole.getRoleKey());
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "超级管理员角色不允许修改");
        }
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        log.debug("已删除角色ID {} 的原有菜单权限", roleId);
        if (request.getSelectedMenuId() != null && !request.getSelectedMenuId().isEmpty()) {
            List<SysRoleMenu> roleMenusToInsert = request.getSelectedMenuId().stream()
                    .map(menuId -> {
                        SysRoleMenu sysRoleMenu = new SysRoleMenu();
                        sysRoleMenu.setRoleId(roleId);
                        sysRoleMenu.setMenuId(menuId);
                        return sysRoleMenu;
                    }).toList();
            log.debug("为角色ID {} 批量插入 {} 条新菜单权限", roleId, roleMenusToInsert.size());
            return sysRoleMenuService.saveBatch(roleMenusToInsert);
        }
        return true;
    }

    /**
     * 获取菜单列表
     *
     * @param request 请求参数
     * @return 菜单列表
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
                .sorted(Comparator.comparing(SysMenuListVo::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private SysMenuListVo convertToSysMenuListVoRecursive(SysMenu menu, Map<Long, List<SysMenu>> parentChildrenMap) {
        SysMenuListVo vo = sysMenuConverter.toMenuListVo(menu);
        List<SysMenu> childrenEntities = parentChildrenMap.getOrDefault(menu.getMenuId(), Collections.emptyList());
        // childrenEntities 已经从map获取时排序过了

        if (!childrenEntities.isEmpty()) {
            vo.setChildren(childrenEntities.stream()
                    .map(child -> convertToSysMenuListVoRecursive(child, parentChildrenMap))
                    .collect(Collectors.toList()));
        }
        return vo;
    }


    @Override
    public List<Long> getRolePermSelectedByRoleId(Long roleId) {
        if (roleId == null) {
            log.warn("获取角色已选菜单ID列表时，角色ID为空");
            return Collections.emptyList();
        }
        Set<String> roleKeys = sysRoleService.getRoleSetByRoleId(roleId);
        if (roleKeys.contains(SysRolesConstant.SUPER_ADMIN)) {
            log.info("角色ID {} (标识: {}) 是超级管理员，返回所有菜单ID", roleId, roleKeys);
            return list().stream().map(SysMenu::getMenuId).distinct().collect(Collectors.toList());
        }
        return roleMenuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 构建前端所需的路由列表
     *
     * @param menus 经过权限过滤和排序的菜单列表
     * @return 树形结构的路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }

        // 预处理：过滤按钮类型，并确保一个基础排序（父节点在前，同级按sort）
        List<SysMenu> processableMenus = menus.stream()
                .filter(menu -> !Constants.MenuConstants.TYPE_BUTTON.equals(menu.getMenuType()))
                .sorted(Comparator.comparing(SysMenu::getParentId, Comparator.nullsFirst(Comparator.naturalOrder())) // 父节点优先
                        .thenComparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder()))) // 同级按sort
                .toList();

        if (processableMenus.isEmpty()) {
            return Collections.emptyList();
        }

        // 构建父ID到其子菜单列表的映射，子菜单列表也进行排序
        Map<Long, List<SysMenu>> parentChildrenMap = processableMenus.stream()
                .filter(menu -> menu.getParentId() != null)
                .collect(Collectors.groupingBy(SysMenu::getParentId));

        parentChildrenMap.forEach((parentId, childrenList) ->
                childrenList.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
        );

        // 从根节点 (parentId 为 0) 开始构建路由树
        return processableMenus.stream()
                .filter(menu -> menu.getParentId() != null && menu.getParentId() == 0L)
                .map(menu -> convertToRouterVoRecursive(menu, parentChildrenMap, "")) // 初始父路径为空字符串
                .filter(Objects::nonNull) // 过滤掉转换过程中可能产生的null
                // 顶层路由列表的顺序由 processableMenus 中根节点的顺序（已排序）决定
                .collect(Collectors.toList());
    }

    /**
     * 递归地将SysMenu实体转换为前端RouterVo对象。
     * 此方法现在能区分“内嵌iframe”和“外部链接跳转”两种模式。
     *
     * @param menu              当前要转换的SysMenu实体。
     * @param parentChildrenMap 预先处理好的父ID到其子菜单列表的映射。子列表应已按sort排序。
     * @param parentPath        当前菜单的父级路由的完整内部路径。
     * @return 构建完成的RouterVo对象，或null（如果菜单不应生成路由）。
     */
    private RouterVo convertToRouterVoRecursive(SysMenu menu, Map<Long, List<SysMenu>> parentChildrenMap, String parentPath) {
        if (menu == null) return null;

        RouterVo router = new RouterVo();
        MetaVo meta = new MetaVo(); // 创建新的MetaVo实例

        // --- 1. 基础Meta信息填充 ---
        meta.setTitle(menu.getMenuName());
        meta.setIcon(menu.getIcon());
        // 根据visible状态决定是否在菜单中显示链接 (showLink通常与visible反相关或直接对应)
        meta.setShowLink(Constants.MenuConstants.VISIBLE.equals(menu.getVisible())); // '0' 为显示
        // 权限标识
        if (StrUtil.isNotBlank(menu.getPermission())) {
            meta.setAuths(new String[]{menu.getPermission()});
        } else if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            meta.setAuths(new String[]{""}); // 目录可设置空权限数组
        }

        String routerPathValue;
        String routerNameValue = menu.getRouteName(); // 默认使用数据库中的routeName
        String componentPathValue;
        // 标记是否为“外部链接跳转”模式

        // --- 2. 根据菜单类型和外链设置，处理 path, name, component, meta.frameSrc, meta.keepAlive ---
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame())) {
            // 2.1 内嵌Iframe模式: isFrame=1, 且 menu.path 本身是 HTTP(S) URL
            if (StringUtils.isHttp(menu.getPath())) {
                meta.setFrameSrc(menu.getPath()); // path作为iframe的源
                meta.setKeepAlive(true);          // iframe通常建议缓存

                // router.path 应为内部路径，可基于routeName或ID生成
                String segment = StrUtil.isNotBlank(menu.getRouteName()) ? menu.getRouteName() : "iframe-" + menu.getMenuId();
                routerPathValue = buildInternalPathStructure(parentPath, segment);
                // routerNameValue (menu.getRouteName()) 此时应为内部路由名
                componentPathValue = getComponentPath(menu); // 获取组件（可能为null或iframe布局）
            }
            // 2.2 外部链接跳转模式: isFrame=1, menu.path 是内部路径段, menu.routeName 是 HTTP(S) URL
            else if (StringUtils.isHttp(menu.getRouteName()) && !StringUtils.isHttp(menu.getPath())) {
                routerPathValue = buildInternalPathStructure(parentPath, menu.getPath()); // path使用内部路径段
                routerNameValue = menu.getRouteName(); // name 就是外部URL
                // meta.frameSrc 保持 null
                meta.setKeepAlive(Constants.MenuConstants.CACHE_ENABLED.equals(menu.getIsCache())); // 或默认false
                componentPathValue = null; // 外链跳转无组件
            }
            // 2.3 isFrame=1 但配置不符合上述两种模式
            else {
                log.warn("菜单ID {} (名称: '{}') 标记为 isFrame=1，但其 path ('{}') 和 routeName ('{}') 未能匹配内嵌iframe或外部链接跳转的配置。将尝试按内部菜单处理。",
                        menu.getMenuId(), menu.getMenuName(), menu.getPath(), menu.getRouteName());
                routerPathValue = buildInternalPathStructure(parentPath, menu.getPath());
                meta.setKeepAlive(Constants.MenuConstants.CACHE_ENABLED.equals(menu.getIsCache()));
                componentPathValue = getComponentPath(menu);
            }
        }
        // 2.4 普通内部菜单或目录 (非 isFrame=1)
        else {
            routerPathValue = buildInternalPathStructure(parentPath, menu.getPath());
            componentPathValue = getComponentPath(menu);

            if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType()) && StrUtil.isNotBlank(componentPathValue)) {
                // 对TYPE_MENU且有组件的，添加 /index 后缀 (排除Layout和ParentView自身)
                if (!Constants.MenuConstants.LAYOUT.equals(componentPathValue) &&
                        !Constants.MenuConstants.PARENT_VIEW.equals(componentPathValue)) {

                    // 确保 pathSegment 不是空的，或者父路径是根，才添加/index
                    String pathSegmentForIndexCheck = menu.getPath() == null ? "" : menu.getPath().trim();
                    if (!pathSegmentForIndexCheck.endsWith("index") && // 避免 menu/index/index
                            (StrUtil.isNotBlank(pathSegmentForIndexCheck) || "/".equals(routerPathValue) || routerPathValue.endsWith("/")) // segment非空，或者当前路径是根/或以/结尾的父目录
                    ) {
                        routerPathValue = routerPathValue.endsWith("/") ? (routerPathValue + "index") : (routerPathValue + "/index");
                    }
                }
                meta.setKeepAlive(Constants.MenuConstants.CACHE_ENABLED.equals(menu.getIsCache()));
            } else { // 目录或其他
                meta.setKeepAlive(false);
            }
        }

        // --- 3. 设置RouterVo的核心属性 ---
        router.setPath(routerPathValue.replaceAll("//+", "/")); // 清理路径
        if (StrUtil.isNotBlank(routerNameValue)) { // 确保routeName不为空才设置
            router.setName(routerNameValue);
        }
        router.setComponent(componentPathValue);
        router.setMeta(meta); // 设置已填充的meta
        router.setQuery(menu.getQuery()); // 设置路由查询参数
        router.setHidden(Constants.MenuConstants.HIDDEN.equals(menu.getVisible())); // 设置是否隐藏

        // --- 4. 处理子路由 ---
        List<SysMenu> childrenEntities = parentChildrenMap.getOrDefault(menu.getMenuId(), Collections.emptyList());
        // childrenEntities 列表已经过排序 (在buildMenus方法中对map的值进行了排序)

        if (!childrenEntities.isEmpty()) {
            List<RouterVo> childrenRouters = childrenEntities.stream()
                    .map(childMenu -> convertToRouterVoRecursive(childMenu, parentChildrenMap, router.getPath())) // 传递当前构建的router.path作为子项的parentPath
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!childrenRouters.isEmpty()) {
                router.setChildren(childrenRouters);
                // 如果一个路由有子路由，通常它的 alwaysShow 应为 true，以便在侧边栏中显示为可展开的父项
                router.setAlwaysShow(true);
            } else {
                // 如果筛选后没有可显示的子路由，父目录的 alwaysShow 可以为 false （除非它本身需要一直显示）
                router.setAlwaysShow(false); // 或根据 menu.getAlwaysShow() 数据库字段（如果存在）
            }
        } else {
            router.setAlwaysShow(false); // 没有子菜单，alwaysShow 为 false
        }
        return router;
    }

    /**
     * 构建内部路由的路径结构。
     *
     * @param parentPath  父级完整内部路径。
     * @param pathSegment 当前菜单的路径段 (来自 menu.getPath())。
     * @return 拼接并规范化后的完整内部路径。
     */
    private String buildInternalPathStructure(String parentPath, String pathSegment) {
        String segment = (pathSegment == null) ? "" : pathSegment.trim();
        String fullPath;

        // 如果 pathSegment 本身是绝对路径 (以 / 开头)，则直接使用它，忽略 parentPath
        if (StrUtil.isNotBlank(segment) && segment.startsWith("/")) {
            fullPath = segment;
        }
        // 否则，它是相对路径，需要与 parentPath 拼接
        else {
            if (StrUtil.isBlank(parentPath) || "/".equals(parentPath)) { // 父路径是根
                fullPath = "/" + segment;
            } else { // 父路径非根
                String formattedParent = parentPath.endsWith("/") ? parentPath : parentPath + "/";
                fullPath = formattedParent + segment;
            }
        }

        fullPath = fullPath.replaceAll("//+", "/"); // 清理连续的斜杠
        if (fullPath.length() > 1 && fullPath.endsWith("/")) { // 移除末尾斜杠 (除非路径本身就是 "/")
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        if (StrUtil.isBlank(fullPath)) { // 如果结果为空（例如父路径和段都为空），则默认为根路径
            fullPath = "/";
        }
        return fullPath;
    }

    /**
     * 根据菜单配置获取其对应的前端组件路径。
     *
     * @param menu SysMenu 实体
     * @return 组件路径字符串，或特定布局组件名（如"Layout", "ParentView"），或null。
     */
    private String getComponentPath(SysMenu menu) {
        if (menu == null) return null;

        // 情况1: 外链/Iframe (isFrame = 1 且 path 是 http/https - 这是内嵌iframe场景)
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame()) && StringUtils.isHttp(menu.getPath())) {
            // return Constants.MenuConstants.IFRAME_LAYOUT; // 如果定义了通用的iframe布局组件
            return null; // 通常前端根据 meta.frameSrc 渲染，不需要后端指定组件
        }
        // 情况2: 外链跳转 (isFrame=1, routeName是HTTP URL, path是内部段) -> 无组件
        if (Constants.MenuConstants.IS_EXTERNAL_LINK.equals(menu.getIsFrame()) &&
                StringUtils.isHttp(menu.getRouteName()) &&
                !StringUtils.isHttp(menu.getPath())) {
            return null;
        }

        // 情况3: 目录类型 (TYPE_DIRECTORY)
        if (Constants.MenuConstants.TYPE_DIRECTORY.equals(menu.getMenuType())) {
            // 如果数据库中 component 字段已有值 (例如在添加/修改时已明确设置如 "Layout")，则优先使用
            if (StrUtil.isNotBlank(menu.getComponent())) {
                return menu.getComponent();
            }
            // 否则，按层级决定：顶级目录用LAYOUT，子目录用PARENT_VIEW
            return menu.getParentId() == 0L ? Constants.MenuConstants.LAYOUT : Constants.MenuConstants.PARENT_VIEW;
        }

        // 情况4: 菜单类型 (TYPE_MENU) 且非外链 (isFrame不为1，或path不是http)
        if (Constants.MenuConstants.TYPE_MENU.equals(menu.getMenuType())) {
            return menu.getComponent(); // 直接返回数据库中配置的组件路径
        }

        return null; // 按钮或其他未匹配情况
    }

    /**
     * 构建菜单选项列表
     *
     * @param allMenus 所有菜单列表
     * @return 菜单选项列表
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
     * 递归获取子菜单列表，构建树形结构
     *
     * @param allMenus 所有菜单列表
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    private List<SysMenuTreeList> getChildrenAsMenuTreeList(List<SysMenu> allMenus, Long parentId) {
        if (parentId == null || allMenus == null || allMenus.isEmpty()) return Collections.emptyList();
        return allMenus.stream()
                .filter(menu -> parentId.equals(menu.getParentId()))
                .sorted(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(menu -> {
                    SysMenuTreeList treeNode = sysMenuConverter.toMenuTreeList(menu); // 假设转换器处理基础字段
                    List<SysMenuTreeList> grandChildren = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!grandChildren.isEmpty()) treeNode.setChildren(grandChildren);
                    return treeNode;
                }).collect(Collectors.toList());
    }

    /**
     * 构建菜单树形结构列表
     *
     * @param allMenus 所有菜单列表
     * @return 树形结构的菜单列表
     */
    private List<SysMenuTreeList> buildMenuTreeList(List<SysMenu> allMenus) {
        if (allMenus == null || allMenus.isEmpty()) return Collections.emptyList();
        return allMenus.stream() // 使用已排序的列表
                .filter(menu -> menu.getParentId() == 0L) // 筛选根节点
                .map(menu -> {
                    SysMenuTreeList treeNode = sysMenuConverter.toMenuTreeList(menu);
                    List<SysMenuTreeList> children = getChildrenAsMenuTreeList(allMenus, menu.getMenuId());
                    if (!children.isEmpty()) treeNode.setChildren(children);
                    return treeNode;
                })
                .collect(Collectors.toList());
    }
}
