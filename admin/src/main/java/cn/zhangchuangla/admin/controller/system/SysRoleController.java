package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.SysRoleQueryRequest;
import cn.zhangchuangla.system.model.vo.SysRoleVo;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @author Chuang
 * <p>
 * created on 2025/1/12 10:55
 */
@RestController
@RequestMapping("/admin/role")
@Tag(name = "角色接口")
public class SysRoleController {

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
    public AjaxResult list(@Parameter(name = "角色查询参数") SysRoleQueryRequest request) {
        PageUtils.checkPageParams(request.getPageNum(), request.getPageSize());
        Page<SysRole> page = sysRoleService.RoleList(request);
        ArrayList<SysRoleVo> sysRoleVos = new ArrayList<>();
        page.getRecords().forEach(sysRole -> {
            SysRoleVo sysRoleVo = new SysRoleVo();
            BeanUtils.copyProperties(sysRole, sysRoleVo);
            sysRoleVos.add(sysRoleVo);
        });

        return AjaxResult.table(page, sysRoleVos);
    }

    /**
     * 根据id获取角色信息
     *
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id获取角色信息")
    public AjaxResult getRoleInfoById(@Parameter(name = "角色ID", required = true)
                                      @PathVariable("id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "角色不存在");
        }
        SysRoleVo sysRoleVo = new SysRoleVo();
        BeanUtils.copyProperties(sysRole, sysRoleVo);
        return AjaxResult.success(sysRoleVo);
    }

    /**
     * 删除角色信息
     *
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色信息")
    public AjaxResult deleteRoleInfo(@Parameter(name = "角色ID", required = true) @PathVariable("id") Long id) {
        if (sysRoleService.removeById(id)) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "删除失败");
    }

    /**
     * 添加角色信息
     *
     * @param name 角色名称
     * @return 添加结果
     */
    @PostMapping
    @Operation(summary = "添加角色信息")
    public AjaxResult addRoleInfo(@Parameter(name = "角色名称", required = true)
                                  @RequestBody String name) {
        if (name == null || name.isEmpty()) {
            return AjaxResult.error(ResponseCode.PARAM_ERROR, "角色名称不能为空");
        }
        SysRole sysRole = new SysRole();
        sysRole.setRoleName(name);
        if (sysRoleService.save(sysRole)) {
            return AjaxResult.success("添加成功");
        }
        return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "添加失败");
    }

    /**
     * 修改角色信息
     *
     * @param sysRoleVo 修改角色信息
     * @return 修改结果
     */
    @PutMapping
    @Operation(summary = "修改角色信息")
    public AjaxResult updateRoleInfo(@Parameter(name = "修改角色信息", required = true, description = "其中角色ID是必填项,其他参数是修改后的结果")
                                     @RequestBody SysRoleVo sysRoleVo) {
        if (sysRoleVo == null || sysRoleVo.getId() == null) {
            return AjaxResult.error(ResponseCode.PARAM_ERROR, "角色ID不能为空");
        }
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleVo, sysRole);
        if (sysRoleService.updateById(sysRole)) {
            return AjaxResult.success("修改成功");
        }
        return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "修改失败");
    }

}
