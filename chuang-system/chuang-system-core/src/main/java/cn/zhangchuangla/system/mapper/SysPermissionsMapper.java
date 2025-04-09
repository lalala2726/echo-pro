package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhangchuang
 */
@Mapper
public interface SysPermissionsMapper extends BaseMapper<SysPermissions> {


    /**
     * 根据角色名获取权限列表
     *
     * @param roleName 角色名
     * @return 获取权限列表
     */
    List<SysPermissions> getPermissionsListByRoleName(@Param("roleName") String roleName);

    /**
     * 根据用户id获取权限列表
     *
     * @param userId 用户id
     * @return 获取权限列表
     */
    List<SysPermissions> getPermissionsByUserId(@Param("userId") Long userId);
}




