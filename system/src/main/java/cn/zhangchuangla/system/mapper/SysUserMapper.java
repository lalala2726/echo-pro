package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.system.model.request.user.UserRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchuang
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 分页查询用户信息
     *
     * @param page    枫叶对象
     * @param request 查询参数
     * @return 返回分页结果
     */
    Page<SysUser> UserList(Page<SysUser> page, @Param("user") UserRequest request);

}




