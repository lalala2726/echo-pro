package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.system.model.entity.SysPermissions;
import cn.zhangchuangla.system.model.request.permissions.SysPermissionsListRequest;
import cn.zhangchuangla.system.model.vo.permissions.SysPermissionsListVo;
import cn.zhangchuangla.system.service.SysPermissionsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Chuang
 * <p>
 * created on 2025/4/11 18:15
 */
@RequestMapping
@RestController("/system/permission")
public class SysPermissionsController extends BaseController {


    private final SysPermissionsService sysPermissionsService;

    public SysPermissionsController(SysPermissionsService sysPermissionsService) {
        this.sysPermissionsService = sysPermissionsService;
    }


    /**
     * 权限列表
     *
     * @param request 请求参数
     * @return 返回分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取权限列表")
    @PreAuthorize("@ss.hasPermission('system:permission:list')")
    public TableDataResult listPermissions(SysPermissionsListRequest request) {
        Page<SysPermissions> page = sysPermissionsService.listPermissions(request);
        List<SysPermissionsListVo> sysPermissionsListVos = copyListProperties(page, SysPermissionsListVo.class);
        return getTableData(page, sysPermissionsListVos);
    }

}
