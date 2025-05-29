package cn.zhangchuangla.api.controller.tool;

import cn.zhangchuangla.common.core.core.controller.BaseController;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.result.AjaxResult;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.framework.annotation.RequiresSecondAuth;
import cn.zhangchuangla.quartz.model.entity.SysJob;
import cn.zhangchuangla.quartz.model.entity.SysJobLog;
import cn.zhangchuangla.quartz.model.request.SysJobAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobListQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobLogListQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobListVo;
import cn.zhangchuangla.quartz.model.vo.SysJobLogListVo;
import cn.zhangchuangla.quartz.model.vo.SysJobVo;
import cn.zhangchuangla.quartz.service.SysJobLogService;
import cn.zhangchuangla.quartz.service.SysJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务调度控制器
 *
 * @author Chuang
 * create on  2025-01-27
 */
@Slf4j
@RestController
@RequestMapping("/tool/job")
@RequiredArgsConstructor
@Tag(name = "定时任务管理", description = "定时任务调度相关接口")
public class JobController extends BaseController {

    private final SysJobLogService sysJobLogService;
    private final SysJobService sysJobService;

    /**
     * 查询定时任务列表
     *
     * @param request 查询参数
     * @return 定时任务列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('tool:job:list')")
    @Operation(summary = "查询定时任务列表")
    public AjaxResult<List<SysJobListVo>> listJobs(@Parameter @ParameterObject SysJobListQueryRequest request) {
        List<SysJob> jobList = sysJobService.listJobs(request);
        List<SysJobListVo> sysJobListVos = copyListProperties(jobList, SysJobListVo.class);
        return success(sysJobListVos);
    }

    /**
     * 查询定时任务详情
     *
     * @param id 定时任务ID
     * @return 定时任务详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('tool:job:query')")
    @Operation(summary = "查询定时任务详情")
    public AjaxResult<SysJobVo> getJobById(@PathVariable("id") Long id) {
        SysJob job = sysJobService.getJobById(id);
        SysJobVo sysJobVo = copyProperties(job, SysJobVo.class);
        return success(sysJobVo);
    }

    /**
     * 添加定时任务
     *
     * @param request 添加参数
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加定时任务")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermission('tool:job:add')")
    public AjaxResult<Void> addJob(@RequestBody @Validated SysJobAddRequest request) {
        boolean result = sysJobService.addJob(request);
        return toAjax(result);
    }

    /**
     * 修改定时任务
     *
     * @param request 请求参数
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改定时任务")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateJob(@RequestBody SysJobUpdateRequest request) {
        boolean result = sysJobService.updateJob(request);
        return toAjax(result);
    }

    /**
     * 删除定时任务
     *
     * @param ids 定时任务ID
     * @return 删除结果
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermission('tool:job:delete')")
    @Operation(summary = "删除定时任务")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteJob(@PathVariable("ids") List<Long> ids) {
        checkParam(ids == null || ids.isEmpty(), "定时任务ID不能为空!");
        boolean result = sysJobService.deleteJob(ids);
        return toAjax(result);
    }

    /**
     * 立即执行定时任务
     *
     * @param id 定时任务ID
     * @return 执行结果
     */
    @PostMapping("/{id}")
    @PreAuthorize("@ss.hasPermission('tool:job:run')")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.OTHER)
    @Operation(summary = "立即执行定时任务")
    public AjaxResult<Void> runJob(@PathVariable("id") Long id) {
        boolean result = sysJobService.runJob(id);
        return toAjax(result);
    }

    /**
     * 暂停定时任务
     *
     * @param id 定时任务ID
     * @return 操作结果
     */
    @PutMapping("/pause/{id}")
    @PreAuthorize("@ss.hasPermission('tool:job:changeStatus')")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "暂停定时任务")
    public AjaxResult<Void> pauseJob(@PathVariable("id") Long id) {
        boolean result = sysJobService.pauseJob(id);
        return toAjax(result);
    }

    /**
     * 恢复定时任务
     *
     * @param id 定时任务ID
     * @return 操作结果
     */
    @PutMapping("/resume/{id}")
    @PreAuthorize("@ss.hasPermission('tool:job:changeStatus')")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.UPDATE)
    @Operation(summary = "恢复定时任务")
    public AjaxResult<Void> resumeJob(@PathVariable("id") Long id) {
        boolean result = sysJobService.resumeJob(id);
        return toAjax(result);
    }

    /**
     * 查询定时任务日志列表
     *
     * @param request 查询参数
     * @return 定时任务日志列表
     */
    @GetMapping("/log/list")
    @PreAuthorize("@ss.hasPermission('tool:job-log:list')")
    @Operation(summary = "查询定时任务日志列表")
    public AjaxResult<List<SysJobLogListVo>> listJobLogs(@Parameter @ParameterObject SysJobLogListQueryRequest request) {
        List<SysJobLog> jobLogList = sysJobLogService.listJobLogs(request);
        List<SysJobLogListVo> sysJobLogListVos = copyListProperties(jobLogList, SysJobLogListVo.class);
        return success(sysJobLogListVos);
    }

    /**
     * 查询定时任务日志详情
     *
     * @param id 定时任务日志ID
     * @return 定时任务日志详情
     */
    @GetMapping("/log/{id}")
    @PreAuthorize("@ss.hasPermission('tool:job-log:query')")
    @Operation(summary = "查询定时任务日志详情")
    public AjaxResult<SysJobLogListVo> getJobLogById(@PathVariable("id") Long id) {
        SysJobLog job = sysJobLogService.getJobLogById(id);
        SysJobLogListVo sysJobLogListVo = copyProperties(job, SysJobLogListVo.class);
        return success(sysJobLogListVo);
    }

    /**
     * 删除定时任务日志
     *
     * @param ids 定时任务日志ID
     * @return 删除结果
     */
    @DeleteMapping("/log/{ids}")
    @PreAuthorize("@ss.hasPermission('tool:job-log:delete')")
    @Operation(summary = "删除定时任务日志")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteJobLog(@PathVariable("ids") List<Long> ids) {
        checkParam(ids == null || ids.isEmpty(), "定时任务日志ID不能为空!");
        boolean result = sysJobLogService.deleteJobLog(ids);
        return toAjax(result);
    }

    /**
     * 清空定时任务日志
     *
     * @return 清空结果
     */
    @GetMapping("/log/clean")
    @RequiresSecondAuth
    @PreAuthorize("@ss.hasPermission('tool:job-log:clean')")
    @Operation(summary = "清除定时任务日志")
    @OperationLog(title = "定时任务管理", businessType = BusinessType.CLEAN)
    public AjaxResult<Void> cleanJobLog() {
        sysJobLogService.cleanJobLog();
        return success();
    }

    /**
     * 导出定时任务日志
     *
     * @param request 请求参数
     */
    @GetMapping("/log/export")
    @PreAuthorize("@ss.hasPermission('tool:job-log:export')")
    @Operation(summary = "导出定时任务日志")
    public void exportJobLog(@Parameter @ParameterObject SysJobLogListQueryRequest request) {
    }
}
