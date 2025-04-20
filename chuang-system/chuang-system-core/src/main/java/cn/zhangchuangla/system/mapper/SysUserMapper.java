package cn.zhangchuangla.system.mapper;

import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.system.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.model.request.user.UserListRequest;
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
     * @param page            枫叶对象
     * @param userListRequest 查询参数
     * @return 返回分页结果
     */
    Page<SysUserDeptDto> listUser(Page<SysUserDeptDto> page, @Param("request") UserListRequest userListRequest);

    /**
     * 查询指定用户以外的指定邮箱数量
     *
     * @param email  邮箱
     * @param userId 需要排除的用户ID
     * @return 返回数量
     */
    Integer countOtherUserEmails(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 查询指定用户以外的指定手机号数量
     *
     * @param phone  手机号
     * @param userId 需要排除的用户ID
     * @return 返回数量
     */
    Integer isPhoneExist(@Param("phone") String phone, @Param("userId") Long userId);


    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 返回用户信息
     */
    SysUser getUserInfoByUsername(@Param("username") String username);
}




