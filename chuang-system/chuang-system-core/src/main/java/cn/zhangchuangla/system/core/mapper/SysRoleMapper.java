package cn.zhangchuangla.system.core.mapper;

import cn.zhangchuangla.system.core.model.entity.SysRole;
import cn.zhangchuangla.system.core.model.request.role.SysRoleQueryRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<SysRole> getRoleListByUserId(@Param("userId") Long userId);


    /**
     * 角色列表
     *
     * @param page    分页参数
     * @param request 查询参数
     * @return 角色列表
     */
    Page<SysRole> roleList(@Param("page") Page<SysRole> page, @Param("request") SysRoleQueryRequest request);
}




