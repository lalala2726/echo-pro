package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.entity.Option;
import cn.zhangchuangla.common.core.entity.base.AjaxResult;
import cn.zhangchuangla.common.core.entity.base.TableDataResult;
import cn.zhangchuangla.common.core.enums.BusinessType;
import cn.zhangchuangla.common.core.utils.Assert;
import cn.zhangchuangla.common.excel.utils.ExcelExporter;
import cn.zhangchuangla.framework.annotation.OperationLog;
import cn.zhangchuangla.system.core.model.entity.SysRole;
import cn.zhangchuangla.system.core.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.core.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.core.model.request.role.SysRoleUpdateRequest;
import cn.zhangchuangla.system.core.model.request.role.SysUpdateRolePermissionRequest;
import cn.zhangchuangla.system.core.model.vo.role.SysRoleListVo;
import cn.zhangchuangla.system.core.model.vo.role.SysRolePermissionVo;
import cn.zhangchuangla.system.core.model.vo.role.SysRoleVo;
import cn.zhangchuangla.system.core.service.SysPermissionService;
import cn.zhangchuangla.system.core.service.SysRoleService;
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
 * 当后续因为业务需求改变需要接入到微服务架构,这边建议使用Redis单独存储角色和权限信息,从而实现权限实时刷新
 *
 * @author Chuang
 * <p>
 * created on 2025/1/12 10:55
 */
@Slf4j
@RestController
@RequestMapping("/system/role")
@Tag(name = "角色管理", description = "提供角色的新增、删除、修改、查询、权限分配、角色选项等管理接口")
@RequiredArgsConstructor
public class RoleController extends BaseController {

    private final SysRoleService sysRoleService;
    private final ExcelExporter excelExporter;
    private final SysPermissionService sysPermissionService;


    /**
     * 获取角色列表
     *
     * @param request 角色列表查询参数
     * @return 分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    @PreAuthorize("@ss.hasPermission('system:role:list')")
    public AjaxResult<TableDataResult> roleList(@Parameter(description = "角色列表查询参数")
                                                @Validated @ParameterObject SysRoleQueryRequest request) {
        Page<SysRole> page = sysRoleService.roleList(request);
        List<SysRoleListVo> sysRoleListVos = copyListProperties(page, SysRoleListVo.class);
        return getTableData(page, sysRoleListVos);
    }

    /**
     * 导出用户列表
     * 根据查询条件导出系统用户列表
     *
     * @param request 包含分页、排序和筛选条件的用户查询参数
     */
    @PostMapping("/export")
    @Operation(summary = "导出用户列表")
    @PreAuthorize("@ss.hasPermission('system:user:export')")
    @OperationLog(title = "角色管理", businessType = BusinessType.EXPORT)
    public void exportExcel(HttpServletResponse response,
                            @Parameter(description = "用户查询参数，包含分页和筛选条件")
                            @RequestBody SysRoleQueryRequest request) {
        log.info("导出用户列表:{}", request);
        Page<SysRole> page = sysRoleService.roleList(request);
        List<SysRoleListVo> sysRoleListVos = copyListProperties(page, SysRoleListVo.class);
        excelExporter.exportExcel(response, sysRoleListVos, SysRoleListVo.class, "角色列表");

    }


    /**
     * 获取角色权限分配信息
     *
     * @param roleId 角色ID
     * @return 角色权限分配信息
     */
    @GetMapping("/permission/{roleId:\\d+}")
    @Operation(summary = "获取角色权限分配信息")
    @PreAuthorize("@ss.hasPermission('system:menu:query')")
    public AjaxResult<SysRolePermissionVo> getPermissionByRoleId(@PathVariable("roleId") Long roleId) {
        SysRolePermissionVo permission = sysPermissionService.getPermissionByRoleId(roleId);
        return success(permission);
    }

    /**
     * 更新角色权限
     *
     * @param request 角色权限更新请求
     * @return 角色权限
     */
    @PutMapping("/permission")
    @Operation(summary = "更新角色权限信息")
    @PreAuthorize("@ss.hasPermission('system:role:permission')")
    @OperationLog(title = "角色权限管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateRolePermission(@RequestBody SysUpdateRolePermissionRequest request) {
        boolean result = sysRoleService.updateRolePermission(request);
        return toAjax(result);
    }

    /**
     * 获取角色选项
     *
     * @return 角色选项列表
     */
    @GetMapping("/options")
    @Operation(summary = "获取角色选项")
    @PreAuthorize("@ss.hasPermission('system:role:options')")
    public AjaxResult<List<Option<Long>>> roleOptions() {
        List<Option<Long>> options = sysRoleService.getRoleOptions();
        return success(options);
    }


    /**
     * 根据id获取角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id:\\d+}")
    @Operation(summary = "根据id获取角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:info')")
    public AjaxResult<SysRoleVo> getRoleInfoById(@Parameter(description = "角色ID") @PathVariable("id") Long id) {
        SysRole sysRole = sysRoleService.getRoleInfoById(id);
        SysRoleVo sysRoleVo = new SysRoleVo();
        BeanUtils.copyProperties(sysRole, sysRoleVo);
        return success(sysRoleVo);
    }

    /**
     * 删除角色信息,支持批量删除
     *
     * @param ids 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{ids:[\\d,]+}")
    @Operation(summary = "删除角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    @OperationLog(title = "角色管理", businessType = BusinessType.DELETE)
    public AjaxResult<Void> deleteRoleInfo(@Parameter(description = "角色ID") @PathVariable("ids") List<Long> ids) {
        Assert.isTrue(!ids.isEmpty(), "角色ID不能为空");
        boolean result = sysRoleService.deleteRoleInfo(ids);
        return toAjax(result);
    }

    /**
     * 修改角色信息
     *
     * @param request 修改角色信息请求参数
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    @OperationLog(title = "角色管理", businessType = BusinessType.UPDATE)
    public AjaxResult<Void> updateRoleInfo(@Parameter(description = "修改角色信息请求参数")
                                           @Validated @RequestBody SysRoleUpdateRequest request) {
        boolean result = sysRoleService.updateRoleInfo(request);
        return toAjax(result);
    }

    /**
     * 添加角色信息
     *
     * @param roleAddRequest 添加角色请求参数
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:add')")
    @OperationLog(title = "角色管理", businessType = BusinessType.INSERT)
    public AjaxResult<Void> addRoleInfo(@Parameter(description = "添加角色请求参数")
                                        @Validated @RequestBody SysRoleAddRequest roleAddRequest) {
        if (sysRoleService.isRoleKeyExist(roleAddRequest.getRoleName())) {
            return error("角色标识符已经存在");
        }
        if (sysRoleService.isRoleNameExist(roleAddRequest.getRoleKey())) {
            return error("角色名称已经存在");
        }
        boolean result = sysRoleService.addRoleInfo(roleAddRequest);
        return toAjax(result);
    }

}
