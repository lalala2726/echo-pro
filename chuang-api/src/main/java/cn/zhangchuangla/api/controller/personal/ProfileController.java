package cn.zhangchuangla.api.controller.personal;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.framework.annotation.SecurityLog;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import cn.zhangchuangla.framework.security.UserSecurityManager;
import cn.zhangchuangla.framework.security.device.DeviceService;
import cn.zhangchuangla.system.core.model.entity.SysDept;
import cn.zhangchuangla.system.core.model.entity.SysPost;
import cn.zhangchuangla.system.core.model.entity.SysRole;
import cn.zhangchuangla.system.core.model.entity.SysSecurityLog;
import cn.zhangchuangla.system.core.model.request.user.ProfileUpdateRequest;
import cn.zhangchuangla.system.core.model.request.user.profile.UpdateEmailRequest;
import cn.zhangchuangla.system.core.model.request.user.profile.UpdatePasswordRequest;
import cn.zhangchuangla.system.core.model.request.user.profile.UpdatePhoneRequest;
import cn.zhangchuangla.system.core.model.vo.personal.ProfileOverviewInfoVo;
import cn.zhangchuangla.system.core.model.vo.personal.UserProfileVo;
import cn.zhangchuangla.system.core.model.vo.personal.UserSecurityLog;
import cn.zhangchuangla.system.core.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/8/9 04:30
 */
@RestController
@RequestMapping("/personal/profile")
@RequiredArgsConstructor
@Tag(name = "用户个人资料", description = "用户个人资料接口")
public class ProfileController extends BaseController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final DeviceService deviceService;
    private final SysDeptService sysDeptService;
    private final SysPostService sysPostService;
    private final SysSecurityLogService sysSecurityLogService;
    private final UserSecurityManager userSecurityManager;


    /**
     * 获取用户信息
     * 获取当前登录用户的详细信息，包括个人资料
     *
     * @return 用户个人资料信息
     */
    @GetMapping
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
    @GetMapping("/overview")
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
            profileOverviewInfoVo.setPostName(postById.getPostName());
        }
        return success(profileOverviewInfoVo);
    }

    /**
     * 更新用户信息
     *
     * @param request 修改用户信息请求参数
     * @return 操作结果
     */
    @PutMapping
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
     * 获取用户手机
     *
     * @return 用户手机
     */
    @GetMapping("/phone")
    @Operation(summary = "获取用户手机")
    public AjaxResult<HashMap<String, String>> getCurrentPhone() {
        HashMap<String, String> data = new HashMap<>();
        Long userId = SecurityUtils.getUserId();
        String phone = sysUserService.getUserInfoByUserId(userId).getPhone();
        data.put("phone", phone);
        return success(data);
    }

    /**
     * 获取用户邮箱
     *
     * @return 用户邮箱
     */
    @GetMapping("/email")
    @Operation(summary = "获取用户邮箱")
    public AjaxResult<HashMap<String, String>> getCurrentEmail() {
        HashMap<String, String> data = new HashMap<>();
        Long userId = SecurityUtils.getUserId();
        String email = sysUserService.getUserInfoByUserId(userId).getEmail();
        data.put("email", email);
        return success(data);
    }

    /**
     * 修改用户邮箱
     *
     * @param request 修改邮箱请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改用户邮箱")
    @PutMapping("/email")
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
    @PutMapping("/phone")
    @SecurityLog(title = "修改手机", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updatePhone(@RequestBody @Validated UpdatePhoneRequest request) {
        boolean result = sysUserService.updatePhone(request);
        return toAjax(result);
    }

}
