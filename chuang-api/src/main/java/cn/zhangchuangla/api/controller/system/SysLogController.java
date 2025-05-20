package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.result.TableDataResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.framework.annotation.RequiresSecondAuth;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.entity.SysOperationLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogQueryRequest;
import cn.zhangchuangla.system.model.request.log.SysOperationLogQueryRequest;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogListVo;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogVo;
import cn.zhangchuangla.system.model.vo.log.SysOperationLogListVo;
import cn.zhangchuangla.system.model.vo.log.SysOperationLogVo;
import cn.zhangchuangla.system.service.SysLoginLogService;
import cn.zhangchuangla.system.service.SysOperationLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@Slf4j
public class SysLogController extends BaseController {

    private final SysLoginLogService sysLoginLogService;
    private final SysOperationLogService sysOperationLogService;

    /**
     * 获取登录日志列表
     *
     * @param request 登录日志列表查询参数
     * @return 登录日志列表
     */
    @GetMapping("/login/list")
    @Operation(summary = "获取登录日志列表")
    @PreAuthorize("@ss.hasPermission('system:log:list')")
    public AjaxResult<TableDataResult> listLoginLog(@Parameter(description = "登录日志列表查询参数")
                                                        @Validated @ParameterObject SysLoginLogQueryRequest request) {
        Page<SysLoginLog> sysLoginLogPage = sysLoginLogService.listLoginLog(request);
        List<SysLoginLogListVo> sysLoginLogListVos = copyListProperties(sysLoginLogPage, SysLoginLogListVo.class);
        return getTableData(sysLoginLogPage, sysLoginLogListVos);
    }

    /**
     * 获取操作日志列表
     *
     * @param request 操作日志列表查询参数
     * @return 操作日志列表
     */
    @GetMapping("/operation/list")
    @Operation(summary = "获取操作日志列表")
    @PreAuthorize("@ss.hasPermission('system:log:list')")
    public AjaxResult<TableDataResult> listOperationLog(@Parameter(description = "操作日志列表查询参数")
                                                            @Validated @ParameterObject SysOperationLogQueryRequest request) {
        Page<SysOperationLog> sysOperationLogPage = sysOperationLogService.listOperationLog(request);
        List<SysOperationLogListVo> sysOperationLogListVos = copyListProperties(sysOperationLogPage,
                SysOperationLogListVo.class);
        return getTableData(sysOperationLogPage, sysOperationLogListVos);
    }

    /**
     * 获取登录日志详情
     *
     * @param id 登录日志ID
     * @return 登录日志详情
     */
    @GetMapping("/login/{id}")
    @Operation(summary = "获取登录日志详情")
    @PreAuthorize("@ss.hasPermission('system:log:query')")
    public AjaxResult<SysLoginLogVo> getLoginLogById(@Parameter(description = "登录日志ID")
                                                     @PathVariable("id") Long id) {
        SysLoginLog sysLoginLog = sysLoginLogService.getLoginLogById(id);
        SysLoginLogVo sysLoginLogVo = new SysLoginLogVo();
        BeanUtils.copyProperties(sysLoginLog, sysLoginLogVo);
        return success(sysLoginLogVo);
    }

    /**
     * 获取操作日志详情
     *
     * @param id 操作日志ID
     * @return 操作日志详情
     */
    @GetMapping("/operation/{id}")
    @Operation(summary = "获取操作日志详情")
    @PreAuthorize("@ss.hasPermission('system:log:query')")
    public AjaxResult<SysOperationLogVo> getOperationLogById(@Parameter(description = "操作日志ID")
                                                             @PathVariable("id") Long id) {
        SysOperationLog sysOperationLog = sysOperationLogService.getOperationLogById(id);
        SysOperationLogVo sysOperationLogVo = new SysOperationLogVo();
        BeanUtils.copyProperties(sysOperationLog, sysOperationLogVo);
        return success(sysOperationLogVo);
    }

    /**
     * 清空登录日志
     *
     * @return 清空结果
     */
    @DeleteMapping("/login")
    @Operation(summary = "清空登录日志", description = "此方法需要传入当前用户密码进行验证")
    @PreAuthorize("@ss.hasPermission('system:log:delete')")
    @OperationLog(title = "日志管理", businessType = BusinessType.CLEAN)
    @RequiresSecondAuth()
    public AjaxResult<Void> cleanLoginLog() {
        boolean result = sysLoginLogService.cleanLoginLog();
        return toAjax(result);
    }

    /**
     * 清空操作日志
     *
     * @return 清空结果
     */
    @DeleteMapping("/operation")
    @Operation(summary = "清空操作日志", description = "此方法需要传入当前用户密码进行验证")
    @PreAuthorize("@ss.hasPermission('system:log:delete')")
    @OperationLog(title = "日志管理", businessType = BusinessType.CLEAN)
    @RequiresSecondAuth()
    public AjaxResult<Void> cleanOperationLog() {
        boolean result = sysOperationLogService.cleanOperationLog();
        return toAjax(result);
    }


}
