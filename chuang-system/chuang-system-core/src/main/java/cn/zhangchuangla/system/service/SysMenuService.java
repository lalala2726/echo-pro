package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author zhangchuang
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单列表
     *
     * @param sysMenuListRequest 请求参数
     * @return 返回分页列表
     */
    Page<SysMenu> listMenu(SysMenuListRequest sysMenuListRequest);


    /**
     * 根据用户id获取菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getMenuUserId(Long userId);

    /**
     * 构建菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenu(List<SysMenu> menus);

    void roleMenuTree(Long roleId);
}
