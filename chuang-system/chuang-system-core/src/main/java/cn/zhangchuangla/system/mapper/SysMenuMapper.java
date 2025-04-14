package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 菜单Mapper接口
 *
 * @author zhangchuang
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> getMenuByUserId(Long userId);
}
