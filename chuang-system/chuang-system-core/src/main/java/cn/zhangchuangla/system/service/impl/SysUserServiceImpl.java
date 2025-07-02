package cn.zhangchuangla.system.service.impl;

import cn.zhangchuangla.common.core.constant.SysRolesConstant;
import cn.zhangchuangla.common.core.core.security.model.SysUser;
import cn.zhangchuangla.common.core.enums.ResponseCode;
import cn.zhangchuangla.common.core.exception.ServiceException;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.system.mapper.SysUserMapper;
import cn.zhangchuangla.system.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.user.SysUserAddRequest;
import cn.zhangchuangla.system.model.request.user.SysUserQueryRequest;
import cn.zhangchuangla.system.model.request.user.SysUserUpdateRequest;
import cn.zhangchuangla.system.model.request.user.profile.UpdatePasswordRequest;
import cn.zhangchuangla.system.model.request.user.profile.UserProfileUpdateRequest;
import cn.zhangchuangla.system.model.vo.user.profile.UserProfileVo;
import cn.zhangchuangla.system.service.SysDeptService;
import cn.zhangchuangla.system.service.SysUserRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 用户实现类
 *
 * @author Chuang
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleService sysUserRoleService;
    private final SysDeptService sysDeptService;

    /**
     * 进行条件查询
     *
     * @param request 请求参数
     * @return 分页数据
     */
    @Override
    public Page<SysUserDeptDto> listUser(SysUserQueryRequest request) {
        Page<SysUserDeptDto> sysUserPage = new Page<>(request.getPageNum(), request.getPageSize());
        return sysUserMapper.listUser(sysUserPage, request);
    }

    /**
     * 添加用户信息
     *
     * @param request 请求参数
     * @return 添加成功返回用户ID，失败返回-1
     */
    @Override
    public Long addUserInfo(SysUserAddRequest request) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        // 部门ID校验
        Long deptId = request.getDeptId();
        if (deptId != null && deptId > 0) {
            SysDept dept = sysDeptService.getDeptById(deptId);
            if (dept == null) {
                throw new ServiceException(ResponseCode.RESULT_IS_NULL, String.format("部门ID:<%s>不存在！", deptId));
            }
        }
        // 角色ID校验
        List<Long> roleIds = request.getRoleIds();
        //存入数据库
        if (!save(sysUser)) {
            return -1L;
        }
        Long userId = sysUser.getUserId();
        //添加用户角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            sysUserRoleService.addUserRoleAssociation(roleIds, userId);
        }
        return userId;
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
        LambdaQueryWrapper<SysUser> sysUserLambdaQueryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, userId);
        SysUser user = getOne(sysUserLambdaQueryWrapper);
        if (user == null) {
            throw new ServiceException(ResponseCode.USER_NOT_EXIST);
        }
        return user;
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
    public boolean updateUserInfoById(SysUserUpdateRequest request) {
        if (request == null) {
            throw new ServiceException(ResponseCode.PARAM_ERROR, "请求参数不能为空");
        }

        List<Long> roles = request.getRoleIds();
        Long deptId = request.getDeptId();
        Long userId = request.getUserId();

        // 部门ID校验
        if (deptId != null && deptId > 0) {
            SysDept dept = sysDeptService.getDeptById(deptId);
            if (dept == null) {
                throw new ServiceException(ResponseCode.RESULT_IS_NULL, String.format("部门ID:<%s>不存在！", deptId));
            }
        }

        // 修改用户信息
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        sysUser.setRemark(request.getRemark());
        LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserId, userId);

        boolean update = update(sysUser, eq);
        if (!update) {
            throw new ServiceException(ResponseCode.UPDATE_ERROR, "用户信息更新失败");
        }

        // 删除角色所关联的全部角色信息
        if (userId != null) {
            sysUserRoleService.deleteUserRoleAssociation(userId);
        }

        // 添加新的角色信息
        sysUserRoleService.addUserRoleAssociation(roles, userId);
        return true;
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
        SysUser user = getOne(eq);
        if (user == null) {
            throw new ServiceException(ResponseCode.RESULT_IS_NULL, String.format("用户名:%s不存在！", username));
        }
        return user;
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
        Long currentUserId = SecurityUtils.getUserId();
        if (Objects.equals(currentUserId, userId)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "不允许修改自己的信息！");
        }
        Set<String> roles = SecurityUtils.getRoles();
        if (roles.contains(SysRolesConstant.SUPER_ADMIN)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "不允许修改超级管理员的信息！");
        }

    }

    /**
     * 获取用户个人中心信息
     *
     * @return 用户个人中心信息
     */
    @Override
    public UserProfileVo getUserProfile() {
        Long currentUserId = SecurityUtils.getUserId();
        SysUser user = getUserInfoByUserId(currentUserId);
        UserProfileVo userProfileVo = new UserProfileVo();
        BeanUtils.copyProperties(user, userProfileVo);
        log.info("获取用户信息:{}", user);
        userProfileVo.setDeptName("开发部门");
        return userProfileVo;
    }

    /**
     * 根据ID重置密码
     *
     * @param password 新密码
     * @return 操作结果
     */
    @Override
    public boolean resetPassword(String password, Long userId) {
        Long currentUserId = SecurityUtils.getUserId();
        Set<String> current = SecurityUtils.getRoles();

        //不允许用户重置自己密码
        if (Objects.equals(currentUserId, userId)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "不允许重置当前用户密码");
        }
        //不允许用户重置管理员密码
        Set<String> roles = SecurityUtils.getRoles();
        if (roles.contains(SysRolesConstant.SUPER_ADMIN)) {
            throw new ServiceException(ResponseCode.OPERATION_ERROR, "不允许重置超级管理员密码");
        }
        SysUser sysUser = SysUser.builder()
                .userId(userId)
                .password(password)
                .build();
        return updateById(sysUser);
    }

    /**
     * 修改密码
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updatePassword(UpdatePasswordRequest request) {
        String newPassword = SecurityUtils.encryptPassword(request.getNewPassword());
        LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, SecurityUtils.getUserId());
        SysUser sysUser = SysUser.builder()
                .password(newPassword)
                .build();
        return update(sysUser, eq);
    }

    /**
     * 修改用户个人信息
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @Override
    public boolean updateUserProfile(UserProfileUpdateRequest request) {
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        LambdaQueryWrapper<SysUser> eq = new LambdaQueryWrapper<SysUser>().eq(SysUser::getUserId, SecurityUtils.getUserId());
        return update(sysUser, eq);
    }
}
