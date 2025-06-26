package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.core.security.model.SysUser;
import cn.zhangchuangla.system.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.model.request.user.SysUserAddRequest;
import cn.zhangchuangla.system.model.request.user.SysUserQueryRequest;
import cn.zhangchuangla.system.model.request.user.SysUserUpdateRequest;
import cn.zhangchuangla.system.model.request.user.profile.UpdatePasswordRequest;
import cn.zhangchuangla.system.model.request.user.profile.UserProfileUpdateRequest;
import cn.zhangchuangla.system.model.vo.user.profile.UserProfileVo;
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
    Page<SysUserDeptDto> listUser(SysUserQueryRequest request);


    /**
     * 新增用户
     *
     * @param request 请求参数
     * @return 用户主键
     */
    Long addUserInfo(SysUserAddRequest request);

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
    boolean updateUserInfoById(SysUserUpdateRequest request);


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

    /**
     * 获取用户个人中心信息
     *
     * @return 用户个人中心信息
     */
    UserProfileVo getUserProfile();

    /**
     * 根据用户ID重置密码
     *
     * @param password 新密码
     * @param userId   用户ID
     * @return 操作结果
     */
    boolean resetPassword(String password, Long userId);

    /**
     * 修改用户密码
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updatePassword(UpdatePasswordRequest request);


    /**
     * 修改用户个人资料
     *
     * @param request 请求参数
     * @return 操作结果
     */
    boolean updateUserProfile(UserProfileUpdateRequest request);
}
