package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.core.model.entity.SysUser;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.common.utils.RegularUtils;
import cn.zhangchuangla.system.model.request.user.AddUserRequest;
import cn.zhangchuangla.system.model.request.user.UpdateUserRequest;
import cn.zhangchuangla.system.model.request.user.UserRequest;
import cn.zhangchuangla.system.model.vo.user.SysUserVo;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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

    @Resource
    private SysUserService sysUserService;

    /**
     * 获取用户列表
     * 需要特定权限才能访问
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    public AjaxResult getUserListByQuery(@Parameter(name = "用户查询参数") UserRequest request) {
        PageUtils.checkPageParams(request.getPageNum(), request.getPageSize());
        Page<SysUser> userPage = sysUserService.UserList(request);
        List<SysUserVo> sysUserVos = userPage.getRecords().stream()
                .map(sysUser -> {
                    SysUserVo sysUserVo = new SysUserVo();
                    BeanUtils.copyProperties(sysUser, sysUserVo);
                    return sysUserVo;
                }).collect(Collectors.toList());
        return AjaxResult.table(userPage, sysUserVos);
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
        ParamsUtils.minValidParam(request.getUserId(), "用户ID不能小于等于0!");
        String validationError = validateUserRequest(request);
        if (validationError != null) {
            return AjaxResult.error(validationError);
        }
        //fixme 修改用户从数据库检验参数时候,要排除当前用户的邮箱和手机号,否则会报重复错误
        boolean result = sysUserService.updateUserInfoById(request);
        return AjaxResult.isSuccess(result);
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
        if (id == null) {
            return AjaxResult.error("用户ID为空");
        }
        SysUser sysUser = sysUserService.getById(id);
        SysUserVo sysUserVo = new SysUserVo();
        BeanUtils.copyProperties(sysUser, sysUserVo);
        return AjaxResult.success(sysUserVo);
    }

    /**
     * 用户请求参数校验
     *
     * @param request 请求对象
     * @return 错误信息或null
     */
    private String validateUserRequest(Object request) {
        if (request instanceof AddUserRequest addUserRequest) {
            if (!RegularUtils.isPhoneValid(addUserRequest.getPhone())) {
                return "手机号不合法";
            }
            if (!RegularUtils.isUsernameValid(addUserRequest.getUsername())) {
                return "用户名必须在5到16位之间";
            }
            if (!RegularUtils.isEmailValid(addUserRequest.getEmail())) {
                return "邮箱格式不合法";
            }
            if (!RegularUtils.isPasswordValid(addUserRequest.getPassword())) {
                return "密码必须在6到16位之间";
            }
        }
        return null;
    }

    private AjaxResult getError(String validationError) {
        return AjaxResult.error(validationError);
    }
}
