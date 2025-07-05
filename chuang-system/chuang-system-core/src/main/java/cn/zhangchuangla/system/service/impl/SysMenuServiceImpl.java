package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuQueryRequest;
import cn.zhangchuangla.system.model.request.menu.SysMenuUpdateRequest;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Chuang
 * <p>
 * created on 2025/7/5 12:47
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {


    /**
     * 获取菜单列表
     *
     * @param request 查询参数
     * @return 菜单列表
     */
    @Override
    public Page<SysMenu> listMenu(SysMenuQueryRequest request) {
        return null;
    }


    /**
     * 根据菜单ID查询菜单信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu getMenuById(Long menuId) {
        return null;
    }

    /**
     * 新增菜单
     *
     * @param request 菜单信息
     * @return 是否成功
     */
    @Override
    public boolean addMenu(SysMenuAddRequest request) {
        return false;
    }

    /**
     * 修改菜单
     *
     * @param request 菜单信息
     * @return 是否成功
     */
    @Override
    public boolean updateMenu(SysMenuUpdateRequest request) {
        return false;
    }

    /**
     * 删除菜单
     *
     * @param menuId 菜单ID
     * @return 是否成功
     */
    @Override
    public boolean deleteMenu(Long menuId) {
        return false;
    }

    /**
     * 根据角色名查询菜单列表
     *
     * @param roleName 角色名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> getSysMenuListByRoleName(Set<String> roleName) {
        return List.of();
    }

    /**
     * 构建菜单路由
     *
     * @param sysMenu 菜单
     * @return 路由
     */
    @Override
    public RouterVo buildRouter(SysMenu sysMenu) {
        return null;
    }

    /**
     * 获取菜单选项
     *
     * @return 菜单选项
     */
    @Override
    public List<Option<String>> getMenuOptions() {
        return List.of();
    }
}
