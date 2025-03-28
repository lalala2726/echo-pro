package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.annotation.Log;
import cn.zhangchuangla.common.constant.SystemMessageConstant;
import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.common.enums.BusinessType;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.ParamsUtils;
import cn.zhangchuangla.infrastructure.annotation.Anonymous;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.role.SysRoleAddRequest;
import cn.zhangchuangla.system.model.request.role.SysRoleQueryRequest;
import cn.zhangchuangla.system.model.vo.permission.SysRoleVo;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "角色接口")
@Anonymous
public class SysRoleController extends BaseController {

    @Resource
    private SysRoleService sysRoleService;


    /**
     * 角色列表
     *
     * @param request 请求参数
     * @return 分页列表
     */

    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    @PreAuthorize("@auth.hasPermission('system:role:list')")
    public TableDataResult list(@Parameter(name = "角色查询参数") @Validated SysRoleQueryRequest request) {
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
    @PreAuthorize("@auth.hasPermission('system:role:info')")
    public AjaxResult getRoleInfoById(@Parameter(name = "角色ID", required = true)
                                      @PathVariable("id") Long id) {
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
    @PreAuthorize("@auth.hasPermission('system:role:delete')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    public AjaxResult deleteRoleInfo(@Parameter(name = "角色ID", required = true) @PathVariable("id") Long id) {
        if (sysRoleService.removeById(id)) {
            return success(SystemMessageConstant.DELETE_SUCCESS);
        }
        return error(SystemMessageConstant.DELETE_FAIL);
    }

    /**
     * 修改角色信息
     * <p>
     * //todo 当修改用户信息需要将角色关系表中数据进行同步
     *
     * @param sysRoleVo 修改角色信息
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改角色信息")
    @PreAuthorize("@auth.hasPermission('system:role:update')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public AjaxResult updateRoleInfo(@Parameter(name = "修改角色信息", required = true, description = "其中角色ID是必填项,其他参数是修改后的结果")
                                     @Validated @RequestBody SysRoleVo sysRoleVo) {
        if (sysRoleVo == null || sysRoleVo.getRoleId() == null) {
            return AjaxResult.error(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleVo, sysRole);
        if (sysRoleService.updateById(sysRole)) {
            return success(SystemMessageConstant.UPDATE_SUCCESS);
        }
        return error(SystemMessageConstant.UPDATE_FAIL);
    }

    /**
     * 添加角色信息
     *
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加角色信息")
    @PreAuthorize("@auth.hasPermission('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    public AjaxResult addRoleInfo(@Parameter(name = "角色名称", required = true)
                                  @Validated @RequestBody SysRoleAddRequest roleAddRequest) {
        boolean roleNameExist = sysRoleService.isRoleNameExist(roleAddRequest.getRoleName());
        ParamsUtils.paramCheck(roleNameExist, "角色名已存在");
        boolean roleKeyExist = sysRoleService.isRoleKeyExist(roleAddRequest.getRoleKey());
        ParamsUtils.paramCheck(roleKeyExist, "角色权限字符串已存在");
        sysRoleService.addRoleInfo(roleAddRequest);
        return AjaxResult.success();
    }

}
