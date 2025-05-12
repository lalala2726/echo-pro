package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysRoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色和菜单关联表数据访问层接口
 *
 * @author Chuang
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 检查菜单是否已分配给角色
     *
     * @param menuId 菜单ID
     * @return 分配数量
     */
    int countRoleMenuByMenuId(Long menuId);

    /**
     * 根据角色ID删除角色和菜单关联
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 批量新增角色菜单关联信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    int batchInsertRoleMenu(List<SysRoleMenu> roleMenuList);

    /**
     * 检查菜单是否已分配给角色
     *
     * @param menuId 菜单ID
     * @return 分配数量
     */
    int checkMenuExistRole(Long menuId);

    /**
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);
}




