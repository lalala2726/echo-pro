package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.system.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.model.request.user.AddUserRequest;
import cn.zhangchuangla.system.model.request.user.UpdateUserRequest;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.model.vo.user.UserListVo;
import cn.zhangchuangla.system.model.vo.user.UserProfileVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 用户对象转换器
 *
 * @author Chuang
 * <p>
 * created on 2025/4/16 19:55
 */
@Mapper(componentModel = "spring")
public interface SysUserConverter {

    /**
     * 转换用户对象为用户简介视图对象
     *
     * @param user 用户对象
     * @return 用户简介视图对象
     */
    @Mapping(target = "deptName", ignore = true)
    @Mapping(source = "createTime", target = "createTime")
    UserProfileVo toUserProfileVo(SysUser user);

    /**
     * 将用户添加请求转换为实体类
     *
     * @param request 用户添加请求
     * @return 用户实体类
     */
    SysUser toEntity(AddUserRequest request);

    /**
     * 将用户更新请求转换为实体类
     *
     * @param request 用户更新请求
     * @return 用户实体类
     */
    SysUser toEntity(UpdateUserRequest request);

    /**
     * 将用户部门数据传输对象转换为用户列表视图对象
     *
     * @param item 用户部门数据传输对象
     * @return 用户列表视图对象
     */
    UserListVo toUserListVo(SysUserDeptDto item);

    /**
     * 将用户对象转换为用户信息视图对象
     *
     * @param sysUser 用户对象
     * @return 用户信息视图对象
     */
    UserInfoVo toUserInfoVo(SysUser sysUser);
}
