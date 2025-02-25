package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.system.mapper.UserMapper;
import cn.zhangchuangla.system.model.entity.SysUser;
import cn.zhangchuangla.system.model.request.AddUserRequest;
import cn.zhangchuangla.system.model.request.UserRequest;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<UserMapper, SysUser>
        implements SysUserService {

    /**
     * 进行条件查询
     *
     * @param request 请求参数
     * @return 分页数据
     */
    @Override
    public Page<SysUser> UserList(UserRequest request) {
        LambdaQueryWrapper<SysUser> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据用户名模糊查询
        userLambdaQueryWrapper.like(request.getUsername() != null && !request.getUsername().isEmpty(),
                SysUser::getUsername, request.getUsername());
        //根据昵称模糊查询
        userLambdaQueryWrapper.like(request.getNickName() != null && !request.getNickName().isEmpty(),
                SysUser::getNickName, request.getNickName());
        //根据邮箱模糊查询
        userLambdaQueryWrapper.like(request.getEmail() != null && !request.getEmail().isEmpty(),
                SysUser::getEmail, request.getEmail());
        //根据备注模糊查询
        userLambdaQueryWrapper.like(request.getRemark() != null && !request.getRemark().isEmpty(),
                SysUser::getRemark, request.getRemark());
        //根据手机号精准查询
        userLambdaQueryWrapper.eq(request.getPhone() != null && !request.getPhone().isEmpty(),
                SysUser::getPhone, request.getPhone());
        //根据邮箱精准查询
        userLambdaQueryWrapper.eq(request.getEmail() != null && !request.getEmail().isEmpty(),
                SysUser::getEmail, request.getEmail());
        //根据状态精准查询
        userLambdaQueryWrapper.eq(request.getStatus() != null,
                SysUser::getStatus, request.getStatus());
        //根据性别精准查询
        userLambdaQueryWrapper.eq(request.getGender() != null, SysUser::getGender, request.getGender());
        //根据创建人模糊查询
        userLambdaQueryWrapper.like(request.getCreateBy() != null && !request.getCreateBy().isEmpty(),
                SysUser::getCreateBy, request.getCreateBy());
        //根据修改人模糊查询
        userLambdaQueryWrapper.eq(request.getUpdateBy() != null && !request.getUpdateBy().isEmpty(),
                SysUser::getUpdateBy, request.getUpdateBy());

        return this.page(new Page<>(request.getPageNum(), request.getPageSize()), userLambdaQueryWrapper);
    }

    /**
     * 添加用户信息
     *
     * @param request 请求参数
     * @return 添加成功返回用户ID，失败返回-1
     */
    @Override
    public Long addUserInfo(AddUserRequest request) {
        if (request == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR);
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        if (!save(sysUser)) {
            return -1L;
        }
        return sysUser.getUserId();
    }

    /**
     * 判断用户名是否存在
     *
     * @param username 用户名
     * @return true存在，false不存在
     */
    @Override
    public boolean isUsernameExist(String username) {
        if (username == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "用户名不能为空!");
        }
        return this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)) > 0;
    }

    /**
     * 判断邮箱是否存在
     *
     * @param email 邮箱
     * @return true存在，false不存在
     */
    @Override
    public boolean isEmailExist(String email) {
        if (email == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "邮箱不能为空!");
        }
        return this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email)) > 0;
    }

    /**
     * 判断手机号是否存在
     *
     * @param phone 手机号
     * @return true存在，false不存在
     */
    @Override
    public boolean isPhoneExist(String phone) {
        if (phone == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "手机号不能为空!");
        }
        return this.count(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone)) > 0;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public SysUser getSysUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        sysUserLambdaQueryWrapper.eq(username != null && !username.isEmpty(), SysUser::getUsername, username);
        return getOne(sysUserLambdaQueryWrapper);
    }
}




