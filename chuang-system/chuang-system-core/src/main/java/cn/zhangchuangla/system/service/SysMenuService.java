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
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getMenuListByUserId(Long userId);


    /**
     * 构造前端需要的路由界面
     *
     * @param menus 菜单列表
     * @return 返回前端需要的路由界面
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);
}
