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
     * 根据角色ID查询菜单ID列表
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuListByRoleId(@Param("roleId") Long roleId);
}




