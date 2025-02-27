package cn.zhangchuangla.system.service;

import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.system.model.request.AddUserRequest;
import cn.zhangchuangla.system.model.request.UserRequest;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * 判断手机号是否存在
     *
     * @param phone 手机号
     * @return true存在 false不存在
     */
    boolean isPhoneExist(String phone);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 返回用户信息
     */
    SysUser getSysUserByUsername(String username);
}
