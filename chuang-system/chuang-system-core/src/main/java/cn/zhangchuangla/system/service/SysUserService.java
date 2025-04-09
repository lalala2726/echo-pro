package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.system.model.request.user.AddUserRequest;
import cn.zhangchuangla.system.model.request.user.UpdateUserRequest;
import cn.zhangchuangla.system.model.request.user.UserRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户列表
     *
     * @param request 请求参数
     * @return 分页数据
     */
    Page<SysUser> UserList(UserRequest request);


    /**
     * 新增用户
     *
     * @param request 请求参数
     * @return 用户主键
     */
    Long addUserInfo(AddUserRequest request);

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return true存在 false不存在
     */
    boolean isUsernameExist(String username);

    /**
     * 判断邮箱是否存在
     *
     * @param email 邮箱
     * @return true存在 false不存在
     */
    boolean isEmailExist(String email);

    /**
     * 判断邮箱是否存在
     *
     * @param email  邮箱
     * @param userId 排除的用户ID
     * @return 返回邮箱是否已经存在
     */
    boolean isEmailExist(String email, Long userId);

    /**
     * 判断手机号是否存在
     *
     * @param phone 手机号
     * @return true存在 false不存在
     */
    boolean isPhoneExist(String phone);

    /**
     * 判断手机号是否存在
     *
     * @param phone  手机号
     * @param userId 排除的用户ID
     * @return 返回邮箱是否已经存在
     */
    boolean isPhoneExist(String phone, Long userId);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回用户信息
     */
    SysUser getSysUserByUsername(String username);

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    SysUser getUserInfoByUserId(Long userId);

    /**
     * 根据用户ID删除用户
     *
     * @param ids 用户ID集合
     */
    void deleteUserById(List<Long> ids);

    /**
     * 修改用户信息
     *
     * @param request 请求参数
     */
    void updateUserInfoById(UpdateUserRequest request);


    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回用户信息
     */
    SysUser getUserInfoByUsername(String username);

    /**
     * 判断用户是否允许修改
     *
     * @param userId 用户ID
     */
    void isAllowUpdate(@NotBlank(message = "用户ID不能为空") Long userId);
}
