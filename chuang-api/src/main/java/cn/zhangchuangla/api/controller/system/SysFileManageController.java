package cn.zhangchuangla.api.controller.system;

import cn.zhangchuangla.common.core.controller.BaseController;
import cn.zhangchuangla.common.core.page.TableDataResult;
import cn.zhangchuangla.system.model.entity.SysFileManagement;
import cn.zhangchuangla.system.model.request.file.manage.SysFileManagementListRequest;
import cn.zhangchuangla.system.model.vo.file.manage.SysFileManagementListVo;
import cn.zhangchuangla.system.service.SysFileManagementService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文件资源管理
 *
 * @author Chuang
 * <p>
 * created on 2025/4/8 16:25
 */
@Tag(name = "文件资源管理")
@RestController
@RequestMapping("/file/manage")
public class SysFileManageController extends BaseController {


    private final SysFileManagementService sysFileManagementService;

    public SysFileManageController(SysFileManagementService sysFileManagementService) {
        this.sysFileManagementService = sysFileManagementService;
    }

    /**
     * 文件资源列表
     *
     * @param request 请求参数
     * @return 文件资源列表
     */
    @GetMapping("/list")
    @Operation(summary = "文件资源列表")
    @PreAuthorize("@auth.hasPermission('system:file-manage:list')")
    public TableDataResult listFileManage(SysFileManagementListRequest request) {
        Page<SysFileManagement> sysFileManagementPage = sysFileManagementService.listFileManage(request);
        List<SysFileManagementListVo> sysFileManagementListVos = copyListProperties(sysFileManagementPage, SysFileManagementListVo.class);
        return getTableData(sysFileManagementPage, sysFileManagementListVos);
    }
}
