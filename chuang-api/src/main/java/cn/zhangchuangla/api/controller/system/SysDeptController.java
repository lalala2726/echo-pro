package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.system.model.entity.SysDept;
import cn.zhangchuangla.system.model.request.department.SysDeptAddRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptListRequest;
import cn.zhangchuangla.system.model.request.department.SysDeptRequest;
import cn.zhangchuangla.system.model.vo.department.SysDeptListVo;
import cn.zhangchuangla.system.model.vo.department.SysDeptVo;
import cn.zhangchuangla.system.service.SysDeptService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/system/department")
public class SysDeptController extends BaseController {

    private final SysDeptService sysDeptService;

    public SysDeptController(SysDeptService sysDeptService) {
        this.sysDeptService = sysDeptService;
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
    public TableDataResult listDept(SysDeptListRequest request) {
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
    @PostAuthorize("@auth.hasPermission('system:department:add')")
    @Operation(summary = "新增部门")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    public AjaxResult addDept(@Validated @RequestBody SysDeptAddRequest request) {
        checkParam(sysDeptService.isDeptNameExist(request.getName()), "部门名称已存在！");
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
    @PostAuthorize("@auth.hasPermission('system:department:edit')")
    @Operation(summary = "修改部门")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateDept(@Validated @RequestBody SysDeptRequest request) {
        checkParam(sysDeptService.isDeptNameExist(request.getName()), "部门名称已存在！");
        return toAjax(sysDeptService.updateDept(request));
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
    public AjaxResult getDeptById(@PathVariable Integer id) {
        checkParam(id == null, "部门ID不能为空！");
        SysDept dept = sysDeptService.getDeptById(id);
        SysDeptVo sysDeptVo = new SysDeptVo();
        BeanUtils.copyProperties(dept, sysDeptVo);
        return success(sysDeptVo);
    }

    /**
     * 删除部门,支持批量删除
     *
     * @param ids 部门id
     * @return 操作结果
     */
    @DeleteMapping("/{ids}")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @Operation(summary = "删除部门")
    @PostAuthorize("@auth.hasPermission('system:department:remove')")
    public AjaxResult removeDept(@PathVariable List<Integer> ids) {
        checkParam(ids == null, "部门ID不能为空！");
        if (ids != null) {
            ids.forEach(id -> {
                checkParam(sysDeptService.departmentHasSubordinates(id), "该部门下有子部门，不能删除！");
            });
        }
        return toAjax(sysDeptService.removeByIds(ids));
    }
}
