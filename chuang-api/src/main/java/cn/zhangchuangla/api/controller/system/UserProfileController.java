package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.PageResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.entity.security.SysUserDetails;
import cn.zhangchuangla.common.core.utils.SecurityUtils;
import cn.zhangchuangla.framework.model.entity.SessionDevice;
import cn.zhangchuangla.framework.model.request.SessionDeviceQueryRequest;
import cn.zhangchuangla.framework.security.device.DeviceService;
import cn.zhangchuangla.system.model.request.user.profile.UpdatePasswordRequest;
import cn.zhangchuangla.system.model.request.user.profile.UserProfileUpdateRequest;
import cn.zhangchuangla.system.model.vo.user.profile.UserProfileVo;
import cn.zhangchuangla.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Chuang
 * <p>
 * created on 2025/6/7 21:34
 */
@RequestMapping("/system/user/profile")
@RequiredArgsConstructor
@RestController
public class UserProfileController extends BaseController {

    private final SysUserService sysUserService;
    private final DeviceService deviceService;

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
     * 获取用户设备列表
     *
     * @return 设备列表
     */
    @Operation(summary = "获取用户设备列表")
    @GetMapping("/devices")
    public AjaxResult<TableDataResult> getUserDeviceList(SessionDeviceQueryRequest request) {
        String username = getUsername();
        PageResult<SessionDevice> deviceListByUsername = deviceService.getDeviceListByUsername(username, request);
        return getTableData(deviceListByUsername);
    }

    /**
     * 删除用户设备
     *
     * @param refreshTokenId 刷新令牌ID
     * @return 删除结果
     */
    @DeleteMapping("/device/{refreshTokenId}")
    @Operation(summary = "删除用户设备")
    public AjaxResult<Void> deleteDevice(@PathVariable("refreshTokenId") String refreshTokenId) {
        String username = SecurityUtils.getUsername();
        return deviceService.deleteDeviceAsUser(refreshTokenId, username) ? success() : error();
    }


    /**
     * 修改用户信息
     * 修改当前登录用户的个人资料
     *
     * @param request 修改用户信息请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改用户信息")
    @PutMapping
    public AjaxResult<Void> updateUserProfile(@RequestBody UserProfileUpdateRequest request) {
        boolean result = sysUserService.updateUserProfile(request);
        return toAjax(result);
    }


    /**
     * 修改用户密码
     * 修改当前登录用户的密码
     *
     * @param request 修改密码请求参数
     * @return 修改结果
     */
    @Operation(summary = "修改用户密码")
    @PutMapping("/password")
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

}
