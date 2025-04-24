package cn.zhangchuangla.system.converter;


import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.MenuForm;
import cn.zhangchuangla.system.model.vo.menu.MenuVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 菜单对象转换器
 *
 * @author Ray Hao
 * @since 2024/5/26
 */
@Mapper(componentModel = "spring")
public interface SysMenuConverter {


    @Mapping(source = "permission", target = "permission")
    MenuVo toVo(SysMenu entity);

    //忽略params属性
    @Mapping(target = "params", ignore = true)
    SysMenu toEntity(MenuForm menuForm);


    @Mapping(target = "params", ignore = true)
    MenuForm toForm(SysMenu entity);
}
