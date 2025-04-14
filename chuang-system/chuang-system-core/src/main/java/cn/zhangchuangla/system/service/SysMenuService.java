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
     * 构建菜单
     *
     * @param menus 菜单列表
     * @return 菜单树形结构
     */
    List<RouterVo> buildMenu(List<SysMenu> menus);

    /**
     * 根据用户ID获取菜单
     *
     * @param userId 用户ID
     * @return 返回用户的菜单
     */
    List<SysMenu> getMenuByUserId(Long userId);
}
