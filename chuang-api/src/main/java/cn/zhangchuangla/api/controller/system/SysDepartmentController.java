package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.entity.SysDepartment;
import cn.zhangchuangla.system.model.request.department.SysDepartmentAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDepartmentListRequest;
import cn.zhangchuangla.system.model.request.department.SysDepartmentUpdateRequest;
import cn.zhangchuangla.system.model.vo.department.SysDepartmentListVo;
import cn.zhangchuangla.system.model.vo.department.SysDepartmentVo;
import cn.zhangchuangla.system.service.SysDepartmentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 部门管理控制器
 *
 * @author Chuang
 * created on 2025/3/25 10:57
 */
@RestController
@RequestMapping("/system/department")
public class SysDepartmentController extends BaseController {

    private final SysDepartmentService departmentService;

    public SysDepartmentController(SysDepartmentService departmentService) {
        this.departmentService = departmentService;
    }


    /**
     * 获取部门列表信息
     * <p>
     *
     * @param request 包含分页等信息的请求对象，用于指定获取哪些部门信息
     * @return 返回一个包含部门列表信息的TableDataResult对象
     */
    @GetMapping("/list")
    @PreAuthorize("@auth.hasPermission('system:department:list')")
    @Operation(summary = "部门列表")
    public TableDataResult listDepartment(SysDepartmentListRequest request) {
        Page<SysDepartment> page = departmentService.listDepartment(request);
        ArrayList<SysDepartmentListVo> sysDepartmentListVos = new ArrayList<>();
        page.getRecords().forEach(department -> {
            SysDepartmentListVo sysDepartmentListVo = new SysDepartmentListVo();
            BeanUtils.copyProperties(department, sysDepartmentListVo);
            sysDepartmentListVos.add(sysDepartmentListVo);
        });
        return getTableData(page, sysDepartmentListVos);
    }

    /**
     * 新增部门
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PostMapping
    @PostAuthorize("@auth.hasPermission('system:department:add')")
    @Operation(summary = "新增部门")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    public AjaxResult addDepartment(@Validated SysDepartmentAddRequest request) {
        return toAjax(departmentService.addDepartment(request));
    }

    /**
     * 修改部门
     *
     * @param request 请求参数
     * @return 操作结果
     */
    @PutMapping
    @PostAuthorize("@auth.hasPermission('system:department:edit')")
    @Operation(summary = "修改部门")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    public AjaxResult editDepartment(@Validated SysDepartmentUpdateRequest request) {
        return toAjax(departmentService.updateDepartment(request));
    }

    /**
     * 根据部门ID获取部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    @PostAuthorize("@auth.hasPermission('system:department:query')")
    @Operation(summary = "获取部门信息")
    public AjaxResult getDepartmentById(@PathVariable Integer id) {
        SysDepartment department = departmentService.getDepartmentById(id);
        SysDepartmentVo sysDepartmentVo = new SysDepartmentVo();
        BeanUtils.copyProperties(department, sysDepartmentVo);
        return success(sysDepartmentVo);
    }

    /**
     * 删除部门,支持批量删除
     * @param ids 部门id
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门")
    @PostAuthorize("@auth.hasPermission('system:department:remove')")
    public AjaxResult remove(@PathVariable List<Integer> ids) {
        return toAjax(departmentService.removeByIds(ids));
    }
}
