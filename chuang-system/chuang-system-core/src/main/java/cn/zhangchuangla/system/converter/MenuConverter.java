package cn.zhangchuangla.system.converter;


import cn.zhangchuangla.system.model.entity.Menu;
import cn.zhangchuangla.system.model.request.menu.MenuForm;
import cn.zhangchuangla.system.model.vo.menu.MenuVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 菜单对象转换器
 *
 * @author Ray Hao
 * @since 2024/5/26
 */
@Mapper(componentModel = "spring")
public interface MenuConverter {


    MenuVO toVo(Menu entity);

    @Mapping(target = "params", ignore = true)
    MenuForm toForm(Menu entity);

    //忽略params属性
    @Mapping(target = "params", ignore = true)
    Menu toEntity(MenuForm menuForm);


}
