package cn.zhangchuangla.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.zhangchuangla.app.model.entity.system.SysUser;
import cn.zhangchuangla.app.model.request.system.AddUserRequest;
import cn.zhangchuangla.app.model.request.system.UpdateUserRequest;
import cn.zhangchuangla.app.model.request.system.UserRequest;
import cn.zhangchuangla.app.model.vo.system.UserVo;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.service.UserService;
import cn.zhangchuangla.common.utils.ValidationUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
@SaCheckLogin
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户列表
     *
     * @param request 请求参数
     * @return 用户列表
     */
    @GetMapping("/list")
    public AjaxResult list(UserRequest request) {
        Page<SysUser> userPage = userService.UserList(request);
        List<UserVo> userVos = userPage.getRecords().stream()
                .map(sysUser -> {
                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(sysUser, userVo);
                    return userVo;
                }).collect(Collectors.toList());
        return AjaxResult.table(userPage, userVos);
    }

    /**
     * 新增用户
     *
     * @param request 新增用户请求参数
     * @return 新增成功返回用户主键,失败返回错误信息
     */
    @PostMapping
    public AjaxResult addUserInfo(@RequestBody @Validated AddUserRequest request) {
        String validationError = validateUserRequest(request);
        if (validationError != null) {
            return getError(validationError);
        }

        if (userService.isUsernameExist(request.getUsername())) {
            return AjaxResult.error("用户名已存在");
        }
        if (userService.isEmailExist(request.getEmail())) {
            return AjaxResult.error("邮箱已存在");
        }
        if (userService.isPhoneExist(request.getPhone())) {
            return AjaxResult.error("手机号已存在");
        }

        return AjaxResult.toSuccess(userService.addUserInfo(request));
    }

    private AjaxResult getError(String validationError) {
        return AjaxResult.error(validationError);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteUser(@PathVariable("id") Long id) {
        if (id == null) {
            return AjaxResult.error("用户ID为空");
        }
        return AjaxResult.isSuccess(userService.removeById(id));
    }

    /**
     * 修改用户信息
     *
     * @param request 修改用户请求
     * @return 修改结果
     */
    @PutMapping
    public AjaxResult updateUserInfoById(@RequestBody @Validated UpdateUserRequest request) {
        if (request.getId() == null) {
            return AjaxResult.error(ResponseCode.PARAM_ERROR, "用户ID不能为空");
        }

        String validationError = validateUserRequest(request);
        if (validationError != null) {
            return AjaxResult.error(validationError);
        }

        if (userService.isUsernameExist(request.getUsername())) {
            return AjaxResult.error("用户名已存在");
        }
        if (userService.isEmailExist(request.getEmail())) {
            return AjaxResult.error("邮箱已存在");
        }
        if (userService.isPhoneExist(request.getPhone())) {
            return AjaxResult.error("手机号已存在");
        }

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(request, sysUser);
        return AjaxResult.isSuccess(userService.updateById(sysUser));
    }

    /**
     * 查询用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public AjaxResult getUserInfoById(@PathVariable("id") Long id) {
        if (id == null) {
            return AjaxResult.error("用户ID为空");
        }
        SysUser sysUser = userService.getById(id);
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo);
        return AjaxResult.success(userVo);
    }

    /**
     * 用户请求参数校验
     *
     * @param request 请求对象
     * @return 错误信息或null
     */
    private String validateUserRequest(Object request) {
        if (request instanceof AddUserRequest addUserRequest) {
            if (!ValidationUtil.isPhoneValid(addUserRequest.getPhone())) {
                return "手机号不合法";
            }
            if (!ValidationUtil.isUsernameValid(addUserRequest.getUsername())) {
                return "用户名必须在5到16位之间";
            }
            if (!ValidationUtil.isEmailValid(addUserRequest.getEmail())) {
                return "邮箱格式不合法";
            }
            if (!ValidationUtil.isPasswordValid(addUserRequest.getPassword())) {
                return "密码必须在6到16位之间";
            }
        }
        return null;
    }
}
