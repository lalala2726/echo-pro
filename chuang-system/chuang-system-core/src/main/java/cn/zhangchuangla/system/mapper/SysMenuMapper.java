package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
     * @param roleName 角色名称
     * @return 菜单列表
     */
    List<SysMenu> listSysMenuByRoleName(Set<String> roleName);
}




