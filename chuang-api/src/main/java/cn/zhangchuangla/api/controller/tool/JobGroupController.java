package cn.zhangchuangla.api.controller.tool;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.quartz.entity.SysJobGroup;
import cn.zhangchuangla.quartz.model.request.SysJobGroupAddRequest;
import cn.zhangchuangla.quartz.model.request.SysJobGroupQueryRequest;
import cn.zhangchuangla.quartz.model.request.SysJobGroupUpdateRequest;
import cn.zhangchuangla.quartz.model.vo.SysJobGroupListVo;
import cn.zhangchuangla.quartz.model.vo.SysJobGroupVo;
import cn.zhangchuangla.quartz.service.SysJobGroupService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务组控制器
 *
 * @author Chuang
 */
@RestController
@RequestMapping("/tool/job/group")
@RequiredArgsConstructor
@Tag(name = "定时任务组管理", description = "定时任务组的增删改查等功能")
public class JobGroupController extends BaseController {

    private final SysJobGroupService sysJobGroupService;

    /**
     * 获取定时任务组列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取定时任务组列表")
    @PreAuthorize("@ss.hasPermission('tool:job:list')")
    public AjaxResult<TableDataResult> list(@Parameter(description = "定时任务组查询参数")
                                            @Validated @ParameterObject SysJobGroupQueryRequest request) {
        Page<SysJobGroup> page = sysJobGroupService.selectJobGroupList(request);
        List<SysJobGroupListVo> jobGroupVos = copyListProperties(page, SysJobGroupListVo.class);
        return getTableData(page, jobGroupVos);
    }

    /**
     * 获取定时任务组详情
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取定时任务组详情")
    @PreAuthorize("@ss.hasPermission('tool:job:query')")
    public AjaxResult<SysJobGroupVo> getInfo(@Parameter(description = "任务组ID") @PathVariable("id") Long id) {
        Assert.notNull(id, "任务组ID不能为空");
        SysJobGroupVo jobGroupVo = sysJobGroupService.selectJobGroupById(id);
        return success(jobGroupVo);
    }

    /**
     * 新增定时任务组
     */
    @PostMapping
    @Operation(summary = "新增定时任务组")
    @PreAuthorize("@ss.hasPermission('tool:job:add')")
    public AjaxResult<Void> add(@Parameter(description = "定时任务组信息") @Validated @RequestBody SysJobGroupAddRequest request) {
        boolean result = sysJobGroupService.addJobGroup(request);
        return result ? success("新增成功") : error("新增失败");
    }

    /**
     * 修改定时任务组
     */
    @PutMapping
    @Operation(summary = "修改定时任务组")
    @PreAuthorize("@ss.hasPermission('tool:job:edit')")
    public AjaxResult<Void> edit(@Parameter(description = "定时任务组信息") @Validated @RequestBody SysJobGroupUpdateRequest request) {
        boolean result = sysJobGroupService.updateJobGroup(request);
        return result ? success("修改成功") : error("修改失败");
    }

    /**
     * 删除定时任务组
     */
    @DeleteMapping("/{ids:\\d+}")
    @Operation(summary = "删除定时任务组")
    @PreAuthorize("@ss.hasPermission('tool:job:remove')")
    public AjaxResult<Void> remove(@Parameter(description = "任务组ID列表，多个用逗号分隔") @PathVariable("ids") List<Long> ids) {
        Assert.notEmpty(ids, "任务组ID不能为空");
        boolean result = sysJobGroupService.deleteJobGroups(ids);
        return result ? success("删除成功") : error("删除失败");
    }

}
