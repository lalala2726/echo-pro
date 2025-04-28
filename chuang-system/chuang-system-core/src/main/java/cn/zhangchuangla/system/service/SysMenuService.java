package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author zhangchuang
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 更新角色权限
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID数组
     */
    boolean updateRoleMenu(Long roleId, Long[] menuIds);

    /**
     * 获取角色菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> listMenu(Long userId);


    /**
     * 构建路由信息
     *
     * @param sysMenus 菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenus(List<SysMenu> sysMenus);

}
