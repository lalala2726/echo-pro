package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.common.utils.RegularUtils;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理接口")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

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
    public AjaxResult getUserListByQuery(@Parameter(name = "用户查询参数") UserRequest request) {
        PageUtils.checkPageParams(request.getPageNum(), request.getPageSize());
        Page<SysUser> userPage = sysUserService.UserList(request);
        List<UserListVo> userListVos = userPage.getRecords().stream()
                .map(sysUser -> {
                    UserListVo userListVo = new UserListVo();
                    BeanUtils.copyProperties(sysUser, userListVo);
                    return userListVo;
                }).collect(Collectors.toList());
        return AjaxResult.table(userPage, userListVos);
    }

    /**
     * 添加用户
     * 需要特定角色才能访问
     */
    @PostMapping()
    @Operation(summary = "添加用户")
    public AjaxResult addUser(@Parameter(name = "添加用户参数", required = true)
                              @RequestBody @Validated AddUserRequest request) {
        ParamsUtils.objectIsNull(request, "参数不能为空!");
        return AjaxResult.toSuccess(sysUserService.addUserInfo(request));
    }

    /**
     * 删除用户
     *
     * @param ids 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除用户")
    public AjaxResult deleteUserById(@Parameter(name = "用户ID", required = true)
                                     @PathVariable("ids") List<Long> ids) {
        ParamsUtils.objectIsNull(ids, "用户ID不能为空!");
        ids.forEach(id -> {
            ParamsUtils.minValidParam(id, "用户ID不能小于等于零!");
        });
        sysUserService.deleteUserById(ids);
        return AjaxResult.success();
    }

    /**
     * 修改用户信息
     *
     * @param request 修改用户请求
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改用户信息")
    public AjaxResult updateUserInfoById(@Parameter(name = "修改用户信息", required = true, description = "其中用户ID是必填项,其他参数是修改后的结果")
                                         @RequestBody UpdateUserRequest request) {
        sysUserService.isAllowUpdate(request.getUserId());
        //参数校验
        ParamsUtils.minValidParam(request.getUserId(), "用户ID不能小于等于0!");
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            boolean phoneValid = RegularUtils.isPhoneValid(request.getPhone());
            ParamsUtils.isParamValid(phoneValid, "手机号格式不正确!");
            boolean phoneExist = sysUserService.isPhoneExist(request.getPhone(), request.getUserId());
            ParamsUtils.isParamValid(!phoneExist, "手机号已存在!");
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            boolean emailValid = RegularUtils.isEmailValid(request.getEmail());
            ParamsUtils.isParamValid(emailValid, "邮箱格式不正确!");
            boolean emailExist = sysUserService.isEmailExist(request.getEmail(), request.getUserId());
            ParamsUtils.isParamValid(!emailExist, "邮箱已存在!");
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            boolean passwordValid = RegularUtils.isPasswordValid(request.getPassword());
            ParamsUtils.isParamValid(passwordValid, "密码格式不正确!");
        }
        //业务逻辑
        sysUserService.updateUserInfoById(request);
        return AjaxResult.success();
    }

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取用户信息")
    public AjaxResult getUserInfoById(@Parameter(name = "用户ID", required = true)
                                      @PathVariable("id") Long id) {
        ParamsUtils.minValidParam(id, "用户ID不能小于等于零!");
        SysUser sysUser = sysUserService.getUserInfoByUserId(id);
        Long userId = sysUser.getUserId();
        List<SysRole> roleList = sysRoleService.getRoleListByUserId(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setRoles(roleList);
        BeanUtils.copyProperties(sysUser, userInfoVo);
        return AjaxResult.success(userInfoVo);
    }

}
