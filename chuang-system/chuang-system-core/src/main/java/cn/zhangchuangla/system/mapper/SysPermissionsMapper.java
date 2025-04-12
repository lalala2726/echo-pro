package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.request.permissions.SysPermissionsListRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 分页获取权限列表
     *
     * @param page    分页对象
     * @param request 请求对象
     * @return 权限列表
     */
    Page<SysPermissions> listPermissions(Page<SysPermissions> page, @Param("request") SysPermissionsListRequest request);
}




