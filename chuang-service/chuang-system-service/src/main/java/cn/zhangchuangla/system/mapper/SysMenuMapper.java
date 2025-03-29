package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.SysMenuListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 分页查询菜单权限
     *
     * @param sysMenuPage        分页对象
     * @param sysMenuListRequest 查询参数
     * @return 分页数据
     */
    Page<SysMenu> listMenu(Page<SysMenu> sysMenuPage, @Param("request") SysMenuListRequest sysMenuListRequest);
}




