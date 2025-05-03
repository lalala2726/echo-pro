package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.system.model.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Chuang
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {


    /**
     * 根据用户id获取角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<SysRole> getRoleListByUserId(Long userId);


}




