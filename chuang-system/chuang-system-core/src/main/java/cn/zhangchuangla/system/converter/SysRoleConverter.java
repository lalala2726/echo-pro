package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.system.model.entity.SysPost;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleUpdateRequest;
import cn.zhangchuangla.system.model.vo.permission.SysRoleVo;
import cn.zhangchuangla.system.model.vo.post.SysPostVo;
import org.mapstruct.Mapper;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/16 20:32
 */
@Mapper(componentModel = "spring")
public interface SysRoleConverter {

    /**
     * 将角色添加请求转换为实体类
     *
     * @param roleAddRequest 角色添加请求
     * @return 角色实体类
     */
    SysRole toEntity(SysRoleAddRequest roleAddRequest);

    /**
     * 将角色更新请求转换为实体类
     *
     * @param roleAddRequest 角色更新请求
     * @return 角色实体类
     */
    SysRole toEntity(SysRoleUpdateRequest roleAddRequest);

    /**
     * 将岗位实体类转换为岗位视图对象
     *
     * @param post 岗位实体类
     * @return 岗位视图对象
     */
    SysPostVo toSysPostVo(SysPost post);

    /**
     * 将角色实体类转换为角色视图对象
     *
     * @param sysRole 角色实体类
     * @return 角色视图对象
     */
    SysRoleVo toSysRoleVo(SysRole sysRole);
}
