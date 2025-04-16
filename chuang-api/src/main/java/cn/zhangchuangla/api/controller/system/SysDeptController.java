package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.dept.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.dept.SysDeptListRequest;
import cn.zhangchuangla.system.model.request.dept.SysDeptRequest;
import cn.zhangchuangla.system.model.vo.dept.DeptTree;
import cn.zhangchuangla.system.model.vo.dept.SysDeptListVo;
import cn.zhangchuangla.system.model.vo.dept.SysDeptVo;
import cn.zhangchuangla.system.service.SysDeptService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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


    /**
     * 获取部门列表信息
     * <p>
     *
     * @param request 包含分页等信息的请求对象，用于指定获取哪些部门信息
     * @return 返回一个包含部门列表信息的TableDataResult对象
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermission('system:dept:list')")
    @Operation(summary = "部门列表")
    public AjaxResult listDept(SysDeptListRequest request) {
        Page<SysDept> page = sysDeptService.listDept(request);
        List<SysDeptListVo> sysDeptListVos = copyListProperties(page, SysDeptListVo.class);
        return getTableData(page, sysDeptListVos);
    }

    /**
     * 新增部门
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping
    @PostAuthorize("@ss.hasPermission('system:dept:add')")
    @Operation(summary = "新增部门")
    @OperationLog(title = "部门管理", businessType = BusinessType.INSERT)
    public AjaxResult addDept(@Validated @RequestBody SysDeptAddRequest request) {
        if (request.getParentId() != null) {
            checkParam(sysDeptService.getById(request.getParentId()) == null, "父部门不存在！");
        }
        return toAjax(sysDeptService.addDept(request));
    }

    /**
     * 修改部门
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PutMapping
    @PostAuthorize("@ss.hasPermission('system:dept:edit')")
    @Operation(summary = "修改部门")
    @OperationLog(title = "部门管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateDept(@Validated @RequestBody SysDeptRequest request) {
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
    public AjaxResult getDeptById(@PathVariable Integer id) {
        checkParam(id == null, "部门ID不能为空！");
        SysDept dept = sysDeptService.getDeptById(id);
        SysDeptVo sysDeptVo = new SysDeptVo();
        BeanUtils.copyProperties(dept, sysDeptVo);
        return success(sysDeptVo);
    }

    /**
     * 部门树
     *
     * @return 部门树
     */
    @GetMapping("/tree")
    @PreAuthorize("@ss.hasPermission('system:dept:tree')")
    @Operation(summary = "部门树")
    public AjaxResult treeDept() {
        List<DeptTree> deptList = sysDeptService.buildTree();
        return success(deptList);
    }

    /**
     * 删除部门,支持批量删除
     *
     * @param ids 部门id
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @OperationLog(title = "部门管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门")
    @PostAuthorize("@ss.hasPermission('system:dept:remove')")
    public AjaxResult removeDept(@PathVariable List<Integer> ids) {
        checkParam(ids == null, "部门ID不能为空！");
        boolean result = sysDeptService.removeDeptById(ids);
        return toAjax(result);
    }
}
