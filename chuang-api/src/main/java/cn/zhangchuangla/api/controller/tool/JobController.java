package cn.zhangchuangla.api.controller.tool;

import cn.zhangchuangla.common.core.base.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.quartz.entity.SysJob;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobBatchRequest;
import cn.zhangchuangla.quartz.model.request.SysJobQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobListVo;
import cn.zhangchuangla.quartz.model.vo.SysJobVo;
import cn.zhangchuangla.quartz.service.SysJobService;
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
 * 定时任务控制器
 *
 * @author Chuang
 */
@RestController
@RequestMapping("/tool/job")
@RequiredArgsConstructor
@Tag(name = "定时任务管理", description = "定时任务的增删改查、启停控制等功能")
public class JobController extends BaseController {

    private final SysJobService sysJobService;
    private final ExcelExporter excelExporter;

    /**
     * 获取定时任务列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取定时任务列表")
    @PreAuthorize("@ss.hasPermission('tool:job:list')")
    public AjaxResult<TableDataResult> list(@Parameter(description = "定时任务查询参数")
                                            @Validated @ParameterObject SysJobQueryRequest request) {
        Page<SysJob> page = sysJobService.selectJobList(request);
        List<SysJobListVo> sysJobVos = copyListProperties(page, SysJobListVo.class);
        return getTableData(page, sysJobVos);
    }

    /**
     * 获取定时任务详情
     */
    @GetMapping("/{jobId:\\d+}")
    @Operation(summary = "获取定时任务详情")
    @PreAuthorize("@ss.hasPermission('tool:job:query')")
    public AjaxResult<SysJobVo> getInfo(@Parameter(description = "任务ID") @PathVariable("jobId") Long jobId) {
        Assert.notNull(jobId, "任务ID不能为空");
        SysJobVo jobVo = sysJobService.selectJobById(jobId);
        return success(jobVo);
    }

    /**
     * 新增定时任务
     */
    @PostMapping
    @Operation(summary = "新增定时任务")
    @PreAuthorize("@ss.hasPermission('tool:job:add')")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> add(@Parameter(description = "定时任务信息") @Validated @RequestBody SysJobAddRequest request) {
        boolean result = sysJobService.addJob(request);
        return result ? success("新增成功") : error("新增失败");
    }

    /**
     * 修改定时任务
     */
    @PutMapping
    @Operation(summary = "修改定时任务")
    @PreAuthorize("@ss.hasPermission('tool:job:update')")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> edit(@Parameter(description = "定时任务信息") @Validated @RequestBody SysJobUpdateRequest request) {
        boolean result = sysJobService.updateJob(request);
        return result ? success("修改成功") : error("修改失败");
    }

    /**
     * 删除定时任务
     */
    @DeleteMapping("/{jobIds:\\d+}")
    @Operation(summary = "删除定时任务")
    @PreAuthorize("@ss.hasPermission('tool:job:delete')")
    @OperationLog(title = "定时任务", businessType = BusinessType.DELETE)
    public AjaxResult<Void> remove(@Parameter(description = "任务ID列表，多个用逗号分隔") @PathVariable("jobIds") List<Long> jobIds) {
        Assert.notEmpty(jobIds, "任务ID不能为空");
        boolean result = sysJobService.deleteJobs(jobIds);
        return result ? success("删除成功") : error("删除失败");
    }

    /**
     * 启动任务
     */
    @PostMapping("/start/{jobId:\\d+}")
    @Operation(summary = "启动任务")
    @PreAuthorize("@ss.hasPermission('tool:job:exec')")
    @OperationLog(title = "定时任务", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> start(@Parameter(description = "任务ID") @PathVariable("jobId") Long jobId) {
        Assert.notNull(jobId, "任务ID不能为空");
        boolean result = sysJobService.startJob(jobId);
        return result ? success("启动成功") : error("启动失败");
    }

    /**
     * 暂停任务
     */
    @PostMapping("/pause/{jobId:\\d+}")
    @Operation(summary = "暂停任务")
    @PreAuthorize("@ss.hasPermission('tool:job:pause')")
    @OperationLog(title = "定时任务", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> pause(@Parameter(description = "任务ID") @PathVariable("jobId") Long jobId) {
        Assert.notNull(jobId, "任务ID不能为空");
        boolean result = sysJobService.pauseJob(jobId);
        return result ? success("暂停成功") : error("暂停失败");
    }

    /**
     * 恢复任务
     */
    @PostMapping("/resume/{jobId:\\d+}")
    @Operation(summary = "恢复任务")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermission('tool:job:resume')")
    public AjaxResult<Void> resume(@Parameter(description = "任务ID") @PathVariable("jobId") Long jobId) {
        Assert.notNull(jobId, "任务ID不能为空");
        boolean result = sysJobService.resumeJob(jobId);
        return result ? success("恢复成功") : error("恢复失败");
    }

    /**
     * 立即执行任务
     */
    @PostMapping("/run/{jobId:\\d+}")
    @Operation(summary = "立即执行任务")
    @PreAuthorize("@ss.hasPermission('tool:job:run')")
    @OperationLog(title = "定时任务", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> run(@Parameter(description = "任务ID") @PathVariable("jobId") Long jobId) {
        Assert.notNull(jobId, "任务ID不能为空");
        boolean result = sysJobService.runJob(jobId);
        return result ? success("执行成功") : error("执行失败");
    }

    /**
     * 批量操作任务
     */
    @PostMapping("/batch")
    @Operation(summary = "批量操作任务")
    @PreAuthorize("@ss.hasPermission('tool:job:batch')")
    @OperationLog(title = "定时任务", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> batchOperate(@Parameter(description = "批量操作参数") @Validated @RequestBody SysJobBatchRequest request) {
        boolean result = sysJobService.batchOperateJobs(request);
        return result ? success("操作成功") : error("操作失败");
    }

    /**
     * 刷新任务状态
     */
    @PostMapping("/refresh/{jobId:\\d+}")
    @Operation(summary = "刷新任务状态")
    @PreAuthorize("@ss.hasPermission('tool:job:refresh')")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.REFRESH)
    public AjaxResult<Void> refresh(@Parameter(description = "任务ID") @PathVariable Long jobId) {
        Assert.notNull(jobId, "任务ID不能为空");
        sysJobService.refreshJobStatus(jobId);
        return success("刷新成功");
    }

    /**
     * 导出定时任务
     */
    @PostMapping("/export")
    @Operation(summary = "导出定时任务")
    @PreAuthorize("@ss.hasPermission('tool:job:export')")
    @OperationLog(title = "定时任务", businessType = BusinessType.EXPORT)
    public void exportJob(@Parameter(description = "定时任务导出查询参数") @RequestBody(required = false) SysJobQueryRequest request,
                          HttpServletResponse response) {
        List<SysJob> sysJobPage = sysJobService.exportJobList(request);
        List<SysJobVo> sysJobVos = copyListProperties(sysJobPage, SysJobVo.class);
        excelExporter.exportExcel(response, sysJobVos, SysJobVo.class, "定时任务列表");
    }
}
