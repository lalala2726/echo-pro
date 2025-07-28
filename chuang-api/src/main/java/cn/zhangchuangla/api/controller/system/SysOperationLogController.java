package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.excel.utils.ExcelExportService;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysOperationLog;
import cn.zhangchuangla.system.core.model.request.log.SysOperationLogQueryRequest;
import cn.zhangchuangla.system.core.model.vo.log.SysOperationLogListVo;
import cn.zhangchuangla.system.core.model.vo.log.SysOperationLogVo;
import cn.zhangchuangla.system.core.service.SysOperationLogService;
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
@RequestMapping("/system/log/operation")
@RequiredArgsConstructor
@Tag(name = "操作日志", description = "提供系统操作日志")
@Slf4j
public class SysOperationLogController extends BaseController {

    private final SysOperationLogService sysOperationLogService;
    private final ExcelExportService excelExportService;


    /**
     * 获取操作日志列表
     *
     * @param request 操作日志列表查询参数
     * @return 操作日志列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取操作日志列表")
    @PreAuthorize("@ss.hasPermission('system:log:list')")
    public AjaxResult<TableDataResult> listOperationLog(@Parameter(description = "操作日志列表查询参数")
                                                        @Validated @ParameterObject SysOperationLogQueryRequest request) {
        Page<SysOperationLog> sysOperationLogPage = sysOperationLogService.listOperationLog(request);
        List<SysOperationLogListVo> sysOperationLogListVos = copyListProperties(sysOperationLogPage,
                SysOperationLogListVo.class);
        return getTableData(sysOperationLogPage, sysOperationLogListVos);
    }

    @PostMapping("/export")
    @Operation(summary = "导出操作日志")
    @OperationLog(title = "登录日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermission('system:log-operation:export')")
    public void exportOperationLog(@Parameter(description = "登录日志导出")
                                   @ParameterObject SysOperationLogQueryRequest request,
                                   HttpServletResponse response) {
        List<SysOperationLog> sysOperationLogs = sysOperationLogService.exportOperationLog(request);
        List<SysOperationLogVo> sysOperationLogVos = copyListProperties(sysOperationLogs, SysOperationLogVo.class);
        excelExportService.exportExcel(response, sysOperationLogVos, SysOperationLogVo.class, "操作日志");
    }

    /**
     * 获取操作日志详情
     *
     * @param id 操作日志ID
     * @return 操作日志详情
     */
    @GetMapping("/{id:\\d+}")
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
     * 清空操作日志
     *
     * @return 清空结果
     */
    @DeleteMapping("/clean")
    @Operation(summary = "清空操作日志")
    @PreAuthorize("@ss.hasPermission('system:log:delete')")
    @OperationLog(title = "日志管理", businessType = BusinessType.CLEAN)
    public AjaxResult<Void> cleanOperationLog() {
        boolean result = sysOperationLogService.cleanOperationLog();
        return toAjax(result);
    }


}
