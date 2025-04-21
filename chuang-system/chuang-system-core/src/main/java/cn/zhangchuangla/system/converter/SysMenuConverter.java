package cn.zhangchuangla.system.converter;


import cn.zhangchuangla.system.model.entity.SysMenu;
import cn.zhangchuangla.system.model.request.menu.MenuAddRequest;
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


    MenuVo toVo(SysMenu entity);

    @Mapping(target = "params", ignore = true)
    MenuAddRequest toAddRequest(SysMenu entity);

    //忽略params属性
    @Mapping(target = "params", ignore = true)
    SysMenu toEntity(MenuAddRequest menuAddRequest);

}
