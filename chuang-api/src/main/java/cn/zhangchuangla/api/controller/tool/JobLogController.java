package cn.zhangchuangla.api.controller.tool;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.quartz.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobLogQueryRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobLogListVo;
import cn.zhangchuangla.quartz.model.vo.SysJobLogVo;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务日志控制器
 *
 * @author Chuang
 */
@RestController
@RequestMapping("/tool/job/log")
@RequiredArgsConstructor
@Tag(name = "定时任务日志管理", description = "定时任务执行日志的查询、清理等功能")
public class JobLogController extends BaseController {


    private final SysJobLogService sysJobLogService;
    private final ExcelExporter excelExporter;

    /**
     * 获取定时任务日志列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取定时任务日志列表")
    @PreAuthorize("@ss.hasPermission('tool:job-log:list')")
    public AjaxResult<TableDataResult> list(@Parameter(description = "定时任务日志查询参数")
                                            @Validated @ParameterObject SysJobLogQueryRequest request) {
        Page<SysJobLog> page = sysJobLogService.selectJobLogList(request);
        List<SysJobLogListVo> sysJobLogListVos = copyListProperties(page, SysJobLogListVo.class);
        return getTableData(page, sysJobLogListVos);
    }

    /**
     * 获取定时任务日志详情
     */
    @GetMapping("/{jobLogId:\\d+}")
    @Operation(summary = "获取定时任务日志详情")
    @PreAuthorize("@ss.hasPermission('tool:job-log:query')")
    public AjaxResult<SysJobLogVo> getInfo(@Parameter(description = "日志ID") @PathVariable("jobLogId") Long jobLogId) {
        Assert.notNull(jobLogId, "日志ID不能为空");
        SysJobLog sysJobLog = sysJobLogService.selectJobLogById(jobLogId);
        SysJobLogVo sysJobLogVo = BeanCotyUtils.copyProperties(sysJobLog, SysJobLogVo.class);
        return success(sysJobLogVo);
    }

    /**
     * 删除定时任务日志
     */
    @DeleteMapping("/{logIds:\\d+}")
    @Operation(summary = "删除定时任务日志")
    @PreAuthorize("@ss.hasPermission('tool:job-log:delete')")
    @OperationLog(title = "定时任务日志", businessType = BusinessType.DELETE)
    public AjaxResult<Void> remove(@Parameter(description = "日志ID列表，多个用逗号分隔") @PathVariable List<Long> logIds) {
        Assert.notEmpty(logIds, "日志ID不能为空");
        boolean result = sysJobLogService.deleteJobLogs(logIds);
        return result ? success("删除成功") : error("删除失败");
    }

    /**
     * 清理所有日志
     */
    @DeleteMapping("/clean/all")
    @Operation(summary = "清理所有日志")
    @PreAuthorize("@ss.hasPermission('tool:job-log:delete')")
    @OperationLog(title = "定时任务调度日志", businessType = BusinessType.CLEAN)
    public AjaxResult<Void> cleanAll() {
        int count = sysJobLogService.cleanAllLogs();
        return success("清理成功，共清理 " + count + " 条日志");
    }

    /**
     * 导出定时任务日志
     */
    @PostMapping("/export")
    @Operation(summary = "导出定时任务")
    @PreAuthorize("@ss.hasPermission('tool:job:export')")
    @OperationLog(title = "定时任务日志", businessType = BusinessType.EXPORT)
    public void exportJobLog(@Parameter(description = "定时任务日志查询参数") @RequestBody SysJobLogQueryRequest request,
                             HttpServletResponse response) {
        List<SysJobLog> sysJobLogPage = sysJobLogService.exportJobLogList(request);
        List<SysJobLogVo> sysJobLogVos = copyListProperties(sysJobLogPage, SysJobLogVo.class);
        excelExporter.exportExcel(response, sysJobLogVos, SysJobLogVo.class, "定时任务日志列表");
    }
}
