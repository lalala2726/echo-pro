package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.constant.RegularConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.framework.annotation.SecurityLog;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import cn.zhangchuangla.framework.security.UserSecurityManager;
import cn.zhangchuangla.framework.security.device.DeviceService;
import cn.zhangchuangla.system.core.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.core.model.entity.SysDept;
import cn.zhangchuangla.system.core.model.entity.SysPost;
import cn.zhangchuangla.system.core.model.entity.SysRole;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import cn.zhangchuangla.system.core.model.request.user.ProfileUpdateRequest;
import cn.zhangchuangla.system.core.model.request.user.SysUserAddRequest;
import cn.zhangchuangla.system.core.model.request.user.SysUserQueryRequest;
import cn.zhangchuangla.system.core.model.request.user.SysUserUpdateRequest;
import cn.zhangchuangla.system.core.model.request.user.profile.UpdateEmailRequest;
import cn.zhangchuangla.system.core.model.request.user.profile.UpdatePasswordRequest;
import cn.zhangchuangla.system.core.model.request.user.profile.UpdatePhoneRequest;
import cn.zhangchuangla.system.core.model.vo.user.*;
import cn.zhangchuangla.system.core.service.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 用户管理控制器
 * 提供用户的增删改查等功能
 *
 * @author Chuang
 */
@Slf4j
@Tag(name = "用户管理", description = "提供用户的新增、删除、修改、查询、重置密码、导出等相关管理接口")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final ExcelExporter excelExporter;
    private final DeviceService deviceService;
    private final SysDeptService sysDeptService;
    private final SysPostService sysPostService;
    private final SysSecurityLogService sysSecurityLogService;
    private final UserSecurityManager userSecurityManager;

    /**
     * 获取用户列表
     * 根据查询条件分页获取系统用户列表
     *
     * @param request 包含分页、排序和筛选条件的用户查询参数
     * @return 分页用户列表结果
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    public AjaxResult<TableDataResult> listUser(@Parameter(description = "用户查询参数，包含分页和筛选条件")
                                                @Validated @ParameterObject SysUserQueryRequest request) {
        Page<SysUserDeptDto> userPage = sysUserService.listUser(request);
        ArrayList<UserListVo> userListVos = new ArrayList<>();
        userPage.getRecords().forEach(user -> {
            UserListVo userInfoVo = new UserListVo();
            BeanUtils.copyProperties(user, userInfoVo);
            if (user.getSysDept() != null) {
                userInfoVo.setDeptName(user.getSysDept().getDeptName());
            }
            userListVos.add(userInfoVo);
        });
        return getTableData(userPage, userListVos);
    }


    /**
     * 导出用户列表
     * 根据查询条件导出系统用户列表
     *
     * @param request 包含分页、排序和筛选条件的用户查询参数
     */
    @PostMapping("/export")
    @Operation(summary = "导出用户列表")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @OperationLog(title = "用户管理", businessType = BusinessType.EXPORT)
    public void exportExcel(HttpServletResponse response,
                            @Parameter(description = "用户查询参数，包含分页和筛选条件")
                            @RequestBody SysUserQueryRequest request) {
        List<SysUser> userList = sysUserService.exportListUser(request);
        ArrayList<UserListVo> userListVos = new ArrayList<>();
        userList.forEach(user -> {
            UserListVo userListVo = BeanCotyUtils.copyProperties(user, UserListVo.class);
            userListVos.add(userListVo);
        });
        excelExporter.exportExcel(response, userListVos, UserListVo.class, "用户列表");
    }

    /**
     * 添加用户
     * 创建新的系统用户
     *
     * @param request 用户添加请求参数，包含用户基本信息
     * @return 添加结果，成功返回用户ID
     */
    @PostMapping()
    @Operation(summary = "添加用户")
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @OperationLog(title = "用户管理", businessType = BusinessType.INSERT)
    public AjaxResult<Long> addUser(@Parameter(description = "添加用户的请求参数，包含用户名、密码等基本信息", required = true)
                                    @Validated @RequestBody SysUserAddRequest request) {
        Assert.isTrue(!sysUserService.isUsernameExist(request.getUsername()), "用户名已存在");
        Assert.isTrue(!sysUserService.isPhoneExist(request.getPhone()), "手机号已存在");
        Assert.isTrue(!sysUserService.isEmailExist(request.getEmail()), "邮箱已存在");
        return success(sysUserService.addUserInfo(request));
    }

    /**
     * 重置用户密码
     *
     * @param request 修改密码的请求参数，包含用户ID和密码
     * @return 修改密码操作结果
     */
    @PutMapping("/resetPassword")
    @Operation(summary = "重置用户密码")
    @PreAuthorize("@ss.hasPermission('system:user:reset')")
    @OperationLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> resetPassword(@Parameter(description = "修改密码的请求参数，传入用户ID和重置的密码即可", required = true)
                                          @Validated @RequestBody SysUserUpdateRequest request) {
        boolean result = sysUserService.resetPassword(request.getPassword(), request.getUserId());
        return toAjax(result);
    }

    /**
     * 删除用户
     * 根据用户ID删除一个或多个用户，支持批量删除
     *
     * @param ids 需要删除的用户ID列表
     * @return 删除操作结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除用户")
    @PreAuthorize("@ss.hasPermission('system:user:info')")
    @OperationLog(title = "用户管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteUserById(@Parameter(description = "用户ID列表，支持批量删除", required = true)
                                           @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "用户ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "用户ID必须大于0！");
        sysUserService.deleteUserById(ids);
        return success();
    }

    /**
     * 修改用户信息
     * 更新用户的基本信息，包括个人资料、联系方式等
     *
     * @param request 修改用户的请求参数
     * @return 修改操作结果
     */
    @PutMapping
    @Operation(summary = "修改用户信息")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    @OperationLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateUserInfoById(@Parameter(description = "修改用户信息的请求参数，包含用户ID和需要修改的字段")
                                               @Validated @RequestBody SysUserUpdateRequest request) {
        sysUserService.isAllowUpdate(request.getUserId());
        Assert.isTrue(!sysUserService.isPhoneExist(request.getPhone(), request.getUserId()), "手机号已存在");
        Assert.isTrue(!sysUserService.isEmailExist(request.getEmail(), request.getUserId()), "邮箱已存在");
        // 业务逻辑
        boolean result = sysUserService.updateUserInfoById(request);
        return toAjax(result);
    }

    /**
     * 重置用户密码
     * 根据用户ID重置用户的登录密码
     *
     * @return 重置操作结果
     */
    @PutMapping("/password/{id:\\d+}")
    @Operation(summary = "根据用户ID重置密码")
    @OperationLog(title = "用户管理", businessType = BusinessType.RESET_PWD)
    @PreAuthorize("@ss.hasPermission('system:user:reset-password')")
    public AjaxResult<Boolean> resetPassword(@PathVariable("id") Long id,
                                             @RequestParam("password") String password) {
        Assert.isTrue(id > 0, "用户ID必须大于0！");
        if (!password.matches(RegularConstants.User.PASSWORD)) {
            return error("密码格式不正确");
        }
        password = encryptPassword(password);
        boolean result = sysUserService.resetPassword(password, id);
        return toAjax(result);
    }

    /**
     * 查询用户信息
     * 根据用户ID获取用户的详细信息，包括基本资料和角色信息
     *
     * @param id 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "根据ID获取用户信息")
    @PreAuthorize("@ss.hasPermission('system:user:info')")
    public AjaxResult<UserInfoVo> getUserInfoById(@Parameter(description = "需要查询的用户ID", required = true)
                                                  @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "用户ID必须大于0！");
        SysUser sysUser = sysUserService.getUserInfoByUserId(id);
        Long userId = sysUser.getUserId();
        Set<Long> roleId = sysRoleService.getUserRoleIdByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(sysUser, userInfoVo);
        userInfoVo.setRoleIds(roleId);
        return success(userInfoVo);
    }


    /**
     * 获取用户信息
     * 获取当前登录用户的详细信息，包括个人资料
     *
     * @return 用户个人资料信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取用户信息")
    public AjaxResult<UserProfileVo> userProfile() {
        UserProfileVo profileVo = sysUserService.getUserProfile();
        return success(profileVo);
    }

    /**
     * 获取用户概述信息
     *
     * @return 用户概述信息
     */
    @GetMapping("/profile/overview")
    @Operation(summary = "获取用户概览信息")
    public AjaxResult<ProfileOverviewInfoVo> overviewInfo() {
        Long userId = SecurityUtils.getUserId();
        SysUser userInfo = sysUserService.getUserInfoByUserId(userId);
        ProfileOverviewInfoVo profileOverviewInfoVo = BeanCotyUtils.copyProperties(userInfo, ProfileOverviewInfoVo.class);
        if (userInfo.getDeptId() != null && userInfo.getDeptId() > 0) {
            SysDept dept = sysDeptService.getDeptById(userInfo.getDeptId());
            profileOverviewInfoVo.setDeptName(dept.getDeptName());
        }
        List<SysRole> roleListByUserId = sysRoleService.getRoleListByUserId(userId);
        if (roleListByUserId != null) {
            List<String> list = roleListByUserId.stream().map(SysRole::getRoleName).toList();
            profileOverviewInfoVo.setRoles(list);
        }
        SysPost postById = sysPostService.getPostById(userInfo.getPostId());
        if (postById != null) {
            profileOverviewInfoVo.setPost(postById.getPostName());
        }
        return success(profileOverviewInfoVo);
    }

    /**
     * 更新用户信息
     *
     * @param request 修改用户信息请求参数
     * @return 操作结果
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户信息")
    public AjaxResult<Void> updateProFile(@RequestBody ProfileUpdateRequest request) {
        boolean result = sysUserService.updateUserProfile(request);
        return toAjax(result);
    }


    /**
     * 获取用户设备列表
     *
     * @return 设备列表
     */
    @Operation(summary = "获取用户设备列表")
    @GetMapping("/security/device")
    public AjaxResult<TableDataResult> getUserDeviceList(SessionDeviceQueryRequest request) {
        String username = getUsername();
        PageResult<SessionDevice> deviceListByUsername = deviceService.getDeviceListByUsername(username, request);
        return getTableData(deviceListByUsername);
    }

    /**
     * 注销全部会话
     *
     * @return 注销结果
     */
    @DeleteMapping("/security/logoutAll")
    @Operation(summary = "注销全部会话")
    public AjaxResult<Void> logoutAll() {
        String username = getUsername();
        boolean logout = userSecurityManager.logout(username);
        return toAjax(logout);
    }

    /**
     * 获取用户安全日志
     *
     * @return 安全日志列表
     */
    @Operation(summary = "获取用户安全日志")
    @GetMapping("/security/log")
    public AjaxResult<List<UserSecurityLog>> securityLog() {
        Long userId = SecurityUtils.getUserId();
        List<SysSecurityLog> userSecurityLog = sysSecurityLogService.getUserSecurityLog(userId);
        List<UserSecurityLog> userSecurityLogs = copyListProperties(userSecurityLog, UserSecurityLog.class);
        return success(userSecurityLogs);
    }


    /**
     * 删除用户设备
     *
     * @param refreshTokenId 刷新令牌ID
     * @return 删除结果
     */
    @DeleteMapping("/security/device/{refreshTokenId}")
    @Operation(summary = "删除用户设备")
    @SecurityLog(title = "退出设备", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> deleteDevice(@PathVariable("refreshTokenId") String refreshTokenId) {
        String username = SecurityUtils.getUsername();
        return deviceService.deleteDeviceAsUser(refreshTokenId, username) ? success() : error();
    }


    /**
     * 修改用户密码
     * 修改当前登录用户的密码
     *
     * @param request 修改密码请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改用户密码")
    @SecurityLog(title = "修改密码", businessType = BusinessType.UPDATE)
    @PutMapping("/security/password")
    public AjaxResult<Void> updatePassword(@RequestBody @Validated UpdatePasswordRequest request) {
        String oldPassword = request.getOldPassword();
        String userPassword = encryptPassword(oldPassword);
        SysUserDetails loginUser = getLoginUser();
        String password = loginUser.getPassword();
        if (!userPassword.equals(password)) {
            return error("旧密码错误");
        }
        boolean result = sysUserService.updatePassword(request);
        return toAjax(result);
    }

    /**
     * 修改用户邮箱
     *
     * @param request 修改邮箱请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改用户邮箱")
    @PostMapping("/email/update")
    @SecurityLog(title = "修改邮箱", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateEmail(@RequestBody @Validated UpdateEmailRequest request) {
        boolean result = sysUserService.updateEmail(request);
        return toAjax(result);
    }


    /**
     * 修改用户手机
     *
     * @param request 修改手机请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改用户手机")
    @PostMapping("/phone/update")
    @SecurityLog(title = "修改手机", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updatePhone(@RequestBody @Validated UpdatePhoneRequest request) {
        boolean result = sysUserService.updatePhone(request);
        return toAjax(result);
    }
}
