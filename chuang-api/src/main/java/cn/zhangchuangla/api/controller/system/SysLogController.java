package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.security.model.SysUser;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.SecurityUtils;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.converter.SysLogConverter;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogListRequest;
import cn.zhangchuangla.system.model.request.log.SysOperationLogListRequest;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogListVo;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogVo;
import cn.zhangchuangla.system.model.vo.log.SysOperationLogListVo;
import cn.zhangchuangla.system.model.vo.log.SysOperationLogVo;
import cn.zhangchuangla.system.service.SysLoginLogService;
import cn.zhangchuangla.system.service.SysOperationLogService;
import cn.zhangchuangla.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 17:44
 */
@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
@Tag(name = "系统日志")
public class SysLogController extends BaseController {

    private final SysLoginLogService sysLoginLogService;
    private final SysOperationLogService sysOperationLogService;
    private final SysUserService sysUserService;
    private final SysLogConverter sysLogConverter;

    /**
     * 获取登录日志列表
     *
     * @param request 查询参数
     * @return 登录日志列表
     */
    @GetMapping("/login/list")
    @Operation(summary = "获取登录日志列表")
    @PreAuthorize("@ss.hasPermission('system:log:list')")
    public AjaxResult listLoginLog(SysLoginLogListRequest request) {
        Page<SysLoginLog> sysLoginLogPage = sysLoginLogService.listLoginLog(request);
        List<SysLoginLogListVo> sysLoginLogListVos = copyListProperties(sysLoginLogPage, SysLoginLogListVo.class);
        return success(getTableData(sysLoginLogPage, sysLoginLogListVos));
    }

    /**
     * 获取操作日志列表
     *
     * @param request 查询参数
     * @return 操作日志列表
     */
    @GetMapping("/operation/list")
    @Operation(summary = "获取操作日志列表")
    @PreAuthorize("@ss.hasPermission('system:log:list')")
    public AjaxResult listOperationLog(SysOperationLogListRequest request) {
        Page<SysOperationLog> sysOperationLogPage = sysOperationLogService.listOperationLog(request);
        List<SysOperationLogListVo> sysOperationLogListVos = copyListProperties(sysOperationLogPage, SysOperationLogListVo.class);
        return success(getTableData(sysOperationLogPage, sysOperationLogListVos));
    }

    /**
     * 获取登录日志详情
     *
     * @param id 日志ID
     * @return 登录日志详情
     */
    @GetMapping("/login/{id}")
    @Operation(summary = "获取登录日志详情")
    @PreAuthorize("@ss.hasPermission('system:log:query')")
    public AjaxResult getLoginLogById(@PathVariable Long id) {
        SysLoginLog sysLoginLog = sysLoginLogService.getLoginLogById(id);
        SysLoginLogVo sysLoginLogVo = sysLogConverter.toSysLoginLogVo(sysLoginLog);
        return success(sysLoginLogVo);
    }

    /**
     * 获取操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    @GetMapping("/operation/{id}")
    @Operation(summary = "获取操作日志详情")
    @PreAuthorize("@ss.hasPermission('system:log:query')")
    public AjaxResult getOperationLogById(@PathVariable Long id) {
        SysOperationLog sysOperationLog = sysOperationLogService.getOperationLogById(id);
        SysOperationLogVo sysOperationLogVo = sysLogConverter.toSysOperationLogVo(sysOperationLog);
        return success(sysOperationLogVo);
    }


    /**
     * 清空登录日志
     *
     * @return 清空结果
     */
    @DeleteMapping("/login")
    @Operation(summary = "清空登录日志", description = "为了系统安全系统不会提供单条或多条数据删除服务，只能清空所有数据，并且需要输入当前用户密码进行验证")
    @PreAuthorize("@ss.hasPermission('system:log:delete')")
    @OperationLog(title = "日志管理", businessType = BusinessType.CLEAN)
    public AjaxResult cleanLoginLog(@RequestParam("password") String password) {
        if (password.isEmpty()) return error("您还没有输入密码！");
        if (verifyCurrentUserPassword(password)) {
            return warning("当前用户密码不正确！");
        }
        boolean result = sysLoginLogService.cleanLoginLog();
        return toAjax(result);
    }


    /**
     * 清空操作日志
     *
     * @return 清空结果
     */
    @DeleteMapping("/operation")
    @Operation(summary = "清空操作日志", description = "为了系统安全系统不会提供单条或多条数据删除服务，只能清空所有数据，并且需要输入当前用户密码进行验证")
    @PreAuthorize("@ss.hasPermission('system:log:delete')")
    @OperationLog(title = "日志管理", businessType = BusinessType.CLEAN)
    public AjaxResult cleanOperationLog(@RequestParam("password") String password) {
        if (password.isEmpty()) return error("您还没有输入密码！");
        if (verifyCurrentUserPassword(password)) {
            return warning("当前用户密码不正确！");
        }
        boolean result = sysOperationLogService.cleanLoginLog();
        return toAjax(result);
    }

    /**
     * 验证当前用户密码是否正确,某些场景下需要验证当前用户的密码
     *
     * @param rawPassword 明文密码
     * @return 是否正确
     */
    private boolean verifyCurrentUserPassword(String rawPassword) {
        Long currentUserId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserService.getUserInfoByUserId(currentUserId);
        if (sysUser == null || sysUser.getPassword() == null) {
            return false;
        }
        String encryptedInputPassword = encryptPassword(rawPassword);
        return SecurityUtils.constantTimeEquals(sysUser.getPassword(), encryptedInputPassword);
    }


}

