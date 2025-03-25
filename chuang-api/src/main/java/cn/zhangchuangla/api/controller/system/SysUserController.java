package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.user.AddUserRequest;
import cn.zhangchuangla.system.model.request.user.UpdateUserRequest;
import cn.zhangchuangla.system.model.request.user.UserRequest;
import cn.zhangchuangla.system.model.vo.user.UserInfoVo;
import cn.zhangchuangla.system.model.vo.user.UserListVo;
import cn.zhangchuangla.system.service.SysRoleService;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理接口")
@RestController
@Anonymous
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;

    public SysUserController(SysUserService sysUserService, SysRoleService sysRoleService) {
        this.sysUserService = sysUserService;
        this.sysRoleService = sysRoleService;
    }

    /**
     * 获取用户列表
     * 需要特定权限才能访问
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    @PreAuthorize("@auth.hasPermission('system:user:list')")
    public TableDataResult getUserListByQuery(@Parameter(name = "用户查询参数") @Validated UserRequest request) {
        Page<SysUser> userPage = sysUserService.UserList(request);
        List<UserListVo> userListVos = userPage.getRecords().stream()
                .map(sysUser -> {
                    UserListVo userListVo = new UserListVo();
                    BeanUtils.copyProperties(sysUser, userListVo);
                    return userListVo;
                }).collect(Collectors.toList());
        return getTableData(userPage, userListVos);
    }

    /**
     * 添加用户
     * 需要特定角色才能访问
     */
    @PostMapping()
    @Operation(summary = "添加用户")
    @PreAuthorize("@auth.hasPermission('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public AjaxResult addUser(@Parameter(name = "添加用户参数", required = true)
                              @Validated @RequestBody AddUserRequest request) {
        ParamsUtils.objectIsNull(request, "参数不能为空!");
        Long result = sysUserService.addUserInfo(request);
        return toAjax(result);
    }

    /**
     * 删除用户
     *
     * @param ids 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除用户")
    @PreAuthorize("@auth.hasPermission('system:user:info')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteUserById(@Parameter(name = "用户ID", required = true)
                                     @PathVariable("ids") List<Long> ids) {
        ParamsUtils.objectIsNull(ids, "用户ID不能为空!");
        ids.forEach(id -> {
            ParamsUtils.minValidParam(id, "用户ID不能小于等于零!");
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
    @PreAuthorize("@auth.hasPermission('system:user:update')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateUserInfoById(@Parameter(name = "修改用户信息")
                                         @Validated @RequestBody UpdateUserRequest request) {
        sysUserService.isAllowUpdate(request.getUserId());
        //参数校验
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            boolean phoneExist = sysUserService.isPhoneExist(request.getPhone(), request.getUserId());
            ParamsUtils.paramCheck(phoneExist, "手机号已存在!");
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            boolean emailExist = sysUserService.isEmailExist(request.getEmail(), request.getUserId());
            ParamsUtils.paramCheck(emailExist, "邮箱已存在!");
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
    @PreAuthorize("@auth.hasPermission('system:user:info')")
    public AjaxResult getUserInfoById(@Parameter(name = "用户ID", required = true)
                                      @PathVariable("id") Long id) {
        ParamsUtils.minValidParam(id, "用户ID不能小于等于零!");
        SysUser sysUser = sysUserService.getUserInfoByUserId(id);
        Long userId = sysUser.getUserId();
        List<SysRole> roleList = sysRoleService.getRoleListByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setRoles(roleList);
        BeanUtils.copyProperties(sysUser, userInfoVo);
        return success();
    }

}
