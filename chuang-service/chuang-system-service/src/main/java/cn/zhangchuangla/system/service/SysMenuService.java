package cn.zhangchuangla.system.service;

import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
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
}
