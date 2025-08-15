package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Chuang
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID获取用户角色
     *
     * @param userId 用户ID
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID批量删除用户角色
     *
     * @param userId 用户ID
     * @return 删除数量
     */
    int deleteUserRoleByUserIds(@Param("userIds") List<Long> userId);
}




