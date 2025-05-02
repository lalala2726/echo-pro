package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

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
}




