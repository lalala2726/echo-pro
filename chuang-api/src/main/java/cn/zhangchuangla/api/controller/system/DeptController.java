package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.core.utils.BeanCotyUtils;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysDept;
import cn.zhangchuangla.system.core.model.request.dept.SysDeptAddRequest;
import cn.zhangchuangla.system.core.model.request.dept.SysDeptQueryRequest;
import cn.zhangchuangla.system.core.model.request.dept.SysDeptUpdateRequest;
import cn.zhangchuangla.system.core.model.vo.dept.SysDeptListVo;
import cn.zhangchuangla.system.core.model.vo.dept.SysDeptVo;
import cn.zhangchuangla.system.core.service.SysDeptService;
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
 * 部门管理控制器
 *
 * @author Chuang
 * created on 2025/3/25 10:57
 */
@Tag(name = "部门管理", description = "提供部门的新增、删除、修改、查询等管理功能接口")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class DeptController extends BaseController {

    private final SysDeptService sysDeptService;

    /**
     * 获取部门列表信息
     *
     * @param request 部门列表查询参数
     * @return 部门列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:dept:list')")
    @Operation(summary = "部门列表")
    public AjaxResult<List<SysDeptListVo>> listDept(@Parameter(description = "部门列表查询参数")
                                                    @Validated @ParameterObject SysDeptQueryRequest request) {
        List<SysDept> sysDept = sysDeptService.listDept(request);
        List<SysDeptListVo> sysDeptListVos = copyListProperties(sysDept, SysDeptListVo.class);
        return success(sysDeptListVos);
    }


    /**
     * 新增部门
     *
     * @param request 部门添加请求参数
     * @return 操作结果
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermission('system:dept:add')")
    @Operation(summary = "新增部门")
    @OperationLog(title = "部门管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addDept(@Parameter(description = "部门添加请求参数")
                                    @Validated @RequestBody SysDeptAddRequest request) {
        return toAjax(sysDeptService.addDept(request));
    }

    /**
     * 修改部门
     *
     * @param request 部门修改请求参数
     * @return 操作结果
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermission('system:dept:update')")
    @Operation(summary = "修改部门")
    @OperationLog(title = "部门管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateDept(@Parameter(description = "部门修改请求参数")
                                       @Validated @RequestBody SysDeptUpdateRequest request) {
        return toAjax(sysDeptService.updateDept(request));
    }

    /**
     * 根据部门ID获取部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("@ss.hasPermission('system:dept:query')")
    @Operation(summary = "获取部门信息")
    public AjaxResult<SysDeptVo> getDeptById(@Parameter(description = "部门ID") @PathVariable("id") Long id) {
        Assert.notNull(id, "部门ID不能为空！");
        Assert.isTrue(id > 0, "部门ID必须大于0！");
        SysDept dept = sysDeptService.getDeptById(id);
        SysDeptVo sysDeptVo = BeanCotyUtils.copyProperties(dept, SysDeptVo.class);
        return success(sysDeptVo);
    }

    /**
     * 部门下拉列表
     *
     * @return 部门下拉列表
     */
    @GetMapping("/options")
    @PreAuthorize("@ss.hasPermission('system:dept:list')")
    @Operation(summary = "部门下拉列表")
    public AjaxResult<List<Option<Long>>> options() {
        List<Option<Long>> deptOptions = sysDeptService.getDeptOptions();
        return success(deptOptions);
    }

    @GetMapping("/tree")
    @PreAuthorize("@ss.hasPermission('system:dept:list')")
    @Operation(summary = "部门树")
    public AjaxResult<List<Option<Long>>> treeDept() {
        List<Option<Long>> deptOptions = sysDeptService.getDeptTreeOptions();
        return success(deptOptions);
    }


    /**
     * 删除部门,支持批量删除
     *
     * @param ids 部门ID集合，支持批量删除
     * @return 操作结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @OperationLog(title = "部门管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门")
    @PreAuthorize("@ss.hasPermission('system:dept:delete')")
    public AjaxResult<Void> deleteDept(
            @Parameter(description = "部门ID集合，支持批量删除，批量删除时其中一个删除失败全部将会失败")
            @PathVariable List<Long> ids) {
        Assert.notEmpty(ids, "部门ID不能为空！");
        Assert.isTrue(ids.stream().allMatch(id -> id > 0), "部门ID必须大于0！");
        boolean result = sysDeptService.deleteDeptById(ids);
        return toAjax(result);
    }
}
