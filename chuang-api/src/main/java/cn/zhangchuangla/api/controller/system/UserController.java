package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.constant.RegularConstants;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.entity.security.SysUser;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.dto.SysUserDeptDto;
import cn.zhangchuangla.system.core.model.request.user.SysUserAddRequest;
import cn.zhangchuangla.system.core.model.request.user.SysUserQueryRequest;
import cn.zhangchuangla.system.core.model.request.user.SysUserUpdateRequest;
import cn.zhangchuangla.system.core.model.vo.user.SysUserInfoVo;
import cn.zhangchuangla.system.core.model.vo.user.SysUserListVo;
import cn.zhangchuangla.system.core.service.SysRoleService;
import cn.zhangchuangla.system.core.service.SysUserService;
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
        ArrayList<SysUserListVo> sysUserListVos = new ArrayList<>();
        userPage.getRecords().forEach(user -> {
            SysUserListVo userInfoVo = new SysUserListVo();
            BeanUtils.copyProperties(user, userInfoVo);
            if (user.getSysDept() != null) {
                userInfoVo.setDeptName(user.getSysDept().getDeptName());
            }
            sysUserListVos.add(userInfoVo);
        });
        return getTableData(userPage, sysUserListVos);
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
    public AjaxResult<SysUserInfoVo> getUserInfoById(@Parameter(description = "需要查询的用户ID", required = true)
                                                         @PathVariable("id") Long id) {
        Assert.isTrue(id > 0, "用户ID必须大于0！");
        SysUser sysUser = sysUserService.getUserInfoByUserId(id);
        Long userId = sysUser.getUserId();
        Set<Long> roleId = sysRoleService.getUserRoleIdByUserId(userId);
        SysUserInfoVo sysUserInfoVo = new SysUserInfoVo();
        BeanUtils.copyProperties(sysUser, sysUserInfoVo);
        sysUserInfoVo.setRoleIds(roleId);
        return success(sysUserInfoVo);
    }


    /**
     * 添加用户
     * 创建新的系统用户
     *
     * @param request 用户添加请求参数，包含用户基本信息
     * @return 添加结果，成功返回用户ID
     */
    @PostMapping
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
        ArrayList<SysUserListVo> sysUserListVos = new ArrayList<>();
        userList.forEach(user -> {
            SysUserListVo sysUserListVo = BeanCotyUtils.copyProperties(user, SysUserListVo.class);
            sysUserListVos.add(sysUserListVo);
        });
        excelExporter.exportExcel(response, sysUserListVos, SysUserListVo.class, "用户列表");
    }
}
