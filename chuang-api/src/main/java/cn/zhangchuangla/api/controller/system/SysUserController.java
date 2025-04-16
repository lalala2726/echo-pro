package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.converter.SysUserConverter;
import cn.zhangchuangla.system.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.user.AddUserRequest;
import cn.zhangchuangla.system.model.request.user.UpdateUserRequest;
import cn.zhangchuangla.system.model.request.user.UserRequest;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.model.vo.user.UserListVo;
import cn.zhangchuangla.system.model.vo.user.UserProfileVo;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理接口")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysUserConverter sysUserConverter;


    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/profile")
    @Operation(summary = "获取用户信息")
    public AjaxResult userProfile() {
        UserProfileVo profileVo = sysUserService.getUserProfile();
        return success(profileVo);
    }

    /**
     * 获取用户列表
     * 需要特定权限才能访问
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    @PreAuthorize("@ss.hasPermission('system:user:list')")
    public AjaxResult listUser(@Parameter(name = "用户查询参数") @Validated UserRequest request) {
        Page<SysUserDeptDto> userPage = sysUserService.listUser(request);
        ArrayList<UserListVo> userListVos = new ArrayList<>();
        userPage.getRecords().forEach(item -> {
            UserListVo userListVo = sysUserConverter.toUserListVo(item);
            userListVos.add(userListVo);
        });
        return getTableData(userPage, userListVos);
    }

    /**
     * 添加用户
     * 需要特定角色才能访问
     */
    @PostMapping()
    @Operation(summary = "添加用户")
    @PreAuthorize("@ss.hasPermission('system:user:add')")
    @OperationLog(title = "用户管理", businessType = BusinessType.INSERT)
    public AjaxResult addUser(@Parameter(name = "添加用户参数", required = true)
                              @Validated @RequestBody AddUserRequest request) {
        return toAjax(sysUserService.addUserInfo(request));
    }

    /**
     * 删除用户
     *
     * @param ids 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除用户")
    @PreAuthorize("@ss.hasPermission('system:user:info')")
    @OperationLog(title = "用户管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteUserById(@Parameter(name = "用户ID", required = true)
                                     @PathVariable("ids") List<Long> ids) {
        ids.forEach(id -> {
            checkParam(id == null || id <= 0, "用户ID不能为空!");
        });
        sysUserService.deleteUserById(ids);
        return success();
    }

    /**
     * 修改用户信息
     *
     * @param request 修改用户请求
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改用户信息")
    @PreAuthorize("@ss.hasPermission('system:user:update')")
    @OperationLog(title = "用户管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateUserInfoById(@Parameter(name = "修改用户信息")
                                         @Validated @RequestBody UpdateUserRequest request) {
        sysUserService.isAllowUpdate(request.getUserId());
        //参数校验
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            checkParam(sysUserService.isPhoneExist(request.getPhone(), request.getUserId()), "手机号已存在");
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            checkParam(sysUserService.isEmailExist(request.getEmail(), request.getUserId()), "邮箱已经存在");
        }
        //业务逻辑
        sysUserService.updateUserInfoById(request);
        return success();
    }

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取用户信息")
    @PreAuthorize("@ss.hasPermission('system:user:info')")
    public AjaxResult getUserInfoById(@Parameter(name = "用户ID", required = true)
                                      @PathVariable("id") Long id) {
        if (id == null || id <= 0) return error("用户ID不能小于等于0");
        SysUser sysUser = sysUserService.getUserInfoByUserId(id);
        Long userId = sysUser.getUserId();
        List<SysRole> roleList = sysRoleService.getRoleListByUserId(userId);
        UserInfoVo userInfoVo = sysUserConverter.toUserInfoVo(sysUser);
        userInfoVo.setRoles(roleList);
        return success();
    }

}
