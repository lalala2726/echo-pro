package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.model.entity.Option;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.converter.SysDeptConverter;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.dept.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.dept.SysDeptListRequest;
import cn.zhangchuangla.system.model.request.dept.SysDeptRequest;
import cn.zhangchuangla.system.model.vo.dept.SysDeptListVo;
import cn.zhangchuangla.system.model.vo.dept.SysDeptVo;
import cn.zhangchuangla.system.service.SysDeptService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PostAuthorize;
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
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController extends BaseController {

    private final SysDeptService sysDeptService;
    private final SysDeptConverter sysDeptConverter;

    /**
     * 获取部门列表信息
     *
     * @param request 部门列表查询参数
     * @return 部门列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:dept:list')")
    @Operation(summary = "部门列表")
    public AjaxResult listDept(@Parameter(description = "部门列表查询参数")
                               @Validated @ParameterObject SysDeptListRequest request) {
        Page<SysDept> page = sysDeptService.listDept(request);
        List<SysDeptListVo> sysDeptListVos = copyListProperties(page, SysDeptListVo.class);
        return getTableData(page, sysDeptListVos);
    }


    /**
     * 新增部门
     *
     * @param request 部门添加请求参数
     * @return 操作结果
     */
    @PostMapping
    @PostAuthorize("@ss.hasPermission('system:dept:add')")
    @Operation(summary = "新增部门")
    @OperationLog(title = "部门管理", businessType = BusinessType.INSERT)
    public AjaxResult addDept(@Parameter(description = "部门添加请求参数") @Validated @RequestBody SysDeptAddRequest request) {
        if (request.getParentId() != null) {
            checkParam(sysDeptService.getById(request.getParentId()) == null, "父部门不存在！");
        }
        return toAjax(sysDeptService.addDept(request));
    }

    /**
     * 修改部门
     *
     * @param request 部门修改请求参数
     * @return 操作结果
     */
    @PutMapping
    @PostAuthorize("@ss.hasPermission('system:dept:edit')")
    @Operation(summary = "修改部门")
    @OperationLog(title = "部门管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateDept(@Parameter(description = "部门修改请求参数") @Validated @RequestBody SysDeptRequest request) {
        return toAjax(sysDeptService.updateDept(request));
    }

    /**
     * 根据部门ID获取部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    @PostAuthorize("@ss.hasPermission('system:dept:query')")
    @Operation(summary = "获取部门信息")
    public AjaxResult getDeptById(@Parameter(description = "部门ID") @PathVariable Long id) {
        checkParam(id == null, "部门ID不能为空！");
        SysDept dept = sysDeptService.getDeptById(id);
        SysDeptVo sysDeptVo = sysDeptConverter.toSysDeptVo(dept);
        return success(sysDeptVo);
    }

    /**
     * 部门下拉列表
     *
     * @return 部门下拉列表
     */
    @GetMapping("/options")
    @PreAuthorize("@ss.hasPermission('system:dept:tree')")
    @Operation(summary = "部门下拉列表")
    public AjaxResult treeDept() {
        List<Option<Long>> deptOptions = sysDeptService.getDeptOptions();
        return success(deptOptions);
    }

    /**
     * 删除部门,支持批量删除
     *
     * @param ids 部门ID集合，支持批量删除
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @OperationLog(title = "部门管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门")
    @PostAuthorize("@ss.hasPermission('system:dept:remove')")
    public AjaxResult removeDept(
            @Parameter(description = "部门ID集合，支持批量删除，批量删除时其中一个删除失败全部将会失败")
            @PathVariable List<Long> ids) {
        checkParam(ids == null, "部门ID不能为空！");
        boolean result = sysDeptService.removeDeptById(ids);
        return toAjax(result);
    }
}
