package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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

}




