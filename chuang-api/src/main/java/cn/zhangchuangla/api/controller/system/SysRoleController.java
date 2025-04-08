package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.constant.SystemMessageConstant;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.infrastructure.annotation.OperationLog;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleUpdateRequest;
import cn.zhangchuangla.system.model.vo.permission.SysRoleVo;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "角色接口")
@Anonymous
public class SysRoleController extends BaseController {

    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }


    /**
     * 角色列表
     *
     * @param request 请求参数
     * @return 分页列表
     */

    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    @PreAuthorize("@ss.hasPermission('system:role:list')")
    public TableDataResult list(@Validated SysRoleQueryRequest request) {
        Page<SysRole> page = sysRoleService.RoleList(request);
        List<SysRoleVo> sysRoleVos = copyListProperties(page, SysRoleVo.class);
        return getTableData(page, sysRoleVos);
    }


    /**
     * 根据id获取角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:info')")
    public AjaxResult getRoleInfoById(@PathVariable("id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "角色不存在");
        }
        SysRoleVo sysRoleVo = new SysRoleVo();
        BeanUtils.copyProperties(sysRole, sysRoleVo);
        return success(sysRoleVo);
    }

    /**
     * 删除角色信息
     * <p>
     *
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:delete')")
    @OperationLog(title = "角色管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteRoleInfo(@PathVariable("id") Long id) {
        if (sysRoleService.removeById(id)) {
            return success(SystemMessageConstant.DELETE_SUCCESS);
        }
        return error(SystemMessageConstant.DELETE_FAIL);
    }

    /**
     * 修改角色信息
     * <p>
     *
     * @param request 修改角色信息
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:update')")
    @OperationLog(title = "角色管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateRoleInfo(@Validated @RequestBody SysRoleUpdateRequest request) {
        boolean result = sysRoleService.updateRoleInfo(request);
        return toAjax(result);
    }

    /**
     * 添加角色信息
     *
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加角色信息")
    @PreAuthorize("@ss.hasPermission('system:role:add')")
    @OperationLog(title = "角色管理", businessType = BusinessType.INSERT)
    public AjaxResult addRoleInfo(@Validated @RequestBody SysRoleAddRequest roleAddRequest) {
        sysRoleService.addRoleInfo(roleAddRequest);
        return AjaxResult.success();
    }

}
