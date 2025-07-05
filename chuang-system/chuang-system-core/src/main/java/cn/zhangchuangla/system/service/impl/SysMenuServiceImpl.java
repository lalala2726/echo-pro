package cn.zhangchuangla.system.service.impl;

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


    @Override
    public Page<SysMenu> listMenu(SysMenuQueryRequest request) {
        return null;
    }

    @Override
    public SysMenu getMenuById(Long menuId) {
        return null;
    }

    @Override
    public boolean addMenu(SysMenuAddRequest request) {
        return false;
    }

    @Override
    public boolean updateMenu(SysMenuUpdateRequest request) {
        return false;
    }

    @Override
    public boolean deleteMenu(Long menuId) {
        return false;
    }

    @Override
    public List<SysMenu> getSysMenuListByRoleName(Set<String> roleName) {
        return List.of();
    }

    @Override
    public RouterVo buildRouter(SysMenu sysMenu) {
        return null;
    }
}
