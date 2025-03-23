package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.model.entity.LoginUser;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.exception.ServiceException;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.SysUserMapper;
import cn.zhangchuangla.system.model.request.user.AddUserRequest;
import cn.zhangchuangla.system.model.request.user.UpdateUserRequest;
import cn.zhangchuangla.system.model.request.user.UserRequest;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, SysUserRoleService sysUserRoleService, SysRoleService sysRoleService) {
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleService = sysUserRoleService;
        this.sysRoleService = sysRoleService;
    }

    //todo 在修改用户角色信息会有不确定会操作失败,下一步计划打印详细的日志方便进行排查


    /**
     * 进行条件查询
     *
     * @param request 请求参数
     * @return 分页数据
     */
    @Override
    public Page<SysUser> UserList(UserRequest request) {
        Page<SysUser> sysUserPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysUserMapper.UserList(sysUserPage, request);
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
     * 判断邮箱是否存在
     *
     * @param email  邮箱
     * @param userId 排除的用户ID
     * @return true存在，false不存在
     */
    @Override
    public boolean isEmailExist(String email, Long userId) {
        ParamsUtils.paramsNotIsNullOrBlank("邮箱不能为空", email);
        ParamsUtils.minValidParam(userId, "用户ID不能小于等于零");
        Integer count = sysUserMapper.countOtherUserEmails(email, userId);
        return count > 0;
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
     * 判断手机号是否存在
     *
     * @param phone  手机号
     * @param userId 排除的用户ID
     * @return true存在，false不存在
     */
    @Override
    public boolean isPhoneExist(String phone, Long userId) {
        ParamsUtils.paramsNotIsNullOrBlank("手机号不能为空", phone);
        ParamsUtils.minValidParam(userId, "用户ID不能小于等于零");
        Integer count = sysUserMapper.isPhoneExist(phone, userId);
        return count > 0;
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public SysUser getSysUserByUsername(String username) {
        ParamsUtils.paramsNotIsNullOrBlank("用户名不能为空", username);
        LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
        SysUser user = getOne(eq);
        log.info("从数据库找到的用户:{}", user);
        if (user == null) {
            throw new ServiceException(ResponseCode.USER_NOT_EXIST);
        }
        return user;
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

    /**
     * 删除用户信息
     *
     * @param ids 用户ID集合
     */
    @Override
    @Transactional
    public void deleteUserById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        Long userId = SecurityUtils.getUserId();
        ids.forEach(id -> {
            if (Objects.equals(id, userId)) {
                throw new ServiceException(ResponseCode.OPERATION_ERROR, "不能删除自己");
            }
        });
        removeByIds(ids);
    }

    /**
     * 修改用户信息
     *
     * @param request 请求参数
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfoById(UpdateUserRequest request) {
        ParamsUtils.minValidParam(request.getUserId(), "用户ID不能小于等于0");
        List<Long> roles = request.getRoles();
        //去除重复的角色ID,并校验角色ID

        //修改用户信息
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, request.getUserId());
        update(sysUser, eq);
        //修改用户角色
        //1.删除角色所关联的全部角色信息
        Long userId = request.getUserId();
        if (userId != null) {
            sysUserRoleService.deleteUserRoleAssociation(userId);
        }
        //2.添加新的角色信息
        if (roles != null && !roles.isEmpty()) {
            roles.stream().distinct().forEach(role -> {
                ParamsUtils.minValidParam(role, "角色ID不能小于等于0");
            });
            sysUserRoleService.addUserRoleAssociation(roles, request.getUserId());
        }
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public SysUser getUserInfoByUsername(String username) {
        LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username);
        if (sysUserMapper.getUserInfoByUsername(username) != null) {
            return sysUserMapper.getUserInfoByUsername(username);
        }
        return null;
    }

    /**
     * 如果是修改当前用户信息或者是修改超级管理员的信息，则不允许修改
     *
     * @param userId 用户ID
     */
    @Override
    public void isAllowUpdate(Long userId) {
        if (userId == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        boolean admin = loginUser.getSysUser().isSuperAdmin();
        Long currentId = loginUser.getUserId();
        if (admin || Objects.equals(currentId, userId)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "不允许修改当前用户信息");
        }
    }
}




