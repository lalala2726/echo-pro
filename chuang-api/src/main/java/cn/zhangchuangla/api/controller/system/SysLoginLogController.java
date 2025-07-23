package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.common.core.result.TableDataResult;
import cn.zhangchuangla.common.excel.utils.ExcelUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysLoginLog;
import cn.zhangchuangla.system.model.request.log.SysLoginLogQueryRequest;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogListVo;
import cn.zhangchuangla.system.model.vo.log.SysLoginLogVo;
import cn.zhangchuangla.system.service.SysLoginLogService;
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

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/17 17:44
 */
@RestController
@RequestMapping("/system/log/login")
@RequiredArgsConstructor
@Tag(name = "登录日志", description = "提供系统登录日志")
@Slf4j
public class SysLoginLogController extends BaseController {

    private final SysLoginLogService sysLoginLogService;
    private final ExcelUtils excelUtils;

    /**
     * 获取登录日志列表
     *
     * @param request 登录日志列表查询参数
     * @return 登录日志列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取登录日志列表")
    @PreAuthorize("@ss.hasPermission('system:log:list')")
    public AjaxResult<TableDataResult> listLoginLog(@Parameter(description = "登录日志列表查询参数")
                                                    @Validated @ParameterObject SysLoginLogQueryRequest request) {
        Page<SysLoginLog> sysLoginLogPage = sysLoginLogService.listLoginLog(request);
        List<SysLoginLogListVo> sysLoginLogListVos = copyListProperties(sysLoginLogPage, SysLoginLogListVo.class);
        return getTableData(sysLoginLogPage, sysLoginLogListVos);
    }


    /**
     * 导出登录日志
     *
     * @param request  需要导出的登录日志列表查询参数
     * @param response 响应对象
     */
    @Operation(summary = "导出登录日志")
    @PostMapping("/export")
    @PreAuthorize("@ss.hasPermission('system:log-login:export')")
    @OperationLog(title = "登录日志", businessType = BusinessType.EXPORT)
    public void exportStorageConfig(@ParameterObject SysLoginLogQueryRequest request, HttpServletResponse response) {
        List<SysLoginLog> loginLogs = sysLoginLogService.exportLoginLog(request);
        List<SysLoginLogVo> storageConfigListVos = copyListProperties(loginLogs, SysLoginLogVo.class);
        excelUtils.exportExcel(response, storageConfigListVos, SysLoginLogVo.class, "存储配置列表");
    }

    /**
     * 获取登录日志详情
     *
     * @param id 登录日志ID
     * @return 登录日志详情
     */
    @GetMapping("/{id:\\d+}")
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
     * 清空登录日志
     *
     * @return 清空结果
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清空登录日志")
    @PreAuthorize("@ss.hasPermission('system:log:delete')")
    @OperationLog(title = "日志管理", businessType = BusinessType.CLEAN)
    public AjaxResult<Void> cleanLoginLog() {
        boolean result = sysLoginLogService.cleanLoginLog();
        return toAjax(result);
    }


}
