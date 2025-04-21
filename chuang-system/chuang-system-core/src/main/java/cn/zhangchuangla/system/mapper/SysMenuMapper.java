package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author zhangchuang
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 获取菜单路由列表
     *
     * @param roleCodes 角色编码集合
     */
    //perfect 待完善
    List<SysMenu> getMenusByRoleCodes(Set<String> roleCodes);

    /**
     * 根据角色名称获取权限信息
     *
     * @param roleName 角色名称(此处所指为角色的标识符，例如“admin”“user”等)
     * @return 权限信息
     */
    List<SysMenu> getPermissionsByRoleName(@Param("roleKey") String roleName);
}




