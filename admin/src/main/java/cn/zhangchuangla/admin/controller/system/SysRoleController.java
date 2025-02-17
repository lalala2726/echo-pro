package cn.zhangchuangla.admin.controller.system;

import cn.zhangchuangla.system.model.entity.SysRole;
import cn.zhangchuangla.system.model.request.SysRoleQueryRequest;
import cn.zhangchuangla.system.model.vo.SysRoleVo;
import cn.zhangchuangla.common.enums.ResponseCode;
import cn.zhangchuangla.common.result.AjaxResult;
import cn.zhangchuangla.common.utils.PageUtils;
import cn.zhangchuangla.system.service.SysRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public AjaxResult list(SysRoleQueryRequest request) {
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
    public AjaxResult getRoleInfoById(@PathVariable("id") Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "角色不存在");
        }
        SysRoleVo sysRoleVo = new SysRoleVo();
        BeanUtils.copyProperties(sysRole, sysRoleVo);
        return AjaxResult.success(sysRoleVo);
    }

    /**
     * 添加角色信息
     *
     * @param name 角色名称
     * @return 添加结果
     */
    @PostMapping
    public AjaxResult addRoleInfo(@RequestBody String name) {
        if (name == null || name.isEmpty()) {
            return AjaxResult.error(ResponseCode.PARAM_ERROR, "角色名称不能为空");
        }
        SysRole sysRole = new SysRole();
        sysRole.setName(name);
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
    public AjaxResult updateRoleInfo(@RequestBody SysRoleVo sysRoleVo) {
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

    /**
     * 删除角色信息
     *
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult deleteRoleInfo(@PathVariable("id") Long id) {
        if (sysRoleService.removeById(id)) {
            return AjaxResult.success("删除成功");
        }
        return AjaxResult.error(ResponseCode.RESULT_IS_NULL, "删除失败");
    }

}
