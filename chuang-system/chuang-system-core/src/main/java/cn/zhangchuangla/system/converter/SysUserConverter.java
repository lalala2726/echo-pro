package cn.zhangchuangla.system.converter;

import cn.zhangchuangla.common.core.security.model.SysUser;
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

}
