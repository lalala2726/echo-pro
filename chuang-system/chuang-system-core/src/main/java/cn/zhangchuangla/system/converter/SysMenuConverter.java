package cn.zhangchuangla.system.converter;


import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.vo.menu.SysMenuListVo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 菜单对象转换器
 *
 * @author Ray Hao
 * @since 2024/5/26
 */
@Mapper(componentModel = "spring")
public interface SysMenuConverter {


    /**
     * 将菜单列表转换为菜单列表视图对象
     *
     * @param sysMenuList 菜单列表
     * @return 菜单列表视图对象
     */
    List<SysMenuListVo> toSysMenuListVo(List<SysMenu> sysMenuList);
}
