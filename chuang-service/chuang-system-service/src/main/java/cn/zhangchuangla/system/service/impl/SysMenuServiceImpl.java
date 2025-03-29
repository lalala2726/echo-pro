package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.system.mapper.SysMenuMapper;
import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import cn.zhangchuangla.system.service.SysMenuService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author zhangchuang
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    /**
     * 菜单列表
     *
     * @param sysMenuListRequest 请求参数
     * @return 返回分页列表
     */
    @Override
    public Page<SysMenu> listMenu(SysMenuListRequest sysMenuListRequest) {
        Page<SysMenu> sysMenuPage = new Page<>(sysMenuListRequest.getPageNum(), sysMenuListRequest.getPageSize());
        return sysMenuMapper.listMenu(sysMenuPage, sysMenuListRequest);
    }
}




