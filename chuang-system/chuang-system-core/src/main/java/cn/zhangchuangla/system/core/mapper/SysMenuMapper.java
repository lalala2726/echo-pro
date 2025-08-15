package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 菜单表数据访问层
 *
 * @author Chuang
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {


    /**
     * 获取菜单列表
     *
     * @param roleNames 角色名称集合
     * @return 菜单列表
     */
    List<SysMenu> listSysMenuByRoleName(@Param("roleNames") Set<String> roleNames);


    /**
     * 获取权限列表
     *
     * @param roleSet 角色标识符集合
     * @return 权限列表
     */
    Set<String> getPermissionByRole(@Param("roleSet") Set<String> roleSet);

}




