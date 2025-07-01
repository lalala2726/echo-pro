package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysMenu;
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
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getMenuListByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<SysMenu> selectMenuListByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 角色ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(@Param("userId") Long userId);

    /**
     * 查询菜单列表
     *
     * @param menu 查询参数
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleSet 角色标签
     * @return 权限列表
     */
    List<SysMenu> getUserPermissionListByRole(Set<String> roleSet);
}




