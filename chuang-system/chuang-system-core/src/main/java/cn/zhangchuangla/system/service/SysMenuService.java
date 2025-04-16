package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.MenuAddRequest;
import cn.zhangchuangla.system.model.request.menu.MenuQueryRequest;
import cn.zhangchuangla.system.model.vo.menu.MenuVo;
import cn.zhangchuangla.system.model.vo.menu.RouteVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author zhangchuang
 */
public interface SysMenuService extends IService<SysMenu> {


    /**
     * 获取菜单表格列表
     */
    List<MenuVo> listMenus(MenuQueryRequest queryParams);

    /**
     * 获取菜单下拉列表
     *
     * @param onlyParent 是否只查询父级菜单
     */
    List<Option<Long>> listMenuOptions(boolean onlyParent);

    /**
     * 新增菜单
     *
     * @param menuAddRequest 菜单表单对象
     */
    boolean saveMenu(MenuAddRequest menuAddRequest);

    /**
     * 获取路由列表
     */
    List<RouteVo> getCurrentUserRoutes();

    /**
     * 修改菜单显示状态
     *
     * @param menuId  菜单ID
     * @param visible 是否显示(1-显示 0-隐藏)
     */
    boolean updateMenuVisible(Long menuId, Integer visible);

    /**
     * 获取菜单表单数据
     *
     * @param id 菜单ID
     */
    MenuAddRequest getMenuForm(Long id);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     */
    boolean deleteMenu(Long id);

}
