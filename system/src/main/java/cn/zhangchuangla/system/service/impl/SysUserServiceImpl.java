package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.system.mapper.SysUserMapper;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.system.model.request.AddUserRequest;
import cn.zhangchuangla.system.model.request.UserRequest;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {


    /**
     * 进行条件查询
     *
     * @param request 请求参数
     * @return 分页数据
     */
    @Override
    public Page<SysUser> UserList(UserRequest request) {

        //fixme 采用更加简介明了的学法，如果超3次查询语句，就直接使用Mybatis原生的查询语句
        return this.page(new Page<>(request.getPageNum(), request.getPageSize()));
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

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public SysUser getUserInfoByUserId(Long userId) {
        ParamsUtils.minValidParam(userId, "用户ID不能为空");
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId);
        SysUser one = getOne(sysUserLambdaQueryWrapper);
        if (one == null) {
            throw new ServiceException(ResponseCode.USER_NOT_EXIST);
        }
        return one;
    }
}




